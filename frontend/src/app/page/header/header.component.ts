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
  navElementsBackup = ["Übersicht", "Beiträge", "Themen", "Anbieter", "Inhalte", "SEO", "Suche", "Newsletter", "System"];
  navElements = this.navElementsBackup;

  loadingBar_process : any = null;
  html_err_code : string = "";

  constructor(private cs : CookieService, private db : DbService) {
    this.navElements = [];
    // COOKIE VALIDATION //
    this.db.validate().then(res  => {
      if (res.toString().includes("Invalid") || res == null || res.user_id == undefined) {
        SysVars.USER_ID = "0";
        return;
      }
      this.db.getUserById(String(res.user_id)).then(res => {
        SysVars.login.next(res);
        SysVars.USER_ID = "0";
        SysVars.ADMIN = true;
        this.stopAndHideLoadingBar();
      })
    })

    SysVars.login.subscribe(user => {
      this.navElements = this.navElementsBackup;
      cs.set("user", user.id + ":" + user.displayName);
      this.selected.next("Übersicht");
      SysVars.USER_ID = "0";
      this.stopAndHideLoadingBar();
    })
  }

  ngOnInit(): void {
    this.showAndStartLoadingBar();
    this.db.status.subscribe(status => {
      if (status == 0) this.stopAndHideLoadingBar();
      else if (status == 1) this.showAndStartLoadingBar();
      else {this.setLoadingBarErrorCode(status); this.showAndStartLoadingBar()}
    })
  }

  setSelected(page : string){
    this.selected.next(page);
  }

  ngAfterViewInit(): void {
    this.selected.next("Landing");
  }

  hideLoadingBar(){
    let loadingBar = document.getElementById("loading-bar");
    // @ts-ignore
    loadingBar.style.display = "none";

    return loadingBar;
  }
  showLoadingbar(){
    let loadingBar = document.getElementById("loading-bar");
    // @ts-ignore
    loadingBar.style.display = "block";

    return loadingBar;
  }
  startLoadingBar(){
    let loadingBar = document.getElementById("loading-bar");
    let loadingBarProgress = document.getElementById("loading-bar-progress");
    // @ts-ignore
    let progress = -loadingBarProgress.clientWidth;
    // @ts-ignore
    if (this.loadingBar_process == null){
      this.loadingBar_process = setInterval(() => {
        if (loadingBarProgress != null){
          loadingBarProgress.style.left = (progress++) + "px";
          if (progress > document.body.clientWidth){
            progress = -loadingBarProgress.clientWidth;
          }
        }
      }, 4);
    }
    return {loadingBar, loadingBarProgress};
  }
  stopLoadingBar(){
    clearInterval(this.loadingBar_process);
    this.loadingBar_process = null;
  }

  stopAndHideLoadingBar(){
    this.stopLoadingBar();
    this.hideLoadingBar();
    this.setLoadingBarErrorCode(0);
  }

  showAndStartLoadingBar(){
    this.startLoadingBar();
    this.showLoadingbar();
  }

  setLoadingBarErrorCode(html_err_code : string | number){
    // @ts-ignore
    if (html_err_code != undefined && html_err_code != 0){
      this.html_err_code = "Err: " + String(html_err_code);
    } else if (html_err_code != 0) {
      this.html_err_code = "Err: Timed Out";
    } else if (html_err_code == 0){
      this.html_err_code = "";
    }
  }

  onLogoutClick() {
    this.cs.deleteAll("/");
    SysVars.WELCOME = true;
    location.reload();
  }

  onSpiderClick(){

  }

  protected readonly SysVars = SysVars;
  protected readonly print = print;
  protected readonly window = window;

  onFeedbackClick() {
      window.open("http://analyse.it-sicherheit.de/api/feature/feedbackSite", "_feedBack");
  }
}
