package com.SXN.Vendor.Service;

import com.SXN.Vendor.Entity.VendorIdDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class VendorServiceImpl implements VendorService {

    private static final String NAMES_COLLECTION_NAME = "Vendor's Names";
    private static final String LIST_COLLECTION_NAME = "Vendor's List";
    private static final String VENDORS_DOCUMENT_NAME = "Vendors";
    private static final String VENDORS_LOC_DOCUMENT_NAME = "Geolocations";

    //regstration----------------------------------------
    @Override
    public String saveVendor(VendorIdDetails vendor) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(LIST_COLLECTION_NAME)
                .document(vendor.getVendorId().toString()) // Use vendor ID as document ID
                .set(vendor);
        Map<String, Object> savedData = saveVendorDetails();

        return collectionApiFuture.get().getUpdateTime().toString();
    }

    //for save the details in vendors name collecton :
    private Map<String, Object> saveVendorDetails() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference namesCollectionRef = dbFirestore.collection(LIST_COLLECTION_NAME);

        Map<String, Object> savedVendorData = new HashMap<>();
        Map<String, Object> savedVendorLocationData = new HashMap<>();

        // Get all documents from Vendor's Names collection
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = namesCollectionRef.get();
        try {
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                String vendorId = documentSnapshot.getId();
                Map<String, Object> vendorData = documentSnapshot.getData();

                // Construct vendor details map
                Map<String, Object> vendorDetails = new HashMap<>();
                vendorDetails.put("location", vendorData.get("location"));
                vendorDetails.put("phno", vendorData.get("phno"));
                vendorDetails.put("vendorName", vendorData.get("vendorName"));

                // Save the vendor data to Vendor's List collection under the vendorId document
                DocumentReference vendorsDocumentRef = dbFirestore.collection(NAMES_COLLECTION_NAME)
                        .document(VENDORS_DOCUMENT_NAME);
                vendorsDocumentRef.set(Collections.singletonMap(vendorId, vendorDetails), SetOptions.mergeFields(vendorId));

                // Save the vendor location to Vendor's List collection under the vendorId document
                Map<String, Object> vendorLocation = new HashMap<>();
                vendorLocation.put("location", vendorData.get("location"));
                DocumentReference vendorsLocationDocumentRef = dbFirestore.collection(NAMES_COLLECTION_NAME)
                        .document(VENDORS_LOC_DOCUMENT_NAME);
                vendorsLocationDocumentRef.set(Collections.singletonMap(vendorId, vendorLocation), SetOptions.mergeFields(vendorId));

                // Add vendor details to savedVendorData map
                savedVendorData.put(vendorId, vendorDetails);
                savedVendorLocationData.put(vendorId, vendorLocation);

                log.info("Vendor details added to Vendor's Name collection under vendorId: {}", vendorId);
            }
            return Map.of("vendorData", savedVendorData, "vendorLocationData", savedVendorLocationData);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving vendor details from Vendor's Names collection: {}", e.getMessage(), e);
            throw e;
        }
    }

//    //login -----------------------------------------------------------
//    @Override
//    public VendorIdDetails login(String phoneNumber, String type , String vendorId) throws ExecutionException, InterruptedException {
//        Firestore dbFirestore = FirestoreClient.getFirestore();
//
//        // Query to find the document where phoneNumber field matches the provided value
//        Query query = dbFirestore.collection(LIST_COLLECTION_NAME).whereEqualTo("phoneNumber", phoneNumber);
//
//        // Execute the query
//        ApiFuture<QuerySnapshot> querySnapshot = query.get();
//
//        // Get the documents from the query result
//        QuerySnapshot documentSnapshots = querySnapshot.get();
//
//        // Check if any documents match the query
//        if (!documentSnapshots.isEmpty()) {
//            // Retrieve the first document (assuming phoneNumber is unique)
//            DocumentSnapshot document = documentSnapshots.getDocuments().get(0);
//
//            // Convert the document to a VendorIdDetails object
//            VendorIdDetails vendor = document.toObject(VendorIdDetails.class);
//
//            // Log and return the retrieved vendor details
//            log.debug("Retrieved vendor details by phoneNumber {}: {}", phoneNumber, vendor);
//            return vendor;
//        } else {
//            // Log and return null if no document matches the phoneNumber
//            log.info("Vendor with phoneNumber {} does not exist", phoneNumber);
//            return null;
//        }
//    }


    //Login ---------------------------------------[type]
    @Override
    public Map<String, Object> login(String phoneNumber) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Create a query to find documents where the phone number matches
        CollectionReference vendorsCollectionRef = dbFirestore.collection(LIST_COLLECTION_NAME);
        Query query = vendorsCollectionRef.whereEqualTo("phno", phoneNumber);

        // Retrieve documents based on the query
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        // Check if any documents were found
        if (documents.isEmpty()) {
            // No vendor found with the provided phone number
            return null;
        } else {
            // Get the data from the first document (assuming only one vendor per phone number)
            QueryDocumentSnapshot document = documents.get(0);
            return document.getData();
        }
    }



    //updateProfile------------------------------------
//    @Override
//    public VendorIdDetails updateProfile(String vendorId, String vendorName, String phoneNo, Map<String, Double> location, String address) {
//        Firestore dbFirestore = FirestoreClient.getFirestore();
//
//        // Retrieve vendor document reference
//        DocumentReference vendorDocRef = dbFirestore.collection(LIST_COLLECTION_NAME)
//                .document(vendorId);
//
//        // Get the existing vendor details
//        ApiFuture<DocumentSnapshot> future = vendorDocRef.get();
//        DocumentSnapshot document;
//        try {
//            document = future.get();
//            if (document.exists()) {
//                // Get the existing vendor details
//                Map<String, Object> existingVendor = document.getData();
//
//                // Update the fields if they are not null
//                if (vendorName != null) {
//                    existingVendor.put("vendorName", vendorName);
//                }
//                if (phoneNo != null) {
//                    existingVendor.put("phoneNumber", phoneNo);
//                }
//                if (location != null) {
//                    existingVendor.put("location", location);
//                }
//                if (address != null) {
//                    existingVendor.put("address", address);
//                }
//
//                // Perform the update
//                ApiFuture<WriteResult> updateResult = vendorDocRef.set(existingVendor, SetOptions.merge());
//
//                // Block on the result of the update operation
//                updateResult.get();
//                System.out.println("Profile updated successfully.");
//
//                // Return the updated profile
//                VendorIdDetails updatedProfile = new VendorIdDetails();
//                updatedProfile.setVendorId(vendorId);
//                updatedProfile.setVendorName(vendorName != null ? vendorName : (String) existingVendor.get("vendorName"));
//                updatedProfile.setPhoneNumber(phoneNo != null ? phoneNo : (String) existingVendor.get("phoneNumber"));
//                updatedProfile.setLocation(location != null ? location : (Map<String, Double>) existingVendor.get("location"));
//                updatedProfile.setAddress(address != null ? address : (String) existingVendor.get("address"));
//
//                return updatedProfile;
//            } else {
//                System.err.println("Vendor document not found.");
//                return null;
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            System.err.println("Error updating profile: " + e.getMessage());
//            return null;
//        }
//    }
    @Override
    public Map<String, Object> updateProfile(String vendorId, String vendorName, String phoneNumber, Map<String, Double> location, String address) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve vendor document reference
        DocumentReference vendorDocRef = dbFirestore.collection(LIST_COLLECTION_NAME).document(vendorId);

        // Create a map to hold the updated fields
        Map<String, Object> updates = new HashMap<>();

        // Add the fields to update, if they are not null
        if (vendorName != null) {
            updates.put("vendorName", vendorName);
        }
        if (phoneNumber != null) {
            updates.put("phoneNumber", phoneNumber);
        }
        if (location != null) {
            updates.put("location", location);
        }
        if (address != null) {
            updates.put("address", address);
        }

        // Perform the update
        ApiFuture<WriteResult> updateResult = vendorDocRef.update(updates);

        // Block on the result of the update operation
        try {
            updateResult.get();
            System.out.println("Profile updated successfully.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }

        // Return only the updated fields
        return updates;
    }

}