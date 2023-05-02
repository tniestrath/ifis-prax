import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Company} from "../Company";
import {CompanyService} from "../../services/company.service";

@Component({
  selector: 'dash-company-lister',
  templateUrl: './company-lister.component.html',
  styleUrls: ['./company-lister.component.css']
})
export class CompanyListerComponent implements OnInit, OnChanges{

  companies : Company[] = [];
  allCompanies : Company[] = [];
  @Input() searchValue : string = "";
  @Output() selectedCompany = new EventEmitter<string>();

  constructor(private service: CompanyService) {
  }

  onClick(companyName : string){
    this.selectedCompany.emit(companyName);
  }

  ngOnInit(): void {
    this.service.getAllCompanies().then(res => this.allCompanies = res).then(res => this.companies = res);
  }

  ngOnChanges(changes: SimpleChanges): void {

    this.companies = this.allCompanies.filter(
      value => value.firmaname.toUpperCase().includes(this.searchValue.toUpperCase()) ||
        value.keywords.toUpperCase().includes(this.searchValue.toUpperCase()));
  }

}
