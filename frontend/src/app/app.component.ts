import {Component, NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";



@Component({
  selector: 'dash-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Dashboard';
  selected : string = "kennzahlen";
  searchValue : string = "";
  company : string = "";

  select(selection : string) {
    this.selected = selection;
  }
  search(searchValue :string){
    this.searchValue = searchValue;
  }
  selectCompany(companyName : string){
    this.company = companyName;
    console.log(companyName)
  }
}


