export interface Kontaktdaten {
  email : string;
  telefon : string;
  link : string;


}

export interface Company{
  firmaname : string;
  groesse : string;
  kontaktdaten : Kontaktdaten;
  land : string;
  typ : string;
  medienTyp : string;
  keywords : string;


}
