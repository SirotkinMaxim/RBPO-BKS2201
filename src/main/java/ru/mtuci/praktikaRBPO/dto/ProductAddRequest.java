package ru.mtuci.praktikaRBPO.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductAddRequest {
    String name;
    Boolean blocked;
}
