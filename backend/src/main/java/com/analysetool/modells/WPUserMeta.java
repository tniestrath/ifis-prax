package com.analysetool.modells;

import jakarta.persistence.*;

import java.util.Objects;


@Entity
@Table(name = "wp_usermeta")
public class WPUserMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "umeta_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "meta_key")
    private String key;

    @Column(name = "meta_value")
    private String value;

    public WPUserMeta(){}

    public WPUserMeta(Long id, Long userId, String key, String value) {
        this.id = id;
        this.userId = userId;
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WPUserMeta that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getKey(), that.getKey()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getKey(), getValue());
    }

    @Override
    public String toString() {
        return "WPUserMeta{" +
                "id=" + id +
                ", userId=" + userId +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
