package com.SXN.Vendor.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import javax.persistence.Entity;
import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VendorIdDetails {

        @Id //from data jpa dependency
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private String vendorId = UUID.randomUUID().toString();
        private String gstNo;
        private String address;
        private String phno;
        private String regNo;
        private String vendorName;
        private String onBoarding;
        private int isActive;
        private Map<String, Double> location = new HashMap<>();
        private String vendorAndroidVersion;
        private String vendorIOSVersion;

        @PrePersist
        @PreUpdate
        private void ensureIsActiveValid() {
                isActive = (isActive == 0) ? 0 : 1;
        }
}