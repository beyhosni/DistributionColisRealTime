package com.distribution.colis.model.dto.request;

import com.distribution.colis.model.entity.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentOrderRequest {

    private Long parcelId; // Optionnel, si null un nouveau colis sera créé

    @NotNull
    @DecimalMin(value = "0.01", message = "Le poids doit être supérieur à 0")
    private BigDecimal weight;

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;

    private String contentDescription;

    private BigDecimal declaredValue;

    @NotBlank(message = "Le nom du destinataire est obligatoire")
    private String recipientName;

    private String recipientPhone;

    @NotBlank(message = "L'adresse du destinataire est obligatoire")
    private String recipientAddressLine1;

    private String recipientAddressLine2;

    @NotBlank(message = "La ville du destinataire est obligatoire")
    private String recipientCity;

    @NotBlank(message = "Le code postal du destinataire est obligatoire")
    private String recipientPostalCode;

    private String recipientCountry;

    @NotNull
    private ServiceType serviceType;

    private Boolean insurance = false;

    private Boolean signatureRequired = false;
}
