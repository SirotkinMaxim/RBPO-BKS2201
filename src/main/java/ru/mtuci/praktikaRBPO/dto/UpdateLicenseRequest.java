package ru.mtuci.praktikaRBPO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateLicenseRequest {
    private String key;
    private String mac;
}