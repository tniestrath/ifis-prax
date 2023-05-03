import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css']
})
export class SearchbarComponent implements OnInit{

  @Output() searchInput :string = "";
  @Output() selected = new EventEmitter<string>();

  @Input() page : string = "placeholder";

  selectedSearch : string = "";
  displaySearchBox: string = "";

  constructor(private cookieService : CookieService) {
  }

  ngOnInit(): void {
    this.onTagSelected(this.cookieService.get(this.page));
  }

  onKey(value : string) {
    this.searchInput = value;
  }

  onTagSelected(tag : string){
    if (tag != ""){
      this.selectedSearch = tag;
      this.displaySearchBox = "0";
    }
    this.selected.emit(tag);
    this.cookieService.set(this.page, tag);
  }

  onCancelClick(){
    this.selectedSearch = "";
    this.displaySearchBox = "50px";
    this.onKey("");
    this.onTagSelected("");
  }
}
