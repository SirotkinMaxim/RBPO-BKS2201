package ru.mtuci.praktikaRBPO.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="licenses")
public class License {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String key;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private ApplicationUser owner;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private ApplicationUser applicationUser;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private LicenseType licenseType;
    private Integer maxDevices;
    private Date activationDate;
    private Date expirationDate;
    private Boolean blocked;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("license")
    private List<LicenseHistory> licenseHistories;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("license")
    private List<DeviceLicense> deviceLicenses;

    public Boolean getBlocked() {
        return blocked != null ? blocked : false;
    }

}
