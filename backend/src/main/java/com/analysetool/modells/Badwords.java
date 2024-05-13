package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "badwords")
public class Badwords implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="bad_word")
    private String badWord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBadWord() {
        return badWord;
    }

    public void setBadWord(String badWord) {
        this.badWord = badWord;
    }
}
