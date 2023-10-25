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
  navElementsBackup = ["Overview", "Posts", "Tags", "Users"];
  navElements = this.navElementsBackup;


  constructor(private cs : CookieService, private db : DbService) {
    this.navElements = [];
    // COOKIE VALIDATION //
    this.db.validate().then(res  => {
      console.log(res);
      var usid = res;
      if (usid.toString().includes("Invalid") || usid == null || res == null || res == undefined) {
        SysVars.USER_ID = "0";
        return;
      }
      this.db.getUserById(String(usid.user_id)).then(res => {
        SysVars.login.next(res);
        SysVars.USER_ID = "0";
        if (usid.user_id == "20" || usid.user_id == "27" || usid.user_id == "52"){
          SysVars.ADMIN = true;
        }
        SysVars.ADMIN = false;
      })
    })

    SysVars.login.subscribe(user => {
       this.navElements = this.navElementsBackup;
      cs.set("user", user.id + ":" + user.displayName);
      this.selected.next("Overview");
      SysVars.USER_ID = "0";
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
