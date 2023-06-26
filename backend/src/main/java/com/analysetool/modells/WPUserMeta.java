package com.analysetool.modells;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wp_usermeta")
public class WPUserMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "umeta_id")
    private Long id;

    @Column(name = "user_id")
    private String user_id;

    @Column(name = "meta_key")
    private String key;

    @Column(name = "meta_value")
    private String value;
}
