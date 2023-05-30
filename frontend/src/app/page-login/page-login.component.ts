import {Component, EventEmitter, Output} from '@angular/core';
import {DbService} from "../services/db.service";
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'dash-page-login',
  templateUrl: './page-login.component.html',
  styleUrls: ['./page-login.component.css']
})
export class PageLoginComponent {

  @Output() login_success = new EventEmitter<void>;

  constructor(private db : DbService, private cs : CookieService) {
  }

  onSubmit(name: string, pass: string) {
    if (name.indexOf("@") != -1){
      this.db.getUserByEmail(name).then(res => {
        this.cs.set("einzel", res.id + ":" + res.displayName, {expires : 7});
        this.login_success.emit();
      })
    }
    else {
      this.db.getUserByLogin(name).then(res => {
        this.cs.set("einzel", res.id + ":" + res.displayName, {expires : 7});
        this.login_success.emit();
      })
    }
  }
}
