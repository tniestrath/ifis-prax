import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'dash-company-details',
  templateUrl: './company-details.component.html',
  styleUrls: ['./company-details.component.css'],
})
export class CompanyDetailsComponent implements OnInit{

  @Input() company :Company = new Company();

  constructor(){
  }

  ngOnInit(): void {
  }


}

class ContactData {
  email: string;
  telefon: string;
  link: string;

  constructor() {
    this.email = "example@company.com";
    this.link = "example.de";
    this.telefon = "+49 123 456789  ";
  }



}

interface Company {
  d: string;
  firmaname: string;
  kontaktdaten: ContactData;
  typ: string[];
  groesse: string;
  land: string[];
  medienTyp: string;
}

class Company {
  id: string;
  firmaname: string;
  kontaktdaten: ContactData;
  typ: string[];
  groesse: string;
  land: string[];
  medienTyp: string;

  constructor() {
    this.id = "0";
    this.firmaname = "Test Company";
    this.kontaktdaten = new ContactData();
    this.typ = ["Unternehmen", "Hochschule"];
    this.groesse = "200 Mitarbeiter";
    this.land = ["DE", "FR"];
    this.medienTyp = "Presse";
  }
}
