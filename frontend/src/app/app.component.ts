import {Component, NgModule, Output} from '@angular/core';
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
  tag : string = "";

  selectedSearch : string = "";

  select(selection : string) {
    this.selected = selection;
  }
  search(searchValue :string){
    this.searchValue = searchValue;
  }
  selectCompany(companyName : string){
    this.company = companyName;
    this.selectedSearch = companyName;
    console.log(companyName)
  }

  selectTag(tag: string) {
    this.tag = tag;
    this.selectedSearch = tag;
    console.log(tag);

  }
}


