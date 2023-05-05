import {Component, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {Tag} from "../../tag/Tag";
import {DbService} from "../../services/db.service";

@Component({
  selector: 'dash-page-tag',
  templateUrl: './page-tag.component.html',
  styleUrls: ['./page-tag.component.css']
})
export class PageTagComponent implements OnInit{
  displayContent: string = "none";

  postCount: string = "0";

  constructor(private cookieService : CookieService, private db: DbService) {
  }
  onSelected(id : string, name : string){
    if (id != "0"){
      this.displayContent = "grid";
      this.db.getTagPostCount(id).then(res => this.postCount = res);
    } else {
      this.displayContent = "none";
    }
  }

  ngOnInit(): void {
    let object :string[]  = this.cookieService.get("tag").split(":");
    this.onSelected(object[0], object[1]);

  }

}
