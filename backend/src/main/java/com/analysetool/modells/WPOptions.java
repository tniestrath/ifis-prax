package com.analysetool.modells;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "wp_options")
public class WPOptions implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @Column(name = "option_name")
    private String optionName;

    @Column(name = "option_value")
    private String optionValue;

    @Column(name = "autoload")
    private String autoload;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getAutoload() {
        return autoload;
    }

    public void setAutoload(String autoload) {
        this.autoload = autoload;
    }
}
