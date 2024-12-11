package ru.mtuci.praktikaRBPO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LicenseRequest {
    private Long productId;
    private Long ownerId;
    private Long licenseTypeId;
    private Integer maxDevice;
}
