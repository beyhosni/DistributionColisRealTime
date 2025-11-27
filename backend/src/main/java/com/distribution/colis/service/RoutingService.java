package com.distribution.colis.service;

import com.distribution.colis.model.dto.request.DeliveryRouteRequest;
import com.distribution.colis.model.dto.response.DeliveryRouteResponse;
import com.distribution.colis.model.entity.*;
import com.distribution.colis.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoutingService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryTaskRepository deliveryTaskRepository;
    private final UserRepository userRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;

    public RoutingService(DeliveryRouteRepository deliveryRouteRepository,
                         DeliveryTaskRepository deliveryTaskRepository,
                         UserRepository userRepository,
                         DeliveryZoneRepository deliveryZoneRepository,
                         ShipmentOrderRepository shipmentOrderRepository) {
        this.deliveryRouteRepository = deliveryRouteRepository;
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.userRepository = userRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.shipmentOrderRepository = shipmentOrderRepository;
    }

    @Transactional
    public DeliveryRouteResponse createRoute(DeliveryRouteRequest request) {
        // Vérifier que le livreur existe
        User courier = userRepository.findById(request.getCourierId())
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        // Vérifier que la zone existe si spécifiée
        DeliveryZone zone = null;
        if (request.getZoneId() != null) {
            zone = deliveryZoneRepository.findById(request.getZoneId())
                    .orElseThrow(() -> new RuntimeException("Delivery zone not found"));
        }

        // Vérifier qu'il n'y a pas déjà une tournée pour ce livreur à cette date
        Optional<DeliveryRoute> existingRoute = deliveryRouteRepository.findByCourierAndDate(
                request.getCourierId(), request.getRouteDate());
        if (existingRoute.isPresent()) {
            throw new RuntimeException("A route already exists for this courier on this date");
        }

        // Créer la tournée
        DeliveryRoute route = new DeliveryRoute();
        route.setCourier(courier);
        route.setZone(zone);
        route.setRouteDate(request.getRouteDate());
        route.setStatus(RouteStatus.PLANNED);
        route.setStartTime(request.getStartTime());
        route.setEndTime(request.getEndTime());
        route.setNotes(request.getNotes());

        route = deliveryRouteRepository.save(route);

        return convertToResponse(route);
    }

    @Transactional
    public DeliveryRouteResponse addTaskToRoute(Long routeId, Long orderId, Integer taskOrder) {
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        ShipmentOrder order = shipmentOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Vérifier que la commande n'est pas déjà assignée à une autre tournée
        List<DeliveryTask> existingTasks = deliveryTaskRepository.findByOrderId(orderId);
        if (!existingTasks.isEmpty()) {
            throw new RuntimeException("Order is already assigned to a route");
        }

        // Créer la tâche de livraison
        DeliveryTask task = new DeliveryTask();
        task.setRoute(route);
        task.setOrder(order);
        task.setTaskOrder(taskOrder);
        task.setStatus(TaskStatus.PENDING);

        deliveryTaskRepository.save(task);

        // Mettre à jour le statut de la commande
        order.setStatus(OrderStatus.ASSIGNEE);
        shipmentOrderRepository.save(order);

        return convertToResponse(route);
    }

    @Transactional
    public DeliveryRouteResponse updateTaskStatus(Long taskId, TaskStatus newStatus, String notes) {
        DeliveryTask task = deliveryTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        task.setNotes(notes);

        if (newStatus == TaskStatus.IN_PROGRESS) {
            task.setActualArrivalTime(LocalTime.now());
        }

        deliveryTaskRepository.save(task);

        // Mettre à jour le statut de la commande associée
        ShipmentOrder order = task.getOrder();
        switch (newStatus) {
            case IN_PROGRESS:
                order.setStatus(OrderStatus.EN_COURS);
                break;
            case COMPLETED:
                order.setStatus(OrderStatus.LIVREE);
                break;
            case FAILED:
                order.setStatus(OrderStatus.RETOUR);
                break;
        }
        shipmentOrderRepository.save(order);

        // Retourner la tournée mise à jour
        DeliveryRoute route = deliveryRouteRepository.findById(task.getRoute().getId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        return convertToResponse(route);
    }

    public DeliveryRouteResponse getRouteById(Long routeId) {
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        return convertToResponse(route);
    }

    public List<DeliveryRouteResponse> getRoutesForCourier(Long courierId, LocalDate date) {
        List<DeliveryRoute> routes;
        if (date != null) {
            routes = deliveryRouteRepository.findByCourierIdAndRouteDate(courierId, date);
        } else {
            routes = deliveryRouteRepository.findByCourierId(courierId);
        }
        return routes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DeliveryRouteResponse> getRoutesByDate(LocalDate date) {
        List<DeliveryRoute> routes = deliveryRouteRepository.findByRouteDate(date);
        return routes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryRouteResponse optimizeRoute(Long routeId) {
        // Simple optimization: reorder tasks based on postal codes
        DeliveryRoute route = deliveryRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        List<DeliveryTask> tasks = deliveryTaskRepository.findByRouteIdOrderByTaskOrder(routeId);

        // This is a simplified optimization - in a real application, you would use
        // a more sophisticated algorithm or an external service
        tasks.sort((t1, t2) -> {
            String postalCode1 = t1.getOrder().getRecipientPostalCode();
            String postalCode2 = t2.getOrder().getRecipientPostalCode();
            return postalCode1.compareTo(postalCode2);
        });

        // Update task order
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setTaskOrder(i + 1);
            deliveryTaskRepository.save(tasks.get(i));
        }

        return convertToResponse(route);
    }

    private DeliveryRouteResponse convertToResponse(DeliveryRoute route) {
        DeliveryRouteResponse response = new DeliveryRouteResponse();
        response.setId(route.getId());
        response.setCourierId(route.getCourier().getId());
        response.setCourierName(route.getCourier().getFirstName() + " " + route.getCourier().getLastName());
        response.setZoneId(route.getZone() != null ? route.getZone().getId() : null);
        response.setZoneName(route.getZone() != null ? route.getZone().getName() : null);
        response.setRouteDate(route.getRouteDate());
        response.setStatus(route.getStatus());
        response.setStartTime(route.getStartTime());
        response.setEndTime(route.getEndTime());
        response.setNotes(route.getNotes());
        response.setCreatedAt(route.getCreatedAt());
        response.setUpdatedAt(route.getUpdatedAt());

        // Récupérer les tâches associées
        List<DeliveryTask> tasks = deliveryTaskRepository.findByRouteIdOrderByTaskOrder(route.getId());
        response.setTasks(tasks.stream()
                .map(this::convertTaskToResponse)
                .collect(Collectors.toList()));

        return response;
    }

    private Object convertTaskToResponse(DeliveryTask task) {
        // Créer une réponse de tâche
        Object taskResponse = new Object() {
            public final Long id = task.getId();
            public final Long orderId = task.getOrder().getId();
            public final String trackingNumber = task.getOrder().getParcel().getTrackingNumber();
            public final String recipientName = task.getOrder().getRecipientName();
            public final String recipientAddress = task.getOrder().getRecipientAddressLine1() + 
                    ", " + task.getOrder().getRecipientPostalCode() + " " + task.getOrder().getRecipientCity();
            public final Integer taskOrder = task.getTaskOrder();
            public final String status = task.getStatus().toString();
            public final LocalTime plannedArrivalTime = task.getPlannedArrivalTime();
            public final LocalTime actualArrivalTime = task.getActualArrivalTime();
            public final String notes = task.getNotes();
        };
        return taskResponse;
    }
}
