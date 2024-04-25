package com.SXN.Vendor.Controller;

import com.SXN.Vendor.ResponseUtils.ApiResponse;
import com.SXN.Vendor.ResponseUtils.ResponseUtils;
import com.SXN.Vendor.Service.CategoryService;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/VendorList/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //checked - additems
   //http://localhost:8085/api/VendorList/addMenswear?outOfStock=false&name=T-shirt&description=demo&price=1234&pictures=link1,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f&itemId=12&vendorId=vendor&lockinPeriod=15&S=10&L=23&XL=3&XXL=0&M=1&subcategory=Kurtas&Category=Menswear
   //dudes
    //http://localhost:8085/api/VendorList/addMenswear?outOfStock=false&name=T-shirt&description=demo&price=1234&pictures=link1,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f&itemId=12&vendorId=vendor1&lockinPeriod=15&S=10&L=23&XL=3&XXL=0&M=1&subcategory=Shirts&Category=Menswear
    @PostMapping("addMenswear")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveMensDetails(
            @RequestParam String vendorId,
            @RequestParam String Category,
            @RequestParam String subcategory,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String itemId,
            @RequestParam List<String> pictures,
            @RequestParam int price,
            @RequestParam boolean outOfStock,
            @RequestParam(required = false) int S,
            @RequestParam(required = false) int M,
            @RequestParam(required = false) int L,
            @RequestParam(required = false) int XL,
            @RequestParam(required = false) int XXL,
            @RequestParam int lockinPeriod) {
        try {
            // Convert lockinStart String to Timestamp
          /*  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date startDate = dateFormat.parse(lockinStart);
             com.google.cloud.Timestamp startTimestamp = com.google.cloud.Timestamp.of(startDate);

*/
            Timestamp currentTimestamp = Timestamp.ofTimeMicroseconds(Instant.now().getEpochSecond());


            // Convert Date to com.google.cloud.Timestamp

            Map<String, Integer> size = new HashMap<>();
            size.put("S", S);
            size.put("M", M);
            size.put("L", L);
            size.put("XL", XL);
            size.put("XXL", XXL);

            Map<String, Object> savedCategory = categoryService.saveCategoryDetails(vendorId,Category,subcategory,name,description, itemId, pictures, price, size, outOfStock, lockinPeriod);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(savedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to add category: " + e.getMessage()));
        }
    }
    //http://localhost:8085/api/VendorList/addWomenswear?outOfStock=false&name=SilkSaree&description=demo&price=1234&pictures=link1,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f&itemId=w12&vendorId=vendor&lockinPeriod=15&S=10&L=23&XL=3&XXL=0&M=1&subcategory=Sarees&Category=Womenswear
    @PostMapping("addWomenswear")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveWomensDetails(
            @RequestParam String vendorId,
            @RequestParam String Category,
            @RequestParam String subcategory,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) String itemId,
            @RequestParam List<String> pictures,
            @RequestParam int price,
            @RequestParam boolean outOfStock,
            @RequestParam(required = false) int S,
            @RequestParam(required = false) int M,
            @RequestParam(required = false) int L,
            @RequestParam(required = false) int XL,
            @RequestParam(required = false) int XXL,
            @RequestParam int lockinPeriod) {
        try {

            // Convert Date to com.google.cloud.Timestamp

            Map<String, Integer> size = new HashMap<>();
            size.put("S", S);
            size.put("M", M);
            size.put("L", L);
            size.put("XL", XL);
            size.put("XXL", XXL);

            Map<String, Object> savedCategory = categoryService.saveCategoryDetails(vendorId,Category,subcategory,name,description, itemId, pictures, price, size, outOfStock, lockinPeriod);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(savedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to add category: " + e.getMessage()));
        }
    }

    //http://localhost:8085/api/VendorList/addKidswear?outOfStock=false&name=kido&description=demo&price=1234&pictures=link1,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f&vendorId=vendor&lockinPeriod=15&S=10&L=23&XL=3&XXL=0&M=1&Category=Kidswear&itemId=k01&subcategory=Boy
    @PostMapping("addKidswear")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveKidsDetails(
            @RequestParam String vendorId,
            @RequestParam String Category,
            @RequestParam String subcategory,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) String itemId,
            @RequestParam List<String> pictures,
            @RequestParam int price,
            @RequestParam boolean outOfStock,
            @RequestParam(required = false) int S,
            @RequestParam(required = false) int M,
            @RequestParam(required = false) int L,
            @RequestParam(required = false) int XL,
            @RequestParam(required = false) int XXL,
            @RequestParam int lockinPeriod) {
        try {
            // Convert Date to com.google.cloud.Timestamp

            Map<String, Integer> size = new HashMap<>();
            size.put("S", S);
            size.put("M", M);
            size.put("L", L);
            size.put("XL", XL);
            size.put("XXL", XXL);

            Map<String, Object> savedCategory = categoryService.saveCategoryDetails(vendorId,Category,subcategory,description,name, itemId, pictures, price, size, outOfStock, lockinPeriod);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(savedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to add category: " + e.getMessage()));
        }
    }
    //checked----
    //https://vendor-1-vfyh.onrender.com/api/VendorList/getcatalogue?vendorId=vendor1
    //http://localhost:8085/api/VendorList/getcatalogue?vendorId=vendor
    //dudes
    //http://localhost:8085/api/VendorList/getcatalogue?vendorId=vendor1
    @GetMapping("getcatalogue")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getCatalogue(@RequestParam String vendorId) {
        try {
            List<Map<String, Object>> catalogue = categoryService.getCatalogue(vendorId);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(catalogue));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to fetch catalogue: " + e.getMessage()));
        }
    }

    //delete product when stock == true ----------------------------checked
    //http://localhost:8085/api/VendorList/deleteOutOfStockItems?vendorId=vendor&Category=Womenswear&subcategory=Sarees&itemId=22
    @PostMapping("deleteOutOfStockItems")
    public ResponseEntity<ApiResponse<String>> deleteOutOfStockItem(@RequestParam String vendorId,
                                                                    @RequestParam String Category,
                                                                    @RequestParam String subcategory,
                                                                    @RequestParam String itemId) {
        try {
            // Check if item is out of stock before deletion
            if (categoryService.isOutOfStock(vendorId, Category, subcategory, itemId)) {
                categoryService.deleteOutOfStockItems(vendorId, Category, subcategory, itemId);
                return ResponseEntity.ok(ResponseUtils.createOkResponse("ItemId " + itemId + " is deleted successfully from " + Category));
            } else {
                // Item is not out of stock, so don't delete
                return ResponseEntity.ok(ResponseUtils.createOkResponse("Item with itemId: " + itemId + " is not out of stock, deletion skipped."));
            }
        }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace(); // Handle the exception properly
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Error occurred while deleting item with itemId: " + itemId));
        }
    }

    //update Products -----------------------------------error
    //http://localhost:8085/api/VendorList/updateItem?vendorId=vendor&Category=Menswear&subcategory=Shirts&itemId=22&Description=12345qwert&price=0000&M=000&pictures=1,2
    @PostMapping("updateItem")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateItem(
            @RequestParam String vendorId,
            @RequestParam String Category,
            @RequestParam String subcategory,
            @RequestParam String itemId,
            @RequestParam (required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> pictures,
            @RequestParam(required = false) int price,
            @RequestParam(required = false) int  S,
            @RequestParam(required = false) int M,
            @RequestParam(required = false) int L,
            @RequestParam(required = false) int XL,
            @RequestParam(required = false) int XXL) {
        try {

            Map<String, Integer> size = new HashMap<>();
            size.put("S", S);
            size.put("M", M);
            size.put("L", L);
            size.put("XL", XL);
            size.put("XXL", XXL);
            Map<String, Object> updateitem = categoryService.updateItem(vendorId, Category, subcategory, itemId, name, description, pictures, price, size);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(updateitem));
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to update the item: " + e.getMessage()));
        }
    }

    //pending orders -------------------------------------

    //servicekey - duds ----checked
    //http://localhost:8085/api/VendorList/pendingOrders?vendorId=QY7AhEbn2kZ9IWVPo7Il2wAsHZj1
    //http://localhost:8085/api/VendorList/pendingOrders?vendorId=vendor
    @GetMapping("pendingOrders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPendingOrders(@RequestParam String vendorId) {
        try {
            List<Map<String, Object>> pendingOrders = categoryService.getPending(vendorId);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(pendingOrders));
        } catch (ExecutionException | InterruptedException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to fetch Pending Orders: " + e.getMessage()));
        }
    }

    //service key - duds ---- checked
    //http://localhost:8085/api/VendorList/completedOrders?vendorId=QY7AhEbn2kZ9IWVPo7Il2wAsHZj1
    @GetMapping("completedOrders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getcompletedOrders(@RequestParam String vendorId) {
        try {
            List<Map<String, Object>> pendingOrders = categoryService.getCompletedOrders(vendorId);
            return ResponseEntity.ok(ResponseUtils.createOkResponse(pendingOrders));
        } catch (ExecutionException | InterruptedException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to fetch Pending Orders: " + e.getMessage()));
        }
    }

    //checked
    //http://localhost:8085/api/VendorList/getitem?vendorId=vendor&itemId=w11
    //duds
    //http://localhost:8085/api/VendorList/getitem?vendorId=vendor1&itemId=tm22
    @GetMapping("getitem")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getItemDetailsByVendorId(
            @RequestParam String vendorId,
            @RequestParam String itemId) {
        try {
            // Call the service method to fetch item details
            Map<String, Object> itemDetails = categoryService.getItemDetailsByVendorId(vendorId, itemId);

            if (itemDetails != null) {
                // Return the item details if found
                return ResponseEntity.ok(ResponseUtils.createOkResponse(itemDetails));
            } else {
                // Return a not found response if item details are not found
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Failed to fetch item details: " + e.getMessage()));
        }
    }




/*


    @PostMapping("/updateUnits")
    public ResponseEntity<ApiResponse<String>> updateUnits(
            @RequestParam String vendorId,
            @RequestParam String categoryName,
            @RequestParam String itemId,
            @RequestParam int Units) {

        try {
            int updatedUnits = categoryService.updateUnits(vendorId, categoryName, itemId, Units);
            return ResponseEntity.ok(ResponseUtils.createOkResponse("Units updated successfully. New units: " + updatedUnits));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.createErrorResponse("Error updating units of an item: " + e.getMessage()));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.createErrorResponse("Error updating units of an item: " + e.getMessage()));
        }
    }

    @PostMapping("/updateOutOfStock")
    public ResponseEntity<ApiResponse<String>> updateOutOfStock(
            @RequestParam String vendorId,
            @RequestParam String categoryName,
            @RequestParam String itemId) {

        try {
            boolean outOfStock = categoryService.updateOutOfStock(vendorId, categoryName, itemId);
            String message = outOfStock ? "Out of stock." : "In stock.";
            ApiResponse<String> response = ResponseUtils.createOkResponse(String.valueOf(outOfStock));
            response.setMessage(message);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<String> errorResponse = ResponseUtils.createErrorResponse("Error updating outOfStock field of an item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }*/
}
