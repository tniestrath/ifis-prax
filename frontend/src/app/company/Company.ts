import {DbObject} from "../services/DbObject";

export interface Kontaktdaten {
  email : string;
  telefon : string;
  link : string;


}

export interface Company extends DbObject{
  id : string;
  name : string;
  groesse : string;
  kontaktdaten : Kontaktdaten;
  land : string;
  typ : string;
  medienTyp : string;
  keywords : string;


}
