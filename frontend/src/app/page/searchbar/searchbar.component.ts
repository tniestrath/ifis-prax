import {ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {SafeUrl} from "@angular/platform-browser";
import {SysVars} from "../../services/sys-vars-service";
import {Subject} from "rxjs";
import {DashBaseComponent} from "../../component/dash-base/dash-base.component";
import {PdfService} from "../../services/pdf.service";


@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css']
})
export class SearchbarComponent extends DashBaseComponent implements OnInit{

  @Output() searchInput :string = "";
  @Output() currentSearch = new EventEmitter<string>();
  @Output() selected = new EventEmitter<DbObject>();
  @Output() filter = new EventEmitter<{ accType : string, sort : string }>();
  @Output() compare = new EventEmitter<void>();
  @Input('reset') reset = new Subject<boolean>();

  page : string = "user";

  selectedSearch : DbObject = new DbObject("0", "");
  displaySearchBox: string = "";
  imgSrc: SafeUrl = "";

  shown = false;

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
    let selected_account_filter = " ";
    let selected_sort = "userId";

    let filter_accountTypeWithoutPlan = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-filter-accountType-without-plan") as HTMLDivElement;
    let filter_accountTypeBasic = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-filter-accountType-basic") as HTMLDivElement;
    let filter_accountTypeBasicPlus = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-filter-accountType-basicPlus") as HTMLDivElement;
    let filter_accountTypePlus = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-filter-accountType-plus") as HTMLDivElement;
    let filter_accountTypePremium = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-filter-accountType-premium") as HTMLDivElement;
    let filter_accountTypeAll = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-filter-accountType-all") as HTMLDivElement;

    let filter_sort_views = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-views") as HTMLDivElement;
    let filter_sort_contentViews = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-content-views") as HTMLDivElement;
    let filter_sort_viewsByTime = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-views-by-time") as HTMLDivElement;
    let filter_sort_uid = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-uid") as HTMLDivElement;

    filter_accountTypeWithoutPlan.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "#951D40";
      filter_accountTypeWithoutPlan.style.fontWeight = "bold";

      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasic.style.fontWeight = "normal";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypeBasicPlus.style.fontWeight = "normal";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePlus.style.fontWeight = "normal";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypePremium.style.fontWeight = "normal";
      filter_accountTypeAll.style.color = "black";
      filter_accountTypeAll.style.fontWeight = "normal";
      selected_account_filter = "none";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypeBasic.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeBasic.style.color = "#951D40";
      filter_accountTypeBasic.style.fontWeight = "bold";

      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypeBasicPlus.style.fontWeight = "normal";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePlus.style.fontWeight = "normal";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypePremium.style.fontWeight = "normal";
      filter_accountTypeAll.style.color = "black";
      filter_accountTypeAll.style.fontWeight = "normal";
      selected_account_filter = "basis";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypeBasicPlus.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeWithoutPlan.style.fontWeight = "normal";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasic.style.fontWeight = "normal";

      filter_accountTypeBasicPlus.style.color = "#951D40";
      filter_accountTypeBasicPlus.style.fontWeight = "bold";

      filter_accountTypePlus.style.color = "black";
      filter_accountTypePlus.style.fontWeight = "normal";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypePremium.style.fontWeight = "normal";
      filter_accountTypeAll.style.color = "black";
      filter_accountTypeAll.style.fontWeight = "normal";
      selected_account_filter = "basis-plus";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypePlus.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeWithoutPlan.style.fontWeight = "normal";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasic.style.fontWeight = "normal";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypeBasicPlus.style.fontWeight = "normal";

      filter_accountTypePlus.style.color = "#951D40";
      filter_accountTypePlus.style.fontWeight = "bold";

      filter_accountTypePremium.style.color = "black";
      filter_accountTypePremium.style.fontWeight = "normal";
      filter_accountTypeAll.style.color = "black";
      filter_accountTypeAll.style.fontWeight = "normal";
      selected_account_filter = "plus";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_accountTypePremium.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeWithoutPlan.style.fontWeight = "normal";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasic.style.fontWeight = "normal";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypeBasicPlus.style.fontWeight = "normal";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePlus.style.fontWeight = "normal";

      filter_accountTypePremium.style.color = "#951D40";
      filter_accountTypePremium.style.fontWeight = "bold";

      filter_accountTypeAll.style.color = "black";
      selected_account_filter = "premium";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })


    filter_accountTypeAll.addEventListener("click", () => {
      filter_accountTypeWithoutPlan.style.color = "black";
      filter_accountTypeWithoutPlan.style.fontWeight = "normal";
      filter_accountTypeBasic.style.color = "black";
      filter_accountTypeBasic.style.fontWeight = "normal";
      filter_accountTypeBasicPlus.style.color = "black";
      filter_accountTypeBasicPlus.style.fontWeight = "normal";
      filter_accountTypePlus.style.color = "black";
      filter_accountTypePlus.style.fontWeight = "normal";
      filter_accountTypePremium.style.color = "black";
      filter_accountTypePremium.style.fontWeight = "normal";

      filter_accountTypeAll.style.color = "#951D40";
      filter_accountTypeAll.style.fontWeight = "bold";
      selected_account_filter = " ";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })
    filter_accountTypeAll.style.color = "#951D40";
    filter_accountTypeAll.style.fontWeight = "bold";


    filter_sort_views.addEventListener("click", () => {
      filter_sort_views.style.color = "#951D40";
      filter_sort_views.style.fontWeight = "bold";

      filter_sort_contentViews.style.color = "black";
      filter_sort_contentViews.style.fontWeight = "normal";
      filter_sort_viewsByTime.style.color = "black";
      filter_sort_viewsByTime.style.fontWeight = "normal";
      filter_sort_uid.style.color = "black";
      filter_sort_uid.style.fontWeight = "normal";
      selected_sort = "profileView";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_sort_contentViews.addEventListener("click", () => {
      filter_sort_views.style.color = "black";
      filter_sort_views.style.fontWeight = "normal";

      filter_sort_contentViews.style.color = "#951D40";
      filter_sort_contentViews.style.fontWeight = "bold";

      filter_sort_viewsByTime.style.color = "black";
      filter_sort_viewsByTime.style.fontWeight = "normal";
      filter_sort_uid.style.color = "black";
      filter_sort_uid.style.fontWeight = "normal";
      selected_sort = "contentView";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_sort_viewsByTime.addEventListener("click", () => {
      filter_sort_views.style.color = "black";
      filter_sort_views.style.fontWeight = "normal";
      filter_sort_contentViews.style.color = "black";
      filter_sort_contentViews.style.fontWeight = "normal";

      filter_sort_viewsByTime.style.color = "#951D40";
      filter_sort_viewsByTime.style.fontWeight = "bold";

      filter_sort_uid.style.color = "black";
      filter_sort_uid.style.fontWeight = "normal";
      selected_sort = "viewsByTime";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })

    filter_sort_uid.addEventListener("click", () => {
      filter_sort_views.style.color = "black";
      filter_sort_views.style.fontWeight = "normal";
      filter_sort_contentViews.style.color = "black";
      filter_sort_contentViews.style.fontWeight = "normal";
      filter_sort_viewsByTime.style.color = "black";
      filter_sort_viewsByTime.style.fontWeight = "normal";

      filter_sort_uid.style.color = "#951D40";
      filter_sort_uid.style.fontWeight = "bold";
      selected_sort = "userId";

      this.filter.emit({accType: selected_account_filter, sort: selected_sort});
    })
    filter_sort_uid.style.color = "#951D40";
    filter_sort_uid.style.fontWeight = "bold";
  }

  protected readonly UserService = SysVars;

  onCompareClick() {
    this.compare.emit();
  }
}
