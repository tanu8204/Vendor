package com.SXN.Vendor.Service;

import com.SXN.Vendor.Entity.VendorIdDetails;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface VendorService {
    String saveVendor(VendorIdDetails vendor) throws ExecutionException, InterruptedException;

    //vendors name geo location and vendors document ---------------------------------------
    Map<String, Object> login(String phoneNumber) throws ExecutionException, InterruptedException;

    Map<String, Object> updateProfile(String vendorId, String vendorName, String phoneNo, Map<String, Double> location, String address);
}