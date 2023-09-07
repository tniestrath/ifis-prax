import {AfterViewInit, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Subject} from "rxjs";
import {DbService} from "../../services/db.service";
import {SysVars} from "../../services/sys-vars-service";
import {CookieService} from "ngx-cookie-service";


@Component({
  selector: 'dash-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements AfterViewInit{

  @Output() selected = new Subject<string>();
  navElements = ["Overview", "Posts", "Tags", "Users"];


  constructor(private cs : CookieService, private db : DbService) {
    var currentNavElements = [];
    // COOKIE VALIDATION //
    this.db.validate().then(res  => {
      var usid = res.user_id;
      if (usid.toString().includes("Invalid") || usid == null || res == null || res == undefined) {
        SysVars.USER_ID = "0";
        return;
      }
      this.db.getUserById(usid).then(res => {
        SysVars.login.next(res);
        SysVars.USER_ID = usid;
        SysVars.ADMIN = true;
      })
    })

    SysVars.login.subscribe(user => {
      currentNavElements = this.navElements;
      cs.set("user", user.id + ":" + user.displayName);
      this.selected.next("Overview");
      SysVars.USER_ID = user.id;
    })
  }

  ngOnInit(): void {

  }

  setSelected(page : string){
    this.selected.next(page);
  }

  ngAfterViewInit(): void {
    this.selected.next("Landing");
  }
}
