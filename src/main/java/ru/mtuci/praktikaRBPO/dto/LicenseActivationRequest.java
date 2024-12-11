package ru.mtuci.praktikaRBPO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LicenseActivationRequest {
    private String key;
    private Long deviceId;
}
