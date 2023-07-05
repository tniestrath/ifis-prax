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

  }

  onSubmit(username: string, userpass: string) {
    this.db.login(username, userpass).then(res => {
      res.text().then(ans => {
        ans = decodeURIComponent(ans);
        this.cs.deleteAll();
        //CHECK LOGIN
        if (ans.includes("LOGIN REJECTED")){
          return;
        }

        this.cs.set(ans.substring(0, ans.indexOf("=")), ans.substring(ans.indexOf("=")+1, ans.indexOf(";")), {expires: 2});
        this.db.getUserByLogin(ans.substring(ans.indexOf("=") + 1, ans.indexOf("|"))).then(res => {
          UserService.login.next(res);
        })
      });
    });
  }

  alertFailedLogin(){

  }

}
