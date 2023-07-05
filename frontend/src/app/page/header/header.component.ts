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


  constructor(private cs : CookieService) {
    this.navElements = [];
    UserService.login.subscribe(user => {
      if (user.accountType == "admin"){
        this.navElements = ["Overview", "Posts", "Tags", "Users"];
      }else {
        cs.set("user", user.id + ":" + user.displayName);
      }
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
