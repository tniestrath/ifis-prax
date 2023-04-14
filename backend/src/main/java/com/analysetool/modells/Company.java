package com.analysetool.modells;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "companies")
    public class Company {
        @Id
        private String id;
       // @Indexed(unique = true)
        private String Firmaname;
        private Kontaktdaten Kontaktdaten;
        private String Typ;
        private String Groesse;
        private String Land;
        private String MedienTyp;

        private String keywords;

        public Company() {}

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public Company(String Firmaname, Kontaktdaten Kontaktdaten, String Typ, String Groesse, String Land, String MedienTyp, String keywords) {
            super();
            this.Firmaname = Firmaname;
            this.Kontaktdaten = Kontaktdaten;
            this.Typ = Typ;
            this.Groesse = Groesse;
            this.Land = Land;
            this.MedienTyp = MedienTyp;
            this.keywords = keywords;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirmaname() {
            return Firmaname;
        }

        public void setFirmaname(String Firmaname) {
            this.Firmaname = Firmaname;
        }

        public Kontaktdaten getKontaktdaten() {
            return Kontaktdaten;
        }

        public void setKontaktdaten(Kontaktdaten Kontaktdaten) {
            this.Kontaktdaten = Kontaktdaten;
        }

        public String getTyp() {
            return Typ;
        }

        public void setTyp(String Typ) {
            this.Typ = Typ;
        }

        public String getGroesse() {
            return Groesse;
        }

        public void setGroesse(String Groesse) {
            this.Groesse = Groesse;
        }

        public String getLand() {
            return Land;
        }

        public void setLand(String Land) {
            this.Land = Land;
        }

        public String getMedienTyp() {
            return MedienTyp;
        }

        public void setMedienTyp(String MedienTyp) {
            this.MedienTyp = MedienTyp;
        }

        public static class Kontaktdaten {
            private String email;
            private String telefon;
            private String link;

            public Kontaktdaten() {}

            public Kontaktdaten(String email, String telefon, String link) {
                this.email = email;
                this.telefon = telefon;
                this.link = link;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getTelefon() {
                return telefon;
            }

            public void setTelefon(String telefon) {
                this.telefon = telefon;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            @Override
            public String toString() {
                return "Kontaktdaten{" +
                        "email='" + email + '\'' +
                        ", telefon='" + telefon + '\'' +
                        ", link='" + link + '\'' +
                        '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Kontaktdaten that)) return false;
                return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getTelefon(), that.getTelefon()) && Objects.equals(getLink(), that.getLink());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getEmail(), getTelefon(), getLink());
            }
        }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", Firmaname='" + Firmaname + '\'' +
                ", Kontaktdaten=" + Kontaktdaten +
                ", Typ='" + Typ + '\'' +
                ", Groesse='" + Groesse + '\'' +
                ", Land='" + Land + '\'' +
                ", MedienTyp='" + MedienTyp + '\'' +
                ", keywords='" + keywords + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company company)) return false;
        return Objects.equals(getId(), company.getId()) && Objects.equals(getFirmaname(), company.getFirmaname()) && Objects.equals(getKontaktdaten(), company.getKontaktdaten()) && Objects.equals(getTyp(), company.getTyp()) && Objects.equals(getGroesse(), company.getGroesse()) && Objects.equals(getLand(), company.getLand()) && Objects.equals(getMedienTyp(), company.getMedienTyp()) && Objects.equals(getKeywords(), company.getKeywords());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirmaname(), getKontaktdaten(), getTyp(), getGroesse(), getLand(), getMedienTyp(), getKeywords());
    }

}


