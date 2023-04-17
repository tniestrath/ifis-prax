import {Injectable} from '@angular/core';
import {Company} from "../company-details/Company";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private dbUrl = "http://localhost:8080";
  private getAllUrl = "http://localhost:8080/list";
  private getFirstUrl = "http://localhost:8080/first";
  constructor() {

  }

  async getFirstCompany() : Promise<Company> {
    return await fetch(this.getFirstUrl).then(res => res.json());
  }

  async getAllCompanies() : Promise<Company[]> {
    return await fetch(this.getAllUrl).then(res => res.json());
  }


}
