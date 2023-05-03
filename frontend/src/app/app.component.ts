import {Component} from '@angular/core';
import {CookieService} from "ngx-cookie-service";



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

  constructor() {
  }

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


