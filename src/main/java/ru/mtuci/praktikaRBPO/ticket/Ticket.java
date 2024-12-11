package ru.mtuci.praktikaRBPO.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.License;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Data
public class Ticket {

    private Date serverDate;
    private Long ticketLifetime;
    private Date activationDate;
    private Date expirationDate;
    private Long userId;
    private String deviceId;
    private String licenseBlocked;
    private String digitalSignature;
    @JsonIgnore
    private License license;
    @JsonIgnore
    private Device device;




}


