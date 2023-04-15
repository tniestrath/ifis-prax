import {Component, Input, OnInit} from '@angular/core';
import {Company} from "./Company";
import {CompanyService} from "../services/company.service";

@Component({
  selector: 'dash-company-details',
  templateUrl: './company-details.component.html',
  styleUrls: ['./company-details.component.css'],
})
export class CompanyDetailsComponent implements OnInit{

  @Input() company? : Company;

  constructor(private service : CompanyService){
  }

   ngOnInit(): void {
  }

}
