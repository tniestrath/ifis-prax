import {Component, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'dash-page-einzel',
  templateUrl: './page-einzel.component.html',
  styleUrls: ['./page-einzel.component.css']
})
export class PageEinzelComponent implements OnInit {
  displayContent: string = "none";

  constructor(private cookieService : CookieService) {
  }
  onSelected(tag : string){
    if (tag != ""){
      this.displayContent = "grid";
    } else {
      this.displayContent = "none";
    }
  }

  ngOnInit(): void {
    this.onSelected(this.cookieService.get("einzel"));
  }
}
