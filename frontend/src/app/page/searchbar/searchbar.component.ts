import {Component, ElementRef, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {SafeUrl} from "@angular/platform-browser";


@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css']
})
export class SearchbarComponent implements OnInit{

  @Output() searchInput :string = "";
  @Output() currentSearch = new EventEmitter<string>();
  @Output() selected = new EventEmitter<DbObject>();

  @Input() page : string = "placeholder";

  selectedSearch : DbObject = new DbObject("0", "");
  displaySearchBox: string = "";
  imgSrc: SafeUrl = "";

  filter_dropdown: HTMLDivElement | null = null;
  constructor(protected element : ElementRef, private cookieService : CookieService, private db : DbService) {
  }

  ngOnInit(): void {
    let object :string[]  = ["0",""];
    if(this.cookieService.check(this.page)){
      object = this.cookieService.get(this.page).split(":");
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
    this.cookieService.set(this.page, object.id + ":" + object.name, {expires : 7});

    this.getImgSrc(this.selectedSearch.id);

    this.filter_dropdown?.remove();
  }

  onCancelClick(){
    this.selectedSearch = new DbObject("0", "");
    this.displaySearchBox = "50px";
    this.onKey("");
    this.onDbObjectSelected("0", "");
  }

  getImgSrc(id: string) {
    this.db.getUserImgSrc(id).then(dataUrl => {
      this.imgSrc = dataUrl;
    });
  }

  onFilterClick() {
    if (!document.getElementById("filter_dropdown")){
      this.filter_dropdown = document.createElement("div");
      this.filter_dropdown.id = "filter_dropdown";
      this.filter_dropdown.style.width = "370px";
      this.filter_dropdown.style.height = "200px";
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

      let filter_styles = '' +
        'margin-right: 5px;' +
        'border: 1px solid #A0A0A0;' +
        'border-radius: 5px;' +
        'text-align: center;' +
        'width: 100px;' +
        'height: 20px;';

      let selected_filter = "all";

      let filter_accountTypeBasic = document.createElement("div");
      let filter_accountTypePlus = document.createElement("div");
      let filter_accountTypePremium = document.createElement("div");
      let filter_accountTypeAll = document.createElement("div");

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
        selected_filter = "basic";
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
        selected_filter = "plus";
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
        selected_filter = "premium";
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
        selected_filter = "all";
      })
      this.filter_dropdown.appendChild(filter_accountTypeAll);

      this.element.nativeElement.appendChild(this.filter_dropdown);
    } else {
      this.filter_dropdown?.remove();
    }
  }
}
