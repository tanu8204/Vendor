package com.SXN.Vendor.ResponseUtils;

import com.SXN.Vendor.Entity.VendorIdDetails;
import org.springframework.http.HttpStatus;

public class ResponseUtils {
    public static <T> ApiResponse<T> createOkResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setMessage("OK");
        response.setError(null);
        response.setStatus("success");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }


        // Adjusted createErrorResponse method to accept a generic data type
        public static <T> ApiResponse<T> createErrorResponse(String errorMessage) {
            ApiResponse<T> errorResponse = new ApiResponse<>();
            errorResponse.setData(null);
            errorResponse.setMessage("Error occurred: " + errorMessage);
            errorResponse.setError(errorMessage);
            errorResponse.setStatus("error");
            errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return errorResponse;
        }

}
