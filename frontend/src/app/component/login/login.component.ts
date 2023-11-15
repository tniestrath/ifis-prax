import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SysVars} from "../../services/sys-vars-service";

enum Reason {
  CORRECT,
  INCORRECT,
  ERR_502
}

@Component({
  selector: 'dash-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class LoginComponent extends DashBaseComponent implements OnInit{
  loginError = false;
  loginErrorMsg : string = "";

  ngOnInit(): void {
    this.setToolTip("Sie können sich hier mit den Login-Daten Ihres Marktplatz-Kontos anmelden.");
  }

  onSubmit(username: string, userpass: string) {
    this.db.login(username, userpass).then(res => {
      res.text().then(ans => {
        console.log(ans);
        ans = decodeURIComponent(ans);
        this.cs.deleteAll();
        this.styleLogin(Reason.CORRECT);
        //CHECK LOGIN
        if (ans.includes("LOGIN REJECTED")){
          this.styleLogin(Reason.INCORRECT);
          return;
        }
        this.cs.set(ans.substring(0, ans.indexOf("=")), ans.substring(ans.indexOf("=")+1, ans.indexOf(";")));
        this.db.getUserByLogin(ans.substring(ans.indexOf("=") + 1, ans.indexOf("|"))).then(res => {
          SysVars.login.next(res);
          //SysVars.ADMIN = res.accountType == "admin";
          SysVars.ADMIN = true;

          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
    }).catch(reason => {
      this.styleLogin(Reason.ERR_502);
      return;
    });
  }

  styleLogin(reason : Reason){
    var inputs = document.getElementsByTagName("input");
    if (reason == Reason.INCORRECT){
      this.loginError = true;
      this.loginErrorMsg = "Anmeldedaten inkorrekt";
      for (let i = 0; i < inputs.length ; i++) {
        inputs[i].style.borderRadius = "5px";
        inputs[i].style.border = "1px solid red";
      }
    }
    if (reason == Reason.ERR_502){
      this.loginError = true;
      this.loginErrorMsg = "Server antwortet nicht"
    }
  }

}
