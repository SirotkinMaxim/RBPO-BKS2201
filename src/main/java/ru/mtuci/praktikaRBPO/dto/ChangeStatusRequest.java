package ru.mtuci.praktikaRBPO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChangeStatusRequest {
    Long licenseId;
    boolean isBlocked;

}
