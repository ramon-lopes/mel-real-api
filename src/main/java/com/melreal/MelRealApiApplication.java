package com.melreal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // habilita o reset automático de meia-noite
public class MelRealApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MelRealApiApplication.class, args);
    }
}
