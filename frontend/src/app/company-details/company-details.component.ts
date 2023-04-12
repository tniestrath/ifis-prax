import {Component, Input, OnInit} from '@angular/core';
import {Company, Contact} from "./Company";

@Component({
  selector: 'dash-company-details',
  templateUrl: './company-details.component.html',
  styleUrls: ['./company-details.component.css'],
})
export class CompanyDetailsComponent implements OnInit{

  @Input() company :Company = new Company("0", "Test Company", "200 Mitarbeiter", new Contact("example@company.com", "+49 123 456789", "example.de"),
    ["DE", "FR"], ["Unternehmen", "Hochschule"], "Presse");

  constructor(){
  }

  ngOnInit(): void {
  }


}
