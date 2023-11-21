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
import {TagChartComponent} from "../../component/tag/tag-chart/tag-chart.component";
import {CallUpChartComponent} from "../../component/call-up-chart/call-up-chart.component";
import {
  Top5ArticleComponent,
  Top5BlogComponent, Top5NewsComponent,
  Top5PostsComponent, Top5WhitepaperComponent
} from "../../component/post/top5-posts/top5-posts.component";
import {NewsletterStatsComponent} from "../../component/newsletter-stats/newsletter-stats.component";
import {SystemloadComponent} from "../../component/system/systemload/systemload.component";
import {EventsStatsComponent} from "../../component/events-stats/events-stats.component";
import {PostTypeComponent} from "../../component/post/post-type/post-type.component";

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
  filterValues : { accType : string, sort : string } = {accType: "all", sort : "uid"};

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
      {type: TagListComponent, row: 1, col: 5, height: 2, width: 2},
      {type: TagPieComponent, row: 3, col: 5, height: 2, width: 2},
      {type: TagChartComponent, row: 1, col: 1, height: 2, width: 4}
    ];
  }
  getPostsPageCards() {
    return [
      {type: PostListComponent, row: 1, col: 1, height: 4, width: 2},
      {type: Top5ArticleComponent, row: 1, col: 3, height: 1, width: 4},
      {type: Top5BlogComponent, row: 2, col: 3, height: 1, width: 4},
      {type: Top5NewsComponent, row: 3, col: 3, height: 1, width: 4},
      {type: Top5WhitepaperComponent, row: 4, col: 3, height: 1, width: 4}
    ];
  }

  getAdminPageCards() {
    return [
      {type: UserPlanComponent, row: 1, col: 1, height: 2, width: 1},
      {type: CallUpChartComponent, row: 1, col: 2, height: 2, width: 4},
      {type: NewsletterStatsComponent, row: 1, col: 6, height: 1, width: 1},
      {type: SystemloadComponent, row: 3, col: 5, height: 2, width: 2},
      {type: PostTypeComponent, row: 3, col: 1, height: 2, width: 1},
      {type: OriginMapComponent, row: 3, col: 2, height: 2, width: 3},
      {type: EventsStatsComponent, row: 2, col: 6, height: 1, width: 1}
    ];
  }

  getStatsPageCards() {
    return [
      {type: CallUpChartComponent, row: 1, col: 2, height: 2, width: 4}
    ];
  }

  onSelected(id: string, name: string) {
    if (id != "0") {
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

  onFilterChange(filter: { accType: string; sort: string }){
    this.filterValues = {accType : filter.accType, sort : filter.sort};
    this.loadSelector(this.filterValues)
  }

  ngOnInit(): void {
    this.pageSelected.subscribe(page => {
      SysVars.CURRENT_PAGE = page;
      console.log(page);
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
          this.db.resetStatus();
          break;
        }
        case "Tags":{
          this.cardsLoaded.next(this.getTagsPageCards());
          this.db.resetStatus();
          break;
        }
        case "Posts":{
          this.cardsLoaded.next(this.getPostsPageCards());
          this.db.resetStatus();
          break;
        }
        case "Overview":{
          this.cardsLoaded.next(this.getAdminPageCards());
          this.db.resetStatus();
          break;
        }
        case "Statistics":{
          this.cardsLoaded.next(this.getStatsPageCards());
          this.db.resetStatus();
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

  loadSelector(filter: {accType : string, sort : string}){
      this.db.loadAllUsers().then(() => {
        this.selectorItems = [];
        for (let u of DbService.Users) {
          this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, u.profileViews, u.postViews, u.postCount, u.performance, u.accountType, u.potential, u.img)));
        }
      }).then(() => {
        this.selectorItems = this.selectorItems.filter(item => item.data.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
          (item.data as User).email.toUpperCase().includes(this.searchValue.toUpperCase()))
      }).then(() => {
        // @ts-ignore
        if (filter.accType != "all"){
          this.selectorItems = this.selectorItems.filter((item ) => (item.data as User).accountType == filter.accType);
        }
        switch (filter.sort) {
          case "uid": {
            this.selectorItems = this.selectorItems.sort((a, b) => (Number(a.data.id) - Number(b.data.id)));
            break;
          }
          case "views": {
            this.selectorItems = this.selectorItems.sort((a, b) => ( (b.data as User).profileViews + (b.data as User).postViews ) - ( (a.data as User).profileViews + (a.data as User).postViews ) );
            break;
          }
          case "performance": {
            this.selectorItems = this.selectorItems.sort((a, b) => (b.data as User).performance - (a.data as User).performance);
            break;
          }
          default: break;
        }
      }).finally(() =>
        this.selectorItemsLoaded.next(this.selectorItems));
    }
}
