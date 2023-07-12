package com.example.beside.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void init(){
        try{
            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            if(firebaseApps != null && !firebaseApps.isEmpty()) {
                for(FirebaseApp app : firebaseApps) {
                    if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                        firebaseApp = app;
                }
            } else {
                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/serviceAccountKey.json");
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
