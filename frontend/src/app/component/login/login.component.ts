import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SysVars} from "../../services/sys-vars-service";
import {User} from "../user/user";

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
    this.setToolTip("Sie können sich hier mit den Login-Daten Ihres Marktplatz-Kontos anmelden." +
      "<br><br>" +
      "Sollte der Server gerage ein Update durchführen oder aus anderen Gründen nicht erreichbar sein, werden sie, sobald möglich automatisch eingeloggt");
  }

  onSubmit(username: string, userpass: string) {
    (document.getElementById("login-submit") as HTMLButtonElement).disabled = true;
    // @ts-ignore
    this.api.loginWithBody(username, userpass).then(res => {
      if (res.status >= 400) throw "Error";
      res.text().then(ans => {
        ans = decodeURIComponent(ans);
        this.cs.deleteAll();
        this.styleLogin(Reason.CORRECT);
        (document.getElementById("login-submit") as HTMLButtonElement).disabled = false;
        //CHECK LOGIN
        if (ans.includes("LOGIN REJECTED")){
          this.styleLogin(Reason.INCORRECT);
          (document.getElementById("login-submit") as HTMLButtonElement).disabled = false;
          return;
        }
        this.cs.set("wordpress_logged_in", ans.substring(ans.indexOf("=")+1, ans.indexOf(";")));
        this.api.getUserByLogin(ans.substring(ans.indexOf("=") + 1, ans.indexOf("|"))).then(res => {
          SysVars.login.next(res);
          SysVars.ADMIN = res.accountType == "admin";

          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
    }).catch(reason => {
      this.styleLogin(Reason.ERR_502);
      var ping = setInterval(() => {
        this.api.ping().then(res => {
          if (res){
            clearInterval(ping);
            this.api.loginWithBody(username, userpass).then(res => {
              res.text().then(ans => {
                ans = decodeURIComponent(ans);
                this.cs.deleteAll();
                this.styleLogin(Reason.CORRECT);
                (document.getElementById("login-submit") as HTMLButtonElement).disabled = false;
                //CHECK LOGIN
                if (ans.includes("LOGIN REJECTED")){
                  this.styleLogin(Reason.INCORRECT);
                  (document.getElementById("login-submit") as HTMLButtonElement).disabled = false;
                  return;
                }
                this.cs.set(ans.substring(0, ans.indexOf("=")), ans.substring(ans.indexOf("=")+1, ans.indexOf(";")));
                this.api.getUserByLogin(ans.substring(ans.indexOf("=") + 1, ans.indexOf("|"))).then(res => {
                  SysVars.login.next(res);
                  SysVars.ADMIN = res.accountType == "admin";

                  this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
              });
            });
          }
        })
      }, 1000 * 30);
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
