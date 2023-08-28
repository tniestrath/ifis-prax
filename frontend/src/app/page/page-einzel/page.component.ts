import {Component, Input, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {SelectorItem} from "../selector/selector.component";
import {DbService} from "../../services/db.service";
import {UserComponent} from "./user/user.component";
import {Observable, Subject} from "rxjs";
import {ClicksComponent} from "../../component/clicks/clicks.component";
import {PostChartComponent} from "../../component/post/post-chart/post-chart.component";
import {GaugeComponent} from "../../component/gauge/gauge.component";
import {GridCard} from "../../grid/GridCard";
import {RelevanceComponent} from "../../component/gauge/relevance/relevance.component";
import {PostComponent} from "../../component/post/post.component";
import {SysVars} from "../../services/sys-vars-service";
import {UserPlanComponent} from "../../component/user-plan/user-plan.component";
import {LoginComponent} from "../../component/login/login.component";
import {User} from "./user/user";
import {OriginMapComponent} from "../../component/origin-map/origin-map.component";
import {ClicksByTimeComponent} from "../../component/clicks-by-time/clicks-by-time.component";
import {TagListComponent} from "../../component/tag/tag-list/tag-list.component";
import {TagPieComponent} from "../../component/tag/tag-pie/tag-pie.component";
import {PostListComponent} from "../../component/post/post-list/post-list.component";

@Component({
  selector: 'dash-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {
  displayContent: string = "none";

  selectorItems : SelectorItem[] = [];
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  searchValue = "";
  filterValues : { accType : string, perf : string } = {accType: "all", perf : "all"};

  @Input() pageSelected = new Observable<string>;
  cardsLoaded = new Subject<GridCard[]>();

  constructor(private cookieService : CookieService, private db : DbService) {
  }

  getLandingPageCards(){
    return [
      {type: LoginComponent, row: 2, col: 3, height: 2, width: 2},
    ];
  }

  getUserPageCards() {
    return [
      {type: ClicksComponent, row: 1, col: 1, height: 4, width: 1},
      //@ts-ignore
      {type: PostChartComponent, row: 1, col: 2, height: 2, width: 4},
      //@ts-ignore
      {type: GaugeComponent, row: 4, col: 6, height: 1, width: 1},
      {type: RelevanceComponent, row: 3, col: 6, height: 1, width: 1},
      //@ts-ignore
      {type: PostComponent, row: 1, col: 6, height: 2, width: 1},
      {type: ClicksByTimeComponent, row: 3, col: 2, height: 2, width: 2},
      {type: OriginMapComponent, row: 3, col: 4, height: 2, width: 2}
    ];
  }
  getTagsPageCards() {
    return [
      {type: TagListComponent, row: 1, col: 1, height: 4, width: 2},
      {type: TagPieComponent, row: 1, col: 3, height: 2, width: 2}
    ];
  }
  getPostsPageCards() {
    return [
      {type: PostListComponent, row: 1, col: 1, height: 3, width: 3},
    ];
  }

  getAdminPageCards() {
    return [
      {type: UserPlanComponent, row: 1, col: 1, height: 2, width: 1},
      {type: OriginMapComponent, row: 1, col: 2, height: 2, width: 2},
      {type: ClicksByTimeComponent, row: 1, col: 4, height: 2, width: 2}

    ];
  }

  onSelected(id: string, name: string){
    if (id != "0"){
      this.displayContent = "grid";
      this.cardsLoaded.next(this.getUserPageCards());
      SysVars.CURRENT_PAGE = "Users";
    } else {
      this.displayContent = "none";
    }
    SysVars.USER_ID = id;
  }

  onSearchInput(value : string){
    this.searchValue = value;
    this.loadSelector(this.filterValues);
  }

  onFilterChange(filter : {accType : string, perf : string}){
    this.filterValues = {accType : filter.accType, perf : filter.perf};
    this.loadSelector(this.filterValues)
  }

  ngOnInit(): void {
    this.pageSelected.subscribe(page => {
      SysVars.CURRENT_PAGE = page;
      console.log(page + " : "+ SysVars.USER_ID);
      this.displayContent = "grid";
      switch (page) {
        case "Users":{
          if (SysVars.USER_ID != "0" && SysVars.ADMIN){
            this.displayContent = "grid";
            this.cardsLoaded.next(this.getUserPageCards());
          } else {
            this.displayContent = "none";
            this.loadSelector(this.filterValues);
          }
          break;
        }
        case "Tags":{
          this.cardsLoaded.next(this.getTagsPageCards());
          break;
        }
        case "Posts":{
          this.cardsLoaded.next(this.getPostsPageCards());
          break;
        }
        case "Overview":{
          this.cardsLoaded.next(this.getAdminPageCards());
          break;
        }
        case "Landing":{
          this.cardsLoaded.next(this.getLandingPageCards());
          break;
        }
        default: {
          this.cardsLoaded.next(this.getLandingPageCards());
          break;
        }
      }
    })

  }

  loadSelector(filter: {accType : string, perf : string}){
    this.db.getMaxPerformance().then(res => {
      const max_performance = res;
      this.db.loadAllUsers().then(() => {
        this.selectorItems = [];
        for (let u of DbService.Users) {
          let performance = ((u.performance || 0) / max_performance)*100;
          if (performance <= 33){
            this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, u.profileViews, u.postViews, u.postCount, 33, u.accountType, u.potential, u.img)));
          } if (performance > 33 && performance <= 66){
            this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, u.profileViews, u.postViews, u.postCount, 33, u.accountType, u.potential, u.img)));
          } if (performance > 66){
            this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, u.profileViews, u.postViews, u.postCount, 33, u.accountType, u.potential, u.img)));
          }

        }
      }).then(() => {
        this.selectorItems = this.selectorItems.filter(item => item.data.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
          (item.data as User).email.toUpperCase().includes(this.searchValue.toUpperCase()))
      }).then(() => {
        // @ts-ignore
        if (filter.accType != "all"){
          this.selectorItems = this.selectorItems.filter((item ) => (item.data as User).accountType == filter.accType);
        }
        switch (filter.perf) {
          case "low": {
            this.selectorItems = this.selectorItems.filter(item => (item.data as User).performance < 33);
            break;
          }
          case "medium": {
            this.selectorItems = this.selectorItems.filter(item => (item.data as User).performance >= 33 && (item.data as User).performance < 66);
            break;
          }
          case "high": {
            this.selectorItems = this.selectorItems.filter(item => (item.data as User).performance >= 66);
            break;
          }
          default: break;
        }
      }).finally(() =>
        this.selectorItemsLoaded.next(this.selectorItems));
    })
  }
}
