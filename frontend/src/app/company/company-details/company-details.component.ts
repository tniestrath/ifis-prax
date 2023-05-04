import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Company} from "../Company";
import {CompanyService} from "../../services/company.service";

@Component({
  selector: 'dash-company-details',
  templateUrl: './company-details.component.html',
  styleUrls: ['./company-details.component.css'],
})
export class CompanyDetailsComponent implements OnInit{

  @Input() company? : Company;
  @Output() selectedCompany = new EventEmitter<string>();

  constructor(private service : CompanyService){
  }

   ngOnInit(): void {
  }

  onClick(){
    this.selectedCompany.emit(this.company?.name);
  }
}
