package com.cinetickets.api.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cinema_info")
public class Cinema {
    @Id
    private final UUID id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"); // ID fijo
    
    private String name = "Cine Annuar Shopping";
    private String address = "Av. Annuar Shopping 123";
    private String city = "San Salvador de Jujuy"; 
    private String state = "Jujuy";
    private String phone = "+54 388 123 4567";
    private String email = "info@cineannuar.com";
    private String website = "https://cineannuar.com";
}