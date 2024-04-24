package com.SXN.Vendor.ResponseUtils;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private String error;
    private String status;
    private int statusCode;

}

