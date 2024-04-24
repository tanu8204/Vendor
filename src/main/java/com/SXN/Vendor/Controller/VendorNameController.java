package com.SXN.Vendor.Controller;
import com.SXN.Vendor.ResponseUtils.ApiResponse;
import com.SXN.Vendor.ResponseUtils.ResponseUtils;
import com.SXN.Vendor.Service.VendorNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/VendorName/")
public class VendorNameController {

    @Autowired
    private VendorNameService vendorNameService;


    //https://vendor-npp2.onrender.com/api/VendorName/getGeoLocation
    @GetMapping("/getGeoLocation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorDetails() {
        try {
            Map<String, Object> vendorDetails = vendorNameService.getVendorDetails();
            return ResponseEntity.ok(ResponseUtils.createOkResponse(vendorDetails));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Error retrieving vendor details: " + e.getMessage()));
        }
    }


    //DATA JSON
    /*@GetMapping("getVendorNameDetails")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorNameDetails() {
        try {
            Map<String, Object> vendorDetails = vendorNameService.getVendorNameDetails();
            log.info("Retrieved vendor name details: {}", vendorDetails);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(vendorDetails));
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error retrieving vendor name details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.createErrorResponse(e.getMessage()));
        }
    }
*/
/*    @PostMapping("saveVendorslocation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveVendorslocation() {
        try {
            Map<String, Object> message = vendorNameService.saveVendorslocation();
            log.info("Vendor location data saved successfully.");
            return ResponseEntity.ok(ResponseUtils.createOkResponse(message));
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error saving vendor names from list: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.createErrorResponse(e.getMessage()));
        }
    }*/

    /*@GetMapping("getVendorLocationDetails")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorLocationDetails() {
        try {
            Map<String, Object> location = vendorNameService.getVendorLocationDetails();
            log.info("Retrieved vendor name details: {}", location);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(location));
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error retrieving vendor name details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtils.createErrorResponse(e.getMessage()));
        }
    }*/
}

