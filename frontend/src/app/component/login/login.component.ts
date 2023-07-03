import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'dash-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class LoginComponent extends DashBaseComponent implements OnInit{

  ngOnInit(): void {
    if (this.cs.check("login_token")){
      UserService.login.next("ifis-admin:1:token");
    }
  }


}
