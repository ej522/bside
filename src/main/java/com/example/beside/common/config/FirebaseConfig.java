package com.example.beside.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {
    private final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void init(){

        try{
            log.info("try문");
            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            if(firebaseApps != null && !firebaseApps.isEmpty()) {
                log.info("not empty");
                for(FirebaseApp app : firebaseApps) {
                    if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                        firebaseApp = app;
                }
            } else {
                log.info("empty");
                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/serviceAccountKey.json");
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                log.info("=======init starttttt=======");
                firebaseApp = FirebaseApp.initializeApp(options);
                log.info("=======init enddddd=======");

            }
        }catch (Exception e){
            log.info("!!error!!");
            e.printStackTrace();
        }
    }

}
