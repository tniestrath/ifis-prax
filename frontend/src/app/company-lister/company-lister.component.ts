import {Component, OnInit} from '@angular/core';
import {Company} from "../company-details/Company";
import {CompanyService} from "../services/company.service";

@Component({
  selector: 'dash-company-lister',
  templateUrl: './company-lister.component.html',
  styleUrls: ['./company-lister.component.css']
})
export class CompanyListerComponent implements OnInit{

  companies : Company[] = [];

  constructor(private service: CompanyService) {
  }

  ngOnInit(): void {
    this.service.getAllCompanies().then(res => this.companies = res);
  }



}
