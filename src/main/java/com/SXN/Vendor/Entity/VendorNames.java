package com.SXN.Vendor.Entity;

import lombok.*;
import javax.persistence.Entity;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VendorNames {
        private String vendorId;
        private String vendorName;
        private String phoneNo ;
        private String location;

}

