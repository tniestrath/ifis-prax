import {Component, ElementRef, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {SafeUrl} from "@angular/platform-browser";
import {SysVars} from "../../services/sys-vars-service";
import {Observable, Subject} from "rxjs";


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

  filter_dropdown: HTMLDivElement | null = null;
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
    this.reset.subscribe(value => {
      this.onReset();
    })
  }

  onKey(value : string) {
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

    if (this.filter_dropdown){
      this.filter_dropdown.style.display = "none";
    }
    this.shown = false;
  }

  onReset(){
    this.selectedSearch = new DbObject("0", "");
    this.displaySearchBox = "50px";
    this.onKey("");
    this.onDbObjectSelected("0", "");
  }

  onCancelClick(){
    this.selectedSearch = new DbObject("0", "");
    this.displaySearchBox = "50px";
    this.onKey("");
    this.onDbObjectSelected("0", "");
    SysVars.CURRENT_PAGE = "Anbieter";

  }
  onLogoutClick() {
    this.cs.deleteAll("/");
    SysVars.WELCOME = true;
    location.reload();
  }

  getImgSrc(id: string) {
    this.db.getUserImgSrc(id).then(dataUrl => {
      this.imgSrc = dataUrl;
    });
  }

  onFilterClick() {
    if (SysVars.CURRENT_PAGE == "Anbieter") {
      if (!document.getElementById("filter_dropdown")) {
        this.filter_dropdown = document.createElement("div");
        this.filter_dropdown.id = "filter_dropdown";
        this.filter_dropdown.style.height = "50px";
        this.filter_dropdown.style.width = "100%";
        this.filter_dropdown.style.boxSizing = "border-box";
        this.filter_dropdown.style.border = "2px solid #A0A0A0";
        this.filter_dropdown.style.boxShadow = "0 5px 5px rgba(0,0,0,.2)"
        this.filter_dropdown.style.marginLeft = "5px;"
        this.filter_dropdown.style.borderRadius = "5px";
        this.filter_dropdown.style.backgroundColor = "white";
        this.filter_dropdown.style.padding = "5px";
        this.filter_dropdown.style.display = "flex";
        this.filter_dropdown.style.flexDirection = "row";
        this.filter_dropdown.style.flexWrap = "none";

        let filter_styles = '' +
          'margin-right: 5px;' +
          'border: 1px solid #A0A0A0;' +
          'border-radius: 5px;' +
          'text-align: center;' +
          'width: 10%;' +
          'height: 35px;';

        let sorter_styles= '' +
          'margin-right: 5px;' +
          'border: 1px solid #A0A0A0;' +
          'border-radius: 5px;' +
          'text-align: center;' +
          'width: 20%;' +
          'height: 35px;';

        let selected_account_filter = "all";
        let selected_sort = "uid";

        let label_accountType = document.createElement("div");
        let filter_accountTypeWithoutPlan = document.createElement("div");
        let filter_accountTypeBasic = document.createElement("div");
        let filter_accountTypeBasicPlus = document.createElement("div");
        let filter_accountTypePlus = document.createElement("div");
        let filter_accountTypePremium = document.createElement("div");
        let filter_accountTypeSponsor = document.createElement("div");
        let filter_accountTypeAll = document.createElement("div");

        let label_sort_by = document.createElement("div");
        let filter_sort_views = document.createElement("div");
        let filter_sort_performance = document.createElement("div");
        let filter_sort_uid = document.createElement("div");

        label_accountType.innerText = "Abo Model";
        label_accountType.style.width = "100%";
        this.filter_dropdown.appendChild(label_accountType);

        filter_accountTypeWithoutPlan.id = "filter_type_withoutPlan";
        filter_accountTypeWithoutPlan.innerText = "Ohne Abo";
        filter_accountTypeWithoutPlan.style.cssText = filter_styles;

        filter_accountTypeWithoutPlan.addEventListener("mouseenter",
          () => filter_accountTypeWithoutPlan.style.borderColor = "black");
        filter_accountTypeWithoutPlan.addEventListener("mouseleave",
          () => filter_accountTypeWithoutPlan.style.borderColor = "#A0A0A0");
        filter_accountTypeWithoutPlan.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "#951D40";

          filter_accountTypeBasic.style.color = "black";
          filter_accountTypeBasicPlus.style.color = "black";
          filter_accountTypePlus.style.color = "black";
          filter_accountTypePremium.style.color = "black";
          filter_accountTypeSponsor.style.color = "black";
          filter_accountTypeAll.style.color = "black";
          selected_account_filter = "ohne abo";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_accountTypeWithoutPlan);


        filter_accountTypeBasic.id = "filter_type_basic";
        filter_accountTypeBasic.innerText = "Basis";
        filter_accountTypeBasic.style.cssText = filter_styles;

        filter_accountTypeBasic.addEventListener("mouseenter",
          () => filter_accountTypeBasic.style.borderColor = "black");
        filter_accountTypeBasic.addEventListener("mouseleave",
          () => filter_accountTypeBasic.style.borderColor = "#A0A0A0");
        filter_accountTypeBasic.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "black";
          filter_accountTypeBasic.style.color = "#951D40";

          filter_accountTypeBasicPlus.style.color = "black";
          filter_accountTypePlus.style.color = "black";
          filter_accountTypePremium.style.color = "black";
          filter_accountTypeSponsor.style.color = "black";
          filter_accountTypeAll.style.color = "black";
          selected_account_filter = "basis";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_accountTypeBasic);


        filter_accountTypeBasicPlus.id = "filter_type_basicPlus";
        filter_accountTypeBasicPlus.innerText = "Basis-Plus";
        filter_accountTypeBasicPlus.style.cssText = filter_styles;

        filter_accountTypeBasicPlus.addEventListener("mouseenter",
          () => filter_accountTypeBasicPlus.style.borderColor = "black");
        filter_accountTypeBasicPlus.addEventListener("mouseleave",
          () => filter_accountTypeBasicPlus.style.borderColor = "#A0A0A0");
        filter_accountTypeBasicPlus.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "black";
          filter_accountTypeBasic.style.color = "black";

          filter_accountTypeBasicPlus.style.color = "#951D40";

          filter_accountTypePlus.style.color = "black";
          filter_accountTypePremium.style.color = "black";
          filter_accountTypeSponsor.style.color = "black";
          filter_accountTypeAll.style.color = "black";
          selected_account_filter = "basis-plus";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_accountTypeBasicPlus);


        filter_accountTypePlus.id = "filter_type_plus";
        filter_accountTypePlus.innerText = "Plus";
        filter_accountTypePlus.style.cssText = filter_styles;
        filter_accountTypePlus.addEventListener("mouseenter",
          () => filter_accountTypePlus.style.borderColor = "black");
        filter_accountTypePlus.addEventListener("mouseleave",
          () => filter_accountTypePlus.style.borderColor = "#A0A0A0");
        filter_accountTypePlus.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "black";
          filter_accountTypeBasic.style.color = "black";
          filter_accountTypeBasicPlus.style.color = "black";

          filter_accountTypePlus.style.color = "#951D40";

          filter_accountTypePremium.style.color = "black";
          filter_accountTypeSponsor.style.color = "black";
          filter_accountTypeAll.style.color = "black";
          selected_account_filter = "plus";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_accountTypePlus);


        filter_accountTypePremium.id = "filter_type_premium";
        filter_accountTypePremium.innerText = "Premium";
        filter_accountTypePremium.style.cssText = filter_styles;
        filter_accountTypePremium.addEventListener("mouseenter",
          () => filter_accountTypePremium.style.borderColor = "black");
        filter_accountTypePremium.addEventListener("mouseleave",
          () => filter_accountTypePremium.style.borderColor = "#A0A0A0");
        filter_accountTypePremium.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "black";
          filter_accountTypeBasic.style.color = "black";
          filter_accountTypeBasicPlus.style.color = "black";
          filter_accountTypePlus.style.color = "black";

          filter_accountTypePremium.style.color = "#951D40";

          filter_accountTypeSponsor.style.color = "black";
          filter_accountTypeAll.style.color = "black";
          selected_account_filter = "premium";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_accountTypePremium);


        filter_accountTypeSponsor.id = "filter_type_sponsor";
        filter_accountTypeSponsor.innerText = "Sponsor";
        filter_accountTypeSponsor.style.cssText = filter_styles;
        filter_accountTypeSponsor.addEventListener("mouseenter",
          () => filter_accountTypeSponsor.style.borderColor = "black");
        filter_accountTypeSponsor.addEventListener("mouseleave",
          () => filter_accountTypeSponsor.style.borderColor = "#A0A0A0");
        filter_accountTypeSponsor.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "black";
          filter_accountTypeBasic.style.color = "black";
          filter_accountTypeBasicPlus.style.color = "black";
          filter_accountTypePlus.style.color = "black";
          filter_accountTypePremium.style.color = "black";

          filter_accountTypeSponsor.style.color = "#951D40";

          filter_accountTypeAll.style.color = "black";
          selected_account_filter = "sponsor";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_accountTypeSponsor);


        filter_accountTypeAll.id = "filter_type_all";
        filter_accountTypeAll.innerText = "Alle";
        filter_accountTypeAll.style.cssText = filter_styles;
        filter_accountTypeAll.addEventListener("mouseenter",
          () => filter_accountTypeAll.style.borderColor = "black");
        filter_accountTypeAll.addEventListener("mouseleave",
          () => filter_accountTypeAll.style.borderColor = "#A0A0A0");
        filter_accountTypeAll.addEventListener("click", () => {
          filter_accountTypeWithoutPlan.style.color = "black";
          filter_accountTypeBasic.style.color = "black";
          filter_accountTypeBasicPlus.style.color = "black";
          filter_accountTypePlus.style.color = "black";
          filter_accountTypePremium.style.color = "black";
          filter_accountTypeSponsor.style.color = "black";

          filter_accountTypeAll.style.color = "#951D40";
          selected_account_filter = "all";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        filter_accountTypeAll.style.color = "#951D40";
        this.filter_dropdown.appendChild(filter_accountTypeAll);

        label_sort_by.innerText = "Sortieren nach";
        label_sort_by.style.width = "100%";
        this.filter_dropdown.appendChild(label_sort_by);

        filter_sort_views.id = "filter_sort_views";
        filter_sort_views.innerText = "Views";
        filter_sort_views.style.cssText = sorter_styles;
        filter_sort_views.addEventListener("mouseenter",
          () => filter_sort_views.style.borderColor = "black");
        filter_sort_views.addEventListener("mouseleave",
          () => filter_sort_views.style.borderColor = "#A0A0A0");
        filter_sort_views.addEventListener("click", () => {
          filter_sort_views.style.color = "#951D40";
          filter_sort_performance.style.color = "black";
          filter_sort_uid.style.color = "black";
          selected_sort = "views";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_sort_views);

        filter_sort_performance.id = "filter_sort_performance";
        filter_sort_performance.innerText = "Performance";
        filter_sort_performance.style.cssText = sorter_styles;
        filter_sort_performance.addEventListener("mouseenter",
          () => filter_sort_performance.style.borderColor = "black");
        filter_sort_performance.addEventListener("mouseleave",
          () => filter_sort_performance.style.borderColor = "#A0A0A0");
        filter_sort_performance.addEventListener("click", () => {
          filter_sort_views.style.color = "black";
          filter_sort_performance.style.color = "#951D40";
          filter_sort_uid.style.color = "black";
          selected_sort = "performance";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        this.filter_dropdown.appendChild(filter_sort_performance);

        filter_sort_uid.id = "filter_sort_uid";
        filter_sort_uid.innerText = "Erstellungsdatum";
        filter_sort_uid.style.cssText = sorter_styles;
        filter_sort_uid.addEventListener("mouseenter",
          () => filter_sort_uid.style.borderColor = "black");
        filter_sort_uid.addEventListener("mouseleave",
          () => filter_sort_uid.style.borderColor = "#A0A0A0");
        filter_sort_uid.addEventListener("click", () => {
          filter_sort_views.style.color = "black";
          filter_sort_performance.style.color = "black";
          filter_sort_uid.style.color = "#951D40";
          selected_sort = "uid";

          this.filter.emit({accType: selected_account_filter, sort: selected_sort});
        })
        filter_sort_uid.style.color = "#951D40";
        this.filter_dropdown.appendChild(filter_sort_uid);

        // @ts-ignore
        document.getElementById("searchbar-box").appendChild(this.filter_dropdown);
      }
      if (this.shown) {
        // @ts-ignore
        this.filter_dropdown.style.display = "none";
        this.shown = false
      } else {
        // @ts-ignore
        this.filter_dropdown.style.display = "flex";
        this.shown = true;
      }
    }
    else if (this.shown) {
      // @ts-ignore
      this.filter_dropdown.style.display = "none";
      this.shown = false;
    }
  }

  protected readonly UserService = SysVars;
}
