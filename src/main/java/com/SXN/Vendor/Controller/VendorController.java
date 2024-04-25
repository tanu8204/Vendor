package com.SXN.Vendor.Controller;

import com.SXN.Vendor.Entity.VendorIdDetails;
import com.SXN.Vendor.ResponseUtils.ApiResponse;
import com.SXN.Vendor.ResponseUtils.ResponseUtils;
import com.SXN.Vendor.Service.VendorService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;


@Slf4j
@RestController
@RequestMapping("/api/VendorList/")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    // checked -------version , location , onboarding ?
    //http://localhost:8085/api/VendorList/registration?vendorId=vendor1&vendorName=ExampleVendor&gst_No=1234567890&address=ExampleAddress&phoneNumber=1234567890&regNo=ABC123&isActive=1&latitude=37.7749&longitude=-122.4194
    // https://vendor-npp2.onrender.com/api/VendorList/registration?vendorId=vendor1&vendorName=ExampleVendor&gst_No=1234567890&address=ExampleAddress&phoneNumber=1234567890&regNo=ABC123&onboarding=2024-04-07&isActive=1&latitude=37.7749&longitude=-122.4194
    //dudes----
    //http://localhost:8085/api/VendorList/registration?vendorId=vendor1&vendorName=ExampleVendor&gst_No=1234567890&address=ExampleAddress&phoneNumber=123456789&regNo=ABC123&isActive=1&latitude=37.7749&longitude=-122.4194
    @PostMapping("registration")
    public ResponseEntity<ApiResponse<VendorIdDetails>> registerVendor(
            @RequestParam(required = false) String vendorId,
            @RequestParam String vendorName,
            @RequestParam String gst_No,
            @RequestParam String address,
            @RequestParam String phoneNumber,
            @RequestParam String regNo,
            @RequestParam(required = false, defaultValue = "1") int isActive,
            @RequestParam(required = false) String latitude,
            @RequestParam(required = false) String longitude) {
        try {
            // Check if vendor with the provided phone number already exists
            Map<String, Object> existingVendor = vendorService.login(phoneNumber);
            if (existingVendor != null) {
                // Vendor with the same phone number already exists
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ResponseUtils.createErrorResponse("Vendor with the provided phone number is already registered."));
            }

            // No existing vendor found, proceed with registration
            VendorIdDetails vendor = new VendorIdDetails();
            if (vendorId != null) {
                vendor.setVendorId(vendorId);
            } else {
                vendor.setVendorId(UUID.randomUUID().toString());
            }
            vendor.setVendorName(vendorName);
            vendor.setGstNo(gst_No);
            vendor.setAddress(address);
            vendor.setPhno(phoneNumber);
            vendor.setRegNo(regNo);
            vendor.setIsActive(isActive);

            Map<String, Double> location = new HashMap<>();
            if (latitude != null && longitude != null) {
                location.put("latitude", Double.parseDouble(latitude));
                location.put("longitude", Double.parseDouble(longitude));
            }
            vendor.setLocation(location);

            // Set onboarding to current timestamp
            vendor.setOnBoarding(String.valueOf(Instant.now().toEpochMilli()));

            String savedVendor = vendorService.saveVendor(vendor);

            log.info("Vendor registration successful for ID: {}", vendor.getVendorId());

            return ResponseEntity.ok(ResponseUtils.createOkResponse(vendor));
        } catch (NumberFormatException | ExecutionException | InterruptedException e) {
            log.error("Error registering vendor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Error registering vendor: " + e.getMessage()));
        }
    }



    //http://localhost:8085/api/VendorList/LogIn?vendorId=vendor1
    //https://vendor-npp2.onrender.com/api/VendorList/LogIn?vendorId=vendor1
//    @GetMapping("LogIn")
//    public ResponseEntity<ApiResponse<VendorIdDetails>> login(@RequestParam String phoneNumber,
//                                                                         @RequestParam String type,
//                                                                         @RequestParam(required = false) String vendorId) {
//        try {
//            // Check if the provided type is compatible with the latest version
//            if (!isStable(phoneNumber,type, vendorId)) {
//                log.info("Incompatible device type: {}", type);
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(ResponseUtils.createErrorResponse("Incompatible device type"));
//            }
//
//            // Retrieve vendor details based on the phoneNumber
//            VendorIdDetails vendorDetails = vendorService.login(phoneNumber, type, vendorId);
//
//            // If vendor details not found for the provided phoneNumber
//            if (vendorDetails == null) {
//                log.info("PhoneNumber not found: {}", phoneNumber);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ResponseUtils.createErrorResponse("Vendor not found"));
//            }
//
//            log.info("Retrieved vendor details for phoneNumber: {}", phoneNumber);
//            ApiResponse<VendorIdDetails> response = ResponseUtils.createOkResponse(vendorDetails);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error retrieving vendor details: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(ResponseUtils.createErrorResponse("Type variable is missing or incorrect"));
//        }
//    }
//
//    private boolean isStable(String phoneNumber, String type, String vendorId) throws ExecutionException, InterruptedException {
//        // Retrieve the latest version from the appropriate document based on type (android/ios)
//        Firestore dbFirestore = FirestoreClient.getFirestore();
//
//        DocumentReference versionDocRef = dbFirestore.collection("Version").document(type + " Version");
//        ApiFuture<DocumentSnapshot> future = versionDocRef.get();
//        DocumentSnapshot document = future.get();
//
//        if (document.exists()) {
//            // Get the version from the document
//            String latestVersion = document.getString("version");
//            log.info("Latest version for {} is {}", type, latestVersion);
//
//            // Retrieve the vendor details based on vendorId
//            VendorIdDetails vendorDetails = vendorService.login(phoneNumber, type, vendorId);
//            log.info("Retrieved vendor details for vendorId: {}", vendorId);
//
//            // Check if the vendorDetails object is null
//            if (vendorDetails == null) {
//                log.info("Vendor details not found for vendorId: {}", vendorId);
//                return false; // Handle null vendorDetails appropriately
//            }
//
//            if (type.equalsIgnoreCase("Android")) {
//                // Check if the vendor's Android version is the latest version
//                String vendorAndroidVersion = vendorDetails.getVendorAndroidVersion();
//                log.info("Vendor Android version: {}", vendorAndroidVersion);
//                return latestVersion.equals(vendorAndroidVersion);
//            } else if (type.equalsIgnoreCase("IOS")) {
//                // Check if the vendor's iOS version is the latest version
//                String vendorIOSVersion = vendorDetails.getVendorIOSVersion();
//                log.info("Vendor iOS version: {}", vendorIOSVersion);
//                return latestVersion.equals(vendorIOSVersion);
//            } else {
//                // If type is neither android nor ios, return false
//                log.info("Invalid device type: {}", type);
//                return false;
//            }
//        } else {
//            // If the document doesn't exist, return false
//            log.info("Document does not exist for {} Version", type);
//            return false;
//        }
//    }

    //Login-------------------------------------------[type? ]
    //http://localhost:8085/api/VendorList/logIn?phoneNumber=1234567890   checked
    // dudes
    //http://localhost:8085/api/VendorList/logIn?phoneNumber=123456789
    @GetMapping("/logIn")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getlogin(String phoneNumber) {
        try {
            Map<String, Object> vendorDetails = vendorService.login(phoneNumber);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(vendorDetails));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Error retrieving vendor details: " + e.getMessage()));
        }
    }

    // updateProfile -------------------------- incorrect
    //http://localhost:8085/api/VendorList/updateProfile?vendorId=vendor&phoneNumber=1233
    @PostMapping("updateProfile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String vendorId,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        try {

            Map<String,Double> location=new HashMap<>();
            if (latitude != null && longitude != null) {
                location.put("latitude", latitude);
                location.put("longitude", longitude);
            }
            // Update the profile and get the updated fields
            Map<String, Object> updatedFields = vendorService.updateProfile(vendorId, vendorName, phoneNumber, location,address);

            // Return only the updated fields in JSON format
            return ResponseEntity.ok(updatedFields);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return an appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to update profile. Please try again later."));
        }
    }
}