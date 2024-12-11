package ru.mtuci.praktikaRBPO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LicenseTypeAddRequest {
    String name;
    Integer defaultDuration;
    String description;
}
