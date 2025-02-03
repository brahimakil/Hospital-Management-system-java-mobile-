package com.example.hospitalmanagementsys.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Map;

public class FirebaseUtils {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    public static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Authentication Methods
    public static void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(listener);
    }

    public static void signUp(String email, String password, OnCompleteListener<AuthResult> listener) {
        if (!email.contains("@") || password.length() < 6) {
            return;
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(listener);
    }

    public static void signOut() {
        auth.signOut();
    }

    // Firestore Methods
    public static void addUserToCollection(String collection, String uid, Map<String, Object> userData,
                                         OnSuccessListener<Void> successListener,
                                         OnFailureListener failureListener) {
        db.collection(collection)
            .document(uid)
            .set(userData)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener);
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static void resetPassword(String email, OnCompleteListener<Void> listener) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(listener);
    }

    public static void getUserRole(String uid, String role, OnCompleteListener<DocumentSnapshot> listener) {
        String collection;
        switch (role.toLowerCase()) {
            case "doctor":
                collection = Constants.COLLECTION_DOCTORS;
                break;
            case "admin":
                collection = Constants.COLLECTION_ADMINS;
                break;
            default:
                collection = Constants.COLLECTION_USERS;
        }
        
        db.collection(collection).document(uid).get()
            .addOnCompleteListener(listener);
    }

    public interface RoleCallback {
        void onRoleFound(String role);
    }

    public static void checkUserRole(String uid, RoleCallback callback) {
        db.collection(Constants.COLLECTION_DOCTORS).document(uid).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    callback.onRoleFound("doctor");
                } else {
                    db.collection(Constants.COLLECTION_ADMINS).document(uid).get()
                        .addOnSuccessListener(adminDoc -> {
                            if (adminDoc.exists()) {
                                callback.onRoleFound("admin");
                            } else {
                                callback.onRoleFound("user");
                            }
                        });
                }
            });
    }

    public static void updateDocument(String collection, String documentId, Map<String, Object> data) {
        db.collection(collection)
            .document(documentId)
            .update(data);
    }

    public static void deleteDocument(String collection, String documentId) {
        db.collection(collection)
            .document(documentId)
            .delete();
    }

    public static void updateDocument(String collection, String documentId, Map<String, Object> data,
                                    OnSuccessListener<Void> successListener,
                                    OnFailureListener failureListener) {
        db.collection(collection)
            .document(documentId)
            .update(data)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener);
    }

    public static void deleteDocument(String collection, String documentId,
                                    OnSuccessListener<Void> successListener,
                                    OnFailureListener failureListener) {
        db.collection(collection)
            .document(documentId)
            .delete()
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener);
    }
} 