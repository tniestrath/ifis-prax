package com.analysetool.modells;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
@Document
public class providers {
    @Id
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public providers(String id){
        this.id = id;
    }

}
