package ru.mtuci.praktikaRBPO.ticket;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.Device;
import ru.mtuci.praktikaRBPO.model.License;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
@Service
@RequiredArgsConstructor
public class TicketService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private String HMAC_ALGORITHM = "HmacSHA256";

    private String generateDigitalSignature(License license, Device device) {
        try {

            String rawData = license.getKey()+ license.getApplicationUser() + device.getId() + license.getExpirationDate();

            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), HMAC_ALGORITHM);

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(rawData.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации цифровой подписи", e);
        }
    }


    public Ticket createTicket(License license, Device device){
        Ticket ticket = new Ticket();
        ticket.setServerDate(new Date());
        ticket.setTicketLifetime(license.getLicenseType().getDefaultDuration().longValue() * 30 * 24 * 60 * 60);
        ticket.setActivationDate(license.getActivationDate());
        ticket.setExpirationDate(license.getExpirationDate());;
        ticket.setUserId(device.getApplicationUser() != null ? device.getApplicationUser().getId() : null); ;
        ticket.setDeviceId(device.getMac());;
        ticket.setLicenseBlocked(license.getBlocked() != null ? license.getBlocked().toString() : "null");;
        ticket.setDigitalSignature(generateDigitalSignature(license, device)) ;
        return ticket;
    }
}
