package com.SXN.Vendor.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService{


    private static final String LIST_COLLECTION_NAME = "Vendor's List";
    private static final String CATALOGUE_COLLECTION_NAME = "Catalogue";


    @Autowired
    private VendorService vendorService;

    //upload products ------------------------------------------ checked
    //http://localhost:8085/api/VendorList/addCategoryDetails?outOfStock=false&description=demo&price=1234&units=19&pictures=https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f&itemId=11&vendorId=vendor&lockinPeriod=15&S=10&L=23&XL=3&XXL=0&M=1&subcategory=Kurtas
   //http://localhost:8085/api/VendorList/addCategoryDetails?outOfStock=false&description=demo&price=1234&units=19&itemId=33&vendorId=vendor&lockinPeriod=15&S=10&L=23&XL=3&XXL=0&M=1&pictures=https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f,https://firebasestorage.googleapis.com/v0/b/duds-68a6d.appspot.com/o/pic1.jpg?alt=media%26token=452ba8c3-928b-490e-87e6-d567419bbf5f&subcategory=Shirts&Category=Menswear
    @Override
    public Map<String, Object> saveCategoryDetails(String vendorId, String Category, String subcategory, String name, String description, String itemId,
                                                   List<String> pictures, int price,
                                                   Map<String, Integer> size, boolean outOfStock,
                                                   int lockinPeriod)
            throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirestoreClient.getFirestore();

        Map<String, Object> savedCategoryData = new HashMap<>();


        try {
            if (itemId == null || itemId.isEmpty()) {
                itemId = UUID.randomUUID().toString();
            }

            // Create a map for the item details
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("vid", vendorId);
            itemDetails.put("Name", name);
            itemDetails.put("Description", description);
            itemDetails.put("Images", pictures != null ? pictures : new ArrayList<>(4));
            itemDetails.put("Price", price);
            itemDetails.put("Size", size);
            itemDetails.put("outOfStock", outOfStock);
            itemDetails.put("Category", Category);
            itemDetails.put("subCategory", subcategory);
            itemDetails.put("lockinPeriod", lockinPeriod);

            // Get the current timestamp in seconds
            long currentTimestampSeconds = Instant.now().getEpochSecond();

            // Convert current timestamp to milliseconds
            long lockinStartMillis = currentTimestampSeconds * 1000;

            itemDetails.put("lockinStart", lockinStartMillis);


            // Saving items in subcategory
            DocumentReference categoryDocRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                    .document(vendorId)
                    .collection(Category)
                    .document(subcategory);

            categoryDocRef.update(itemId, itemDetails);

            // Fetch existing articles list
            DocumentSnapshot categoryDoc = categoryDocRef.get().get();
            List<String> existingArticles = (List<String>) categoryDoc.get("articles");

            // Append new itemId to the existing articles list
            if (existingArticles == null) {
                existingArticles = new ArrayList<>();
            }
            existingArticles.add(itemId);

            // Update the articles field with the updated list
            categoryDocRef.update("articles", existingArticles);

            // Saving items in catalogue
            DocumentReference catalogueRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                    .document(vendorId)
                    .collection("Catalogue")
                    .document(itemId);
            catalogueRef.set(itemDetails);

            // Saving items in womenswear collections
            DocumentReference collectionRef = dbFirestore.collection(subcategory)
                    .document(itemId);
            collectionRef.set(itemDetails);

            // Directly put item details under "data" key
            savedCategoryData.putAll(itemDetails);

            return savedCategoryData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save category details: " + e.getMessage(), e);
        }
    }



    //fetch catalogue----------------------------------
    @Override
    public List<Map<String, Object>> getCatalogue(String vendorId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Reference to the catalogue collection
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = dbFirestore.collection(LIST_COLLECTION_NAME)
                .document(vendorId)
                .collection(CATALOGUE_COLLECTION_NAME)
                .get();

        // Get the documents from the catalogue collection
        QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
        List<Map<String, Object>> catalogue = new ArrayList<>();

        // Iterate through the documents and add them to the catalogue list
        for (DocumentSnapshot document : querySnapshot) {
            Map<String, Object> getCatalogue = new HashMap<>();

            // Add order ID to the pending order
            getCatalogue.put("id", document.getId());

            Map<String, Object> itemDetails = document.getData();
            // Get item detail map from document data
            Map<String, Object> itemDetail = new HashMap<>();
            itemDetail.putAll(itemDetails);


            // Add item detail to the pending order
            getCatalogue.putAll(itemDetail);

            // Add the pending order to the list
            catalogue.add(getCatalogue);
        }

        return catalogue;
    }

    @Override
    public boolean isOutOfStock(String vendorId, String category, String subcategory, String itemId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Reference to the subcategory document
        DocumentReference subcategoryRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                .document(vendorId)
                .collection(category)
                .document(subcategory);

        // Fetch the subcategory document
        ApiFuture<DocumentSnapshot> subcategorySnapshot = subcategoryRef.get();

        // Get the item details (if document exists)
        DocumentSnapshot subcategoryDoc = subcategorySnapshot.get();
        if (subcategoryDoc.exists()) {
            Map<String, Object> itemsMap = subcategoryDoc.getData();
            if (itemsMap != null && itemsMap.containsKey(itemId)) {
                Map<String, Object> itemDetails = (Map<String, Object>) itemsMap.get(itemId);
                if (itemDetails != null) {
                    // Check for "outOfStock" field and return its value (assuming boolean)
                    return (boolean) itemDetails.getOrDefault("outOfStock", false); // return false if "outOfStock" is missing
                }
            }
        }

        // Item not found or document doesn't exist, consider it in stock (can be customized)
        return false;
    }

    //delete product of outOfStock ----------------------------------------
    @Override
    public void deleteOutOfStockItems(String vendorId, String Category, String subcategory, String itemId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Get references to the subcategory document and other relevant documents
        DocumentReference subcategoryRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                .document(vendorId)
                .collection(Category)
                .document(subcategory);
        DocumentReference catalogueRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                .document(vendorId)
                .collection("Catalogue")
                .document(itemId);
        DocumentReference collectionRef = dbFirestore.collection(subcategory)
                .document(itemId);

        // Fetch the subcategory document
        ApiFuture<DocumentSnapshot> subcategorySnapshot = subcategoryRef.get();

        try {
            DocumentSnapshot subcategoryDoc = subcategorySnapshot.get();

            if (subcategoryDoc.exists()) {
                // Retrieve the items map from the subcategory document
                Map<String, Object> itemsMap = subcategoryDoc.getData();

                if (itemsMap != null && itemsMap.containsKey(itemId)) {
                    Map<String, Object> itemDetails = (Map<String, Object>) itemsMap.get(itemId);
                    if (itemDetails != null) {
                        Boolean outOfStock = (Boolean) itemDetails.get("outOfStock");
                        if (outOfStock != null && outOfStock) {
                            // If outOfStock is true, delete the item from all locations
                            subcategoryRef.update(itemId, FieldValue.delete());
                            catalogueRef.delete();
                            collectionRef.delete();

                            // Fetch the articles array from the subcategory document
                            List<String> articles = (List<String>) subcategoryDoc.get("articles");

                            // Remove itemId from the articles array
                            if (articles != null) {
                                articles.remove(itemId);

                                // Update the subcategory document with the modified articles array
                                subcategoryRef.update("articles", articles);
                            }

                            System.out.println("Item with itemId " + itemId + " deleted because it is out of stock.");
                        } else {
                            System.out.println("Item with itemId " + itemId + " is not out of stock.");
                        }
                    }
                } else {
                    System.out.println("Item with itemId " + itemId + " not found in subcategory " + subcategory + ".");
                }
            } else {
                System.out.println("Subcategory " + subcategory + " not found for vendor " + vendorId + ".");
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error occurred: " + e.getMessage());
            throw e;
        }
    }

    //update products -------------------------------------

    @Override
    public Map<String, Object> updateItem(String vendorId, String Category, String subcategory, String itemId, String name, String description,
                                          List<String> pictures, Integer price, Map<String, Integer> size) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        try {
            // Construct a map containing the fields to update
            Map<String, Object> updateData = new HashMap<>();
            if (name != null) updateData.put("Name", description);
            if (description != null) updateData.put("Description", description);
            if (pictures != null) updateData.put("Images", pictures);
            if (price != null) updateData.put("Price", price);
            if (size != null) updateData.put("Size", size);

            // Update the item in Catalogue collection
            DocumentReference catalogueRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                    .document(vendorId)
                    .collection("Catalogue")
                    .document(itemId);
            catalogueRef.set(updateData, SetOptions.merge());

            // Update the item in the subcategory collection
            DocumentReference subcategoryRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                    .document(vendorId)
                    .collection(Category)
                    .document(subcategory);
            subcategoryRef.update(itemId, updateData);

            // Update the item in the corresponding subcategory collection
            DocumentReference collectionRef = dbFirestore.collection(subcategory)
                            .document(itemId);
            collectionRef.update(updateData);

            return updateData;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getPending(String vendorId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve all documents from the "Pending Orders" collection for the specified vendor
        Query query = dbFirestore.collection(LIST_COLLECTION_NAME).document(vendorId)
                .collection("Pending Orders");
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Map<String, Object>> pendingOrdersList = new ArrayList<>();

        // Iterate through the documents in the query snapshot
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            // Create a map to represent the pending order
            Map<String, Object> pendingOrder = new HashMap<>();

            // Add order ID to the pending order
            pendingOrder.put("id", document.getId());

            Map<String, Object> itemDetails = document.getData();
            // Get item detail map from document data
            Map<String, Object> itemDetail = new HashMap<>();
            itemDetail.putAll(itemDetails);


            // Add item detail to the pending order
            pendingOrder.putAll(itemDetail);

            // Add the pending order to the list
            pendingOrdersList.add(pendingOrder);
        }

        return pendingOrdersList;
    }


    @Override
    public List<Map<String, Object>> getCompletedOrders(String vendorId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Retrieve all documents from the "Pending Orders" collection for the specified vendor
        Query query = dbFirestore.collection(LIST_COLLECTION_NAME).document(vendorId)
                .collection("Completed Orders");
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Map<String, Object>> compeleteOrderList = new ArrayList<>();

        // Iterate through the documents in the query snapshot
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            // Create a map to hold the document data
            Map<String, Object> compeleteOrder = new HashMap<>();

            // Add document id to the document data
            compeleteOrder.put("id", document.getId());

            // Add document data to the map
            compeleteOrder.putAll(document.getData());
            Map<String, Object> itemDetails = document.getData();
            // Get item detail map from document data
            Map<String, Object> itemDetail = new HashMap<>();
            itemDetail.putAll(itemDetails);
            // Add item detail to the pending order
            compeleteOrder.putAll(itemDetail);
            // Add the pending order to the list
            compeleteOrderList.add(compeleteOrder);
        }
        return compeleteOrderList;
    }




/*
    @Override
    public Map<String, List<Category>> getAllItemsBySubcategory(String vendorId, String categoryName) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference categoryCollectionRef = dbFirestore.collection(COLLECTION_NAME)
                .document(vendorId)
                .collection(categoryName);

        Map<String, List<Category>> itemsBySubcategory = new HashMap<>();

        ApiFuture<QuerySnapshot> querySnapshotApiFuture = categoryCollectionRef.get();
        try {
            QuerySnapshot querySnapshot = querySnapshotApiFuture.get();
            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                String subCategory = documentSnapshot.getId();
                List<Category> items = Collections.singletonList(documentSnapshot.toObject(Category.class));
                itemsBySubcategory.put(subCategory, items);
            }
            return itemsBySubcategory;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error retrieving items by subcategory: {}", e.getMessage(), e);
            throw e;
        }
    }*/

    @Override
    public Map<String,Object> getItemDetailsByVendorId(String vendorId, String itemId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        DocumentReference catalogueRef = dbFirestore.collection(LIST_COLLECTION_NAME)
                .document(vendorId)
                .collection("Catalogue")
                .document(itemId);
        // Get the document snapshot from Firestore
        DocumentSnapshot documentSnapshot = catalogueRef.get().get();

        // Check if the document exists
        if (documentSnapshot.exists()) {
            // Convert document data to a map
            return documentSnapshot.getData();
        } else {
            // Document does not exist
            return null;
        }

    }

    /*@Override
    public int updateUnits(String vendorId, String categoryName, String itemId, int updatedUnits) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Get a reference to the category document
        DocumentReference categoryDocRef = dbFirestore.collection(COLLECTION_NAME)
                .document(vendorId)
                .collection(categoryName)
                .document(itemId);

        try {
            // Retrieve the current category document
            DocumentSnapshot documentSnapshot = categoryDocRef.get().get();
            if (documentSnapshot.exists()) {
                // Extract the existing category data
                Category existingCategory = documentSnapshot.toObject(Category.class);
                int currentUnits = existingCategory.getUnits();

                // Ensure sufficient units are available for the update
                if (currentUnits < updatedUnits) {
                    throw new IllegalArgumentException("Insufficient units available for item ID " + itemId);
                }

                // Update the unit count
                int newUnits = currentUnits - updatedUnits;
                existingCategory.setUnits(newUnits);

                // Update the category document in Firestore
                ApiFuture<WriteResult> updateFuture = categoryDocRef.set(existingCategory);
                updateFuture.get(); // Wait for the update operation to complete

                return newUnits;
            } else {
                throw new IllegalArgumentException("Category document with ID " + itemId + " does not exist");
            }
        } catch (FirestoreException e) {
            throw new RuntimeException("Error updating units: " + e.getMessage(), e);
        }
        }
     */


   /* @Override
    public boolean updateOutOfStock(String vendorId, String categoryName, String itemId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Get a reference to the category document
        DocumentReference categoryDocRef = dbFirestore.collection(COLLECTION_NAME)
                .document(vendorId)
                .collection(categoryName)
                .document(itemId);

        // Get the current units in the category document
        ApiFuture<DocumentSnapshot> future = categoryDocRef.get();
        DocumentSnapshot document;
        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error getting category document: " + e.getMessage(), e);
        }

        int currentUnits = document.getLong("units").intValue();

        // Update the outOfStock field
        try {
            categoryDocRef.update("outOfStock", currentUnits == 0);
        } catch (FirestoreException e) {
            throw new RuntimeException("Error updating outOfStock field: " + e.getMessage(), e);
        }

        return currentUnits == 0;
    }


*/
}





