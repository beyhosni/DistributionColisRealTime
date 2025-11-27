import api from './api'

export interface RouteRequest {
  courierId: number
  zoneId?: number
  routeDate: string
  startTime?: string
  endTime?: string
  notes?: string
}

export interface TaskRequest {
  orderId: number
  taskOrder: number
}

export interface DeliveryRouteResponse {
  id: number
  courierId: number
  courierName: string
  zoneId?: number
  zoneName?: string
  routeDate: string
  status: string
  startTime?: string
  endTime?: string
  notes?: string
  createdAt: string
  updatedAt: string
  tasks: Array<{
    id: number
    orderId: number
    trackingNumber: string
    recipientName: string
    recipientAddress: string
    taskOrder: number
    status: string
    plannedArrivalTime?: string
    actualArrivalTime?: string
    notes?: string
  }>
}

export const routingService = {
  // Créer une nouvelle tournée
  async createRoute(routeData: RouteRequest): Promise<DeliveryRouteResponse> {
    const response = await api.post('/routes', routeData)
    return response.data
  },

  // Obtenir toutes les tournées (selon le rôle)
  async getAllRoutes(): Promise<DeliveryRouteResponse[]> {
    const response = await api.get('/routes')
    return response.data
  },

  // Obtenir une tournée par son ID
  async getRouteById(id: number): Promise<DeliveryRouteResponse> {
    const response = await api.get(`/routes/${id}`)
    return response.data
  },

  // Mettre à jour une tournée
  async updateRoute(id: number, routeData: Partial<RouteRequest>): Promise<DeliveryRouteResponse> {
    const response = await api.put(`/routes/${id}`, routeData)
    return response.data
  },

  // Supprimer une tournée
  async deleteRoute(id: number): Promise<void> {
    await api.delete(`/routes/${id}`)
  },

  // Optimiser une tournée
  async optimizeRoute(id: number): Promise<DeliveryRouteResponse> {
    const response = await api.post(`/routes/${id}/optimize`)
    return response.data
  },

  // Ajouter une tâche à une tournée
  async addTaskToRoute(routeId: number, orderId: number, taskOrder: number): Promise<DeliveryRouteResponse> {
    const response = await api.post(`/routes/${routeId}/tasks`, null, {
      params: { orderId, taskOrder }
    })
    return response.data
  },

  // Mettre à jour le statut d'une tâche
  async updateTaskStatus(routeId: number, taskId: number, status: string, notes?: string): Promise<DeliveryRouteResponse> {
    const response = await api.put(`/routes/${routeId}/tasks/${taskId}`, null, {
      params: { status, notes }
    })
    return response.data
  },

  // Obtenir les tournées d'un livreur pour une date
  async getRoutesForCourier(courierId: number, date: string): Promise<DeliveryRouteResponse[]> {
    const response = await api.get(`/routes/courier/${courierId}/date/${date}`)
    return response.data
  },

  // Obtenir les tournées pour une date
  async getRoutesByDate(date: string): Promise<DeliveryRouteResponse[]> {
    const response = await api.get(`/routes/date/${date}`)
    return response.data
  }
}
