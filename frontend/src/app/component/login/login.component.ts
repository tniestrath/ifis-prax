import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {UserService} from "../../services/user.service";
import {User} from "../../page/page-einzel/user/user";

@Component({
  selector: 'dash-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class LoginComponent extends DashBaseComponent implements OnInit{
  incorrect = false;

  ngOnInit(): void {

  }

  onSubmit(username: string, userpass: string) {
    this.db.login(username, userpass).then(res => {
      res.text().then(ans => {
        ans = decodeURIComponent(ans);
        this.cs.deleteAll();
        this.styleLogin(false);
        //CHECK LOGIN
        if (ans.includes("LOGIN REJECTED")){
          this.styleLogin(true);
          return;
        }

        this.cs.set(ans.substring(0, ans.indexOf("=")), ans.substring(ans.indexOf("=")+1, ans.indexOf(";")), {expires: 2});
        this.db.getUserByLogin(ans.substring(ans.indexOf("=") + 1, ans.indexOf("|"))).then(res => {
          UserService.login.next(res);
          console.log("admin?" + res.accountType)
          UserService.ADMIN = res.accountType == "admin";

          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
    });
  }

  styleLogin(incorrect : boolean){
    this.incorrect = incorrect;
    if (incorrect){
      var inputs = document.getElementsByTagName("input");
      for (let i = 0; i < inputs.length ; i++) {
        inputs[i].style.borderRadius = "5px";
        inputs[i].style.border = "1px solid red";
      }

    }
  }

}
