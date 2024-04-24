package com.SXN.Vendor.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface VendorNameService {

    Map<String, Object> getVendorDetails() throws ExecutionException, InterruptedException;
}