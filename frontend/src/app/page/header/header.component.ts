import {AfterViewInit, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Subject} from "rxjs";
import {DbService} from "../../services/db.service";
import {UserService} from "../../services/user.service";
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
    this.navElements = [];
    // COOKIE VALIDATION //
    this.db.validate().then(res  => {
      if (res.user_id.toString().includes("Invalid")) {
        UserService.USER_ID = "0";
        return;
      }
      this.db.getUserById(res.user_id).then(res => {
        UserService.login.next(res);
        UserService.USER_ID = "0";
        UserService.ADMIN = res.accountType == "admin";
      })
    })

    UserService.login.subscribe(user => {
      if (user.accountType == "admin"){
        this.navElements = ["Overview", "Posts", "Tags", "Users"];
      }else {
        cs.set("user", user.id + ":" + user.displayName);
      }
      UserService.USER_ID = user.id;
      this.selected.next("Users");
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
