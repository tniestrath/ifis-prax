import {Component, ElementRef, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {SafeUrl} from "@angular/platform-browser";
import {SysVars} from "../../services/sys-vars-service";
import {Subject} from "rxjs";


@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css']
})
export class SearchbarComponent implements OnInit{

  @Output() searchInput :string = "";
  @Output() currentSearch = new EventEmitter<string>();
  @Output() selected = new EventEmitter<DbObject>();
  @Output() filter = new EventEmitter<{ accType : string, sort : string }>();
  @Input('reset') reset = new Subject<boolean>();

  page : string = "user";

  selectedSearch : DbObject = new DbObject("0", "");
  displaySearchBox: string = "";
  imgSrc: SafeUrl = "";

  shown = false;
  logged_in = false;
  loggedUser :string[]  = ["0", ""];
  constructor(protected element : ElementRef, private cs : CookieService, private db : DbService) {
    SysVars.login.subscribe(user => {
      this.loggedUser = [user.id, user.displayName];
      this.logged_in = true;
      SysVars.WELCOME = true;
    })
  }

  ngOnInit(): void {
    this.setupFilter();
    this.reset.subscribe(value => {
      this.onReset();
    });
  }

  onKey(value? : string) {
    if (value == null){return}
    this.searchInput = value;
    this.currentSearch.emit(value);
  }

  onDbObjectSelected(id: string, name: string){
    let index  = Math.max(name.lastIndexOf("-"), name.lastIndexOf(" "));
    let shortName;
    if (index >= 10){
      shortName =  name.slice(0,index);
    } else {
      shortName = name;
    }
    let object : DbObject = new DbObject(id,shortName);

    if (id != "0"){
      this.selectedSearch = object;
      this.displaySearchBox = "0";
    }
    this.selected.emit(new DbObject(id, name));
    this.cs.set(this.page, object.id + ":" + object.name, {expires : 2});

    this.getImgSrc(this.selectedSearch.id);

    this.shown = false;
  }

  onReset(){
    this.selectedSearch = new DbObject("0", "");
    this.displaySearchBox = "50px";
    this.onKey();
    this.onDbObjectSelected("0", "");
  }

  onCancelClick() {
    this.selectedSearch = new DbObject("0", "");
    this.displaySearchBox = "50px";
    this.onKey();
    this.onDbObjectSelected("0", "");
    SysVars.CURRENT_PAGE = "Anbieter";
  }

  getImgSrc(id: string) {
    this.db.getUserImgSrc(id).then(dataUrl => {
      this.imgSrc = dataUrl;
    });
  }

  setupFilter() {
    let selected_account_filter = "all";
    let selected_sort = "uid";

    let filter_accountTypeWithoutPlan = document.getElementById("searchbar-filter-accountType-without-plan") as HTMLDivElement;
    let filter_accountTypeBasic = document.getElementById("searchbar-filter-accountType-basic") as HTMLDivElement;
    let filter_accountTypeBasicPlus = document.getElementById("searchbar-filter-accountType-basicPlus") as HTMLDivElement;
    let filter_accountTypePlus = document.getElementById("searchbar-filter-accountType-plus") as HTMLDivElement;
    let filter_accountTypePremium = document.getElementById("searchbar-filter-accountType-premium") as HTMLDivElement;
    let filter_accountTypeAll = document.getElementById("searchbar-filter-accountType-all") as HTMLDivElement;

    let filter_sort_views = document.getElementById("searchbar-sorter-views") as HTMLDivElement;
    let filter_sort_performance = document.getElementById("searchbar-sorter-performance") as HTMLDivElement;
    let filter_sort_uid = document.getElementById("searchbar-sorter-uid") as HTMLDivElement;

    filter_accountTypeWithoutPlan.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "#951D40";

      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypeAll.style.color = "black";
      selected_account_filter = "ohne abo";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypeBasic.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeBasic.style.color = "#951D40";

      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypeAll.style.color = "black";
      selected_account_filter = "basis";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypeBasicPlus.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeBasic.style.color = "black";

      filter_accountTypeBasicPlus.style.color = "#951D40";

      filter_accountTypePlus.style.color = "black";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypeAll.style.color = "black";
      selected_account_filter = "basis-plus";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypePlus.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasicPlus.style.color = "black";

      filter_accountTypePlus.style.color = "#951D40";

      filter_accountTypePremium.style.color = "black";
      filter_accountTypeAll.style.color = "black";
      selected_account_filter = "plus";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypePremium.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypePlus.style.color = "black";

      filter_accountTypePremium.style.color = "#951D40";

      filter_accountTypeAll.style.color = "black";
      selected_account_filter = "premium";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })


    filter_accountTypeAll.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePremium.style.color = "black";

      filter_accountTypeAll.style.color = "#951D40";
      selected_account_filter = "all";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })
    filter_accountTypeAll.style.color = "#951D40";


    filter_sort_views.addEventListener("click", () => {
      filter_sort_views.style.color = "#951D40";
      filter_sort_performance.style.color = "black";
      filter_sort_uid.style.color = "black";
      selected_sort = "views";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_sort_performance.addEventListener("click", () => {
      filter_sort_views.style.color = "black";
      filter_sort_performance.style.color = "#951D40";
      filter_sort_uid.style.color = "black";
      selected_sort = "performance";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_sort_uid.addEventListener("click", () => {
      filter_sort_views.style.color = "black";
      filter_sort_performance.style.color = "black";
      filter_sort_uid.style.color = "#951D40";
      selected_sort = "uid";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })
    filter_sort_uid.style.color = "#951D40";
  }

  protected readonly UserService = SysVars;
}
