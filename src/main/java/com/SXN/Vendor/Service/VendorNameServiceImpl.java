package com.SXN.Vendor.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class VendorNameServiceImpl implements VendorNameService {

    private static final String NAMES_COLLECTION_NAME = "Vendor's Names";
    private static final String LIST_COLLECTION_NAME = "Vendor's List";
    private static final String VENDORS_DOCUMENT_NAME = "Vendors";
    private static final String VENDORS_LOC_DOCUMENT_NAME="Geolocations";


    @Override
    public Map<String, Object> getVendorDetails() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve vendor location details
        DocumentReference locationDocumentRef = dbFirestore.collection(NAMES_COLLECTION_NAME)
                .document(VENDORS_LOC_DOCUMENT_NAME);
        Map<String, Object> locationDetails = retrieveDocumentData(locationDocumentRef);

        // Combine both sets of details
        Map<String, Object> vendorDetails = new HashMap<>();
       // vendorDetails.put("nameDetails", nameDetails);
        vendorDetails.put("locationDetails", locationDetails);

        return vendorDetails;
    }

    private Map<String, Object> retrieveDocumentData(DocumentReference documentReference)
            throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.getData();
            } else {
                log.info("No vendor details found.");
                return new HashMap<>();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error retrieving vendor details: {}", e.getMessage(), e);
            throw e;
        }
    }

}
