import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private dbUrl = "http://localhost:8080";
  private getAllUrl = "http://localhost:8080/list";
  constructor(private http : HttpClient) {

  }

  getCompanies() : Observable<String> {
    return this.http.get<String>(this.getAllUrl);
  }
}
