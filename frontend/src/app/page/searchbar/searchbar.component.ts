import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {DbObject} from "../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";
import {SysVars} from "../../services/sys-vars-service";
import {Subject} from "rxjs";
import {DashBaseComponent} from "../../component/dash-base/dash-base.component";
import {DashColors} from "../../util/Util";


@Component({
  selector: 'dash-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrls: ['./searchbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchbarComponent extends DashBaseComponent implements OnInit{

  @Output() searchInput :string = "";
  lastSearchInput : string = "";
  @Output() currentSearch = new EventEmitter<string>();
  @Output() selected = new EventEmitter<DbObject>();
  @Output() filter = new EventEmitter<{ accType : string, usrType : string, sort : string }>();
  @Output() compare = new EventEmitter<void>();
  @Input('reset') reset = new Subject<boolean>();
  @ViewChild(HTMLInputElement, {static : true}) search!: HTMLInputElement;
  page : string = "user";

  selectedSearch : DbObject = new DbObject("0", "");
  searchSuggestions : string[] = [];
  displaySearchBox: string = "";
  imgSrc: SafeUrl = "";

  shown = false;

  selectedAccFilterString : string = "Alle";
  selectedUsrFilterString : string = "Alle";

  selected_account_filter = "";
  selected_user_filter = "";

  ngOnInit(): void {
    this.setupFilter();
    this.reset.subscribe(value => {
      this.onReset();
    });
  }

  onKey(value? : string) {
    if (value == null){return}
    this.searchInput = value;
  }
  onEnter(value : string){
    console.log(value)
    if (value == "Enter"){
      this.currentSearch.emit(this.searchInput);
      this.searchSuggestions = [];
      this.cdr.detectChanges();
    }
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
    this.api.getUserImgSrc(id).then(dataUrl => {
      this.imgSrc = dataUrl;
    });
  }

  setupFilter() {
    this.selected_account_filter = "";
    this.selected_user_filter = "";

    let selected_sort = "userId";

    let filterBoxes = (this.element.nativeElement as HTMLElement).querySelectorAll(".searchbar-filter-type") as NodeListOf<HTMLDivElement>;
    let accFilters = (this.element.nativeElement as HTMLElement).querySelectorAll(".searchbar-filter-acc-type") as NodeListOf<HTMLDivElement>;
    let usrFilters = (this.element.nativeElement as HTMLElement).querySelectorAll(".searchbar-filter-usr-type") as NodeListOf<HTMLDivElement>;

    let filter_sort_views = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-views") as HTMLDivElement;
    let filter_sort_contentViews = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-content-views") as HTMLDivElement;
    let filter_sort_viewsByTime = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-views-by-time") as HTMLDivElement;
    let filter_sort_uid = (this.element.nativeElement as HTMLElement).querySelector("#searchbar-sorter-uid") as HTMLDivElement;

    filterBoxes.forEach(item => {
      item.addEventListener("mouseenter", ev => {
        item.childNodes.forEach(item => {
          if (item.nodeType == 1){
            (item as HTMLDivElement).style.display = "block";
          }
        });
      });
      item.addEventListener("mouseleave", ev => {
        item.childNodes.forEach(item => {
          if (item.nodeType == 1){
            (item as HTMLDivElement).style.display = "none";
          }
        });
      });
    });

    accFilters.forEach(filter => {
      filter.addEventListener("click", () => {
        accFilters.forEach(otherFilter => {
          otherFilter.style.color = "black";
          otherFilter.style.fontWeight = "normal";
        });
        filter.style.color = DashColors.RED;
        filter.style.fontWeight = "bold";
        switch (filter.id) {
          case "searchbar-filter-accountType-without-plan":
            this.selected_account_filter = "\"um_anbieter\"";
            this.selectedAccFilterString = "Ohne Abo";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-accountType-basic":
            this.selected_account_filter = "\"um_basis\"";
            this.selectedAccFilterString = "Basis";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-accountType-basicPlus":
            this.selected_account_filter = "\"um_basis-plus\"";
            this.selectedAccFilterString = "Basis+";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-accountType-plus":
            this.selected_account_filter = "\"um_plus\"";
            this.selectedAccFilterString = "Plus";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-accountType-premium":
            this.selected_account_filter = "\"um_premium\"";
            this.selectedAccFilterString = "Premium";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-accountType-all":
            this.selected_account_filter = "";
            this.selectedAccFilterString = "Alle";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
        }
      });
    });

    usrFilters.forEach(filter => {
      filter.addEventListener("click", () => {
        usrFilters.forEach(otherFilter => {
          otherFilter.style.color = "black";
          otherFilter.style.fontWeight = "normal";
        });
        filter.style.color = DashColors.RED;
        filter.style.fontWeight = "bold";
        switch (filter.id) {
          case "searchbar-filter-userType-startup":
            this.selected_user_filter = "Startup";
            this.selectedUsrFilterString = "Startup";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-userType-middle":
            this.selected_user_filter = "Mittelstand";
            this.selectedUsrFilterString = "Mittelstand";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-userType-corporate":
            this.selected_user_filter = "Großkonzern";
            this.selectedUsrFilterString = "Großkonzern";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-userType-uni":
            this.selected_user_filter = "Hochschule";
            this.selectedUsrFilterString = "Hochschule";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-userType-collective":
            this.selected_user_filter = "Verband";
            this.selectedUsrFilterString = "Verband";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
          case "searchbar-filter-userType-all":
            this.selected_user_filter = "";
            this.selectedUsrFilterString = "Alle";
            this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
            break;
        }
      });
    });

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

      this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
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

      this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
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

      this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
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

      this.filter.emit({accType: this.selected_account_filter, usrType: this.selected_user_filter, sort: selected_sort});
    })
    filter_sort_uid.style.color = "#951D40";
    filter_sort_uid.style.fontWeight = "bold";
  }

  protected readonly UserService = SysVars;

  onCompareClick() {
    this.compare.emit();
  }

  getSuggestions(value: string, abo : string, typ : string) {
    if (value == this.lastSearchInput){return this.searchSuggestions}
    if (value == ""){this.searchSuggestions = []; return [];}
    this.api.getUserSearchSuggestions(value, abo, typ).then(res => {
      this.searchSuggestions = res;
      this.cdr.detectChanges();
    });
    this.lastSearchInput = value;
    return this.searchSuggestions;
  }

  onSuggestionClick(user : string){
    this.currentSearch.emit(user);
    this.searchSuggestions = [];
  }
}
