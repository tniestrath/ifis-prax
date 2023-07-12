import {Component, ElementRef, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {SafeUrl} from "@angular/platform-browser";
import {UserService} from "../../services/user.service";


@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css']
})
export class SearchbarComponent implements OnInit{

  @Output() searchInput :string = "";
  @Output() currentSearch = new EventEmitter<string>();
  @Output() selected = new EventEmitter<DbObject>();
  @Output() filter = new EventEmitter<{ accType : string, perf : string }>();

  @Input() page : string = "placeholder";

  selectedSearch : DbObject = new DbObject("0", "");
  displaySearchBox: string = "";
  imgSrc: SafeUrl = "";

  filter_dropdown: HTMLDivElement | null = null;
  shown = false;
  logged_in = false;
  constructor(protected element : ElementRef, private cs : CookieService, private db : DbService) {
    UserService.login.subscribe(user => {
      this.logged_in = true;
    })
  }

  ngOnInit(): void {
    let object :string[]  = ["0",""];
      if (this.cs.check(this.page)) {
        object = this.cs.get(this.page).split(":");
      }
      this.onDbObjectSelected(object[0], object[1]);
  }

  onKey(value : string) {
    this.searchInput = value;
    this.currentSearch.emit(value);
  }

  onDbObjectSelected(id: string, name: string){
    let object : DbObject = new DbObject(id, name);

    if (id != "0"){
      this.selectedSearch = object;
      this.displaySearchBox = "0";
    }
    this.selected.emit(new DbObject(id, name));
    this.cs.set(this.page, object.id + ":" + object.name, {expires : 2});

    this.getImgSrc(this.selectedSearch.id);

    // @ts-ignore
    this.filter_dropdown.style.display = "none";
    this.shown = false;
  }

  onCancelClick(){
    if (UserService.ADMIN){
      this.selectedSearch = new DbObject("0", "");
      this.displaySearchBox = "50px";
      this.onKey("");
      this.onDbObjectSelected("0", "");
    } else {
      this.cs.deleteAll();
      location.reload();
    }
  }
  onLogoutClick() {
    this.cs.deleteAll();
    location.reload();
  }

  getImgSrc(id: string) {
    this.db.getUserImgSrc(id).then(dataUrl => {
      this.imgSrc = dataUrl;
    });
  }

  onFilterClick() {
    if (!document.getElementById("filter_dropdown")) {
      this.filter_dropdown = document.createElement("div");
      this.filter_dropdown.id = "filter_dropdown";
      this.filter_dropdown.style.width = "370px";
      this.filter_dropdown.style.height = "100px";
      this.filter_dropdown.style.position = "absolute";
      this.filter_dropdown.style.top = "60px";
      this.filter_dropdown.style.right = "5px";
      this.filter_dropdown.style.boxSizing = "border-box";
      this.filter_dropdown.style.border = "2px solid #A0A0A0";
      this.filter_dropdown.style.borderRadius = "5px";
      this.filter_dropdown.style.backgroundColor = "white";
      this.filter_dropdown.style.padding = "5px";
      this.filter_dropdown.style.display = "flex";
      this.filter_dropdown.style.flexDirection = "row";
      this.filter_dropdown.style.flexWrap = "wrap";

      let filter_styles = '' +
        'margin-right: 5px;' +
        'border: 1px solid #A0A0A0;' +
        'border-radius: 5px;' +
        'text-align: center;' +
        'width: 82px;' +
        'height: 20px;';

      let selected_account_filter = "all";
      let selected_performance_filter = "all";

      let label_accountType = document.createElement("div");
      let filter_accountTypeBasic = document.createElement("div");
      let filter_accountTypePlus = document.createElement("div");
      let filter_accountTypePremium = document.createElement("div");
      let filter_accountTypeAll = document.createElement("div");

      let label_performance = document.createElement("div");
      let filter_peformance33 = document.createElement("div");
      let filter_peformance66 = document.createElement("div");
      let filter_peformance100 = document.createElement("div");
      let filter_peformanceAll = document.createElement("div");

      label_accountType.innerText = "Abo Model";
      label_accountType.style.width = "100%";
      this.filter_dropdown.appendChild(label_accountType);

      filter_accountTypeBasic.id = "filter_type_basic";
      filter_accountTypeBasic.innerText = "Basic";
      filter_accountTypeBasic.style.cssText = filter_styles;

      filter_accountTypeBasic.addEventListener("mouseenter",
        () => filter_accountTypeBasic.style.borderColor = "black");
      filter_accountTypeBasic.addEventListener("mouseleave",
        () => filter_accountTypeBasic.style.borderColor = "#A0A0A0");
      filter_accountTypeBasic.addEventListener("click", () => {
        filter_accountTypeBasic.style.color = "#951D40";
        filter_accountTypePlus.style.color = "black";
        filter_accountTypePremium.style.color = "black";
        filter_accountTypeAll.style.color = "black";
        selected_account_filter = "basic";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      this.filter_dropdown.appendChild(filter_accountTypeBasic);


      filter_accountTypePlus.id = "filter_type_plus";
      filter_accountTypePlus.innerText = "Plus";
      filter_accountTypePlus.style.cssText = filter_styles;
      filter_accountTypePlus.addEventListener("mouseenter",
        () => filter_accountTypePlus.style.borderColor = "black");
      filter_accountTypePlus.addEventListener("mouseleave",
        () => filter_accountTypePlus.style.borderColor = "#A0A0A0");
      filter_accountTypePlus.addEventListener("click", () => {
        filter_accountTypeBasic.style.color = "black";
        filter_accountTypePlus.style.color = "#951D40";
        filter_accountTypePremium.style.color = "black";
        filter_accountTypeAll.style.color = "black";
        selected_account_filter = "plus";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
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
        filter_accountTypeBasic.style.color = "black";
        filter_accountTypePlus.style.color = "black";
        filter_accountTypePremium.style.color = "#951D40";
        filter_accountTypeAll.style.color = "black";
        selected_account_filter = "premium";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      this.filter_dropdown.appendChild(filter_accountTypePremium);

      filter_accountTypeAll.id = "filter_type_all";
      filter_accountTypeAll.innerText = "Alle";
      filter_accountTypeAll.style.cssText = filter_styles;
      filter_accountTypeAll.addEventListener("mouseenter",
        () => filter_accountTypeAll.style.borderColor = "black");
      filter_accountTypeAll.addEventListener("mouseleave",
        () => filter_accountTypeAll.style.borderColor = "#A0A0A0");
      filter_accountTypeAll.addEventListener("click", () => {
        filter_accountTypeBasic.style.color = "black";
        filter_accountTypePlus.style.color = "black";
        filter_accountTypePremium.style.color = "black";
        filter_accountTypeAll.style.color = "#951D40";
        selected_account_filter = "all";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      filter_accountTypeAll.style.color = "#951D40";
      this.filter_dropdown.appendChild(filter_accountTypeAll);

      label_performance.innerText = "Performance";
      label_performance.style.width = "100%";
      this.filter_dropdown.appendChild(label_performance);

      filter_peformance33.id = "filter_performance_33";
      filter_peformance33.innerText = "Niedrig";
      filter_peformance33.style.cssText = filter_styles;
      filter_peformance33.addEventListener("mouseenter",
        () => filter_peformance33.style.borderColor = "black");
      filter_peformance33.addEventListener("mouseleave",
        () => filter_peformance33.style.borderColor = "#A0A0A0");
      filter_peformance33.addEventListener("click", () => {
        filter_peformance33.style.color = "#951D40";
        filter_peformance66.style.color = "black";
        filter_peformance100.style.color = "black";
        filter_peformanceAll.style.color = "black";
        selected_performance_filter = "low";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      this.filter_dropdown.appendChild(filter_peformance33);

      filter_peformance66.id = "filter_performance_66";
      filter_peformance66.innerText = "Mittel";
      filter_peformance66.style.cssText = filter_styles;
      filter_peformance66.addEventListener("mouseenter",
        () => filter_peformance66.style.borderColor = "black");
      filter_peformance66.addEventListener("mouseleave",
        () => filter_peformance66.style.borderColor = "#A0A0A0");
      filter_peformance66.addEventListener("click", () => {
        filter_peformance33.style.color = "black";
        filter_peformance66.style.color = "#951D40";
        filter_peformance100.style.color = "black";
        filter_peformanceAll.style.color = "black";
        selected_performance_filter = "medium";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      this.filter_dropdown.appendChild(filter_peformance66);

      filter_peformance100.id = "filter_performance_100";
      filter_peformance100.innerText = "Hoch";
      filter_peformance100.style.cssText = filter_styles;
      filter_peformance100.addEventListener("mouseenter",
        () => filter_peformance100.style.borderColor = "black");
      filter_peformance100.addEventListener("mouseleave",
        () => filter_peformance100.style.borderColor = "#A0A0A0");
      filter_peformance100.addEventListener("click", () => {
        filter_peformance33.style.color = "black";
        filter_peformance66.style.color = "black";
        filter_peformance100.style.color = "#951D40";
        filter_peformanceAll.style.color = "black";
        selected_performance_filter = "high";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      this.filter_dropdown.appendChild(filter_peformance100);

      filter_peformanceAll.id = "filter_performance_all";
      filter_peformanceAll.innerText = "Alle";
      filter_peformanceAll.style.cssText = filter_styles;
      filter_peformanceAll.addEventListener("mouseenter",
        () => filter_peformanceAll.style.borderColor = "black");
      filter_peformanceAll.addEventListener("mouseleave",
        () => filter_peformanceAll.style.borderColor = "#A0A0A0");
      filter_peformanceAll.addEventListener("click", () => {
        filter_peformance33.style.color = "black";
        filter_peformance66.style.color = "black";
        filter_peformance100.style.color = "black";
        filter_peformanceAll.style.color = "#951D40";
        selected_performance_filter = "all";

        this.filter.emit({accType: selected_account_filter, perf: selected_performance_filter});
      })
      filter_peformanceAll.style.color = "#951D40";
      this.filter_dropdown.appendChild(filter_peformanceAll);

      this.element.nativeElement.appendChild(this.filter_dropdown);
    }
    if (this.shown) {
      // @ts-ignore
      this.filter_dropdown.style.display = "none";
      this.shown = !this.shown;
    } else {
      // @ts-ignore
      this.filter_dropdown.style.display = "flex";
      this.shown = !this.shown;
    }
  }

  protected readonly UserService = UserService;
}
