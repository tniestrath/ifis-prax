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
import {TagListComponent} from "../../component/tag/tag-list/tag-list.component";
import {TagPieComponent} from "../../component/tag/tag-pie/tag-pie.component";
import {PodcastListComponent, PostListComponent, RatgeberListComponent} from "../../component/post/post-list/post-list.component";
import {TagChartComponent} from "../../component/tag/tag-chart/tag-chart.component";
import {CallUpChartComponent} from "../../component/call-up-chart/call-up-chart.component";
import {Top5ArticleComponent, Top5BlogComponent, Top5NewsComponent, Top5WhitepaperComponent} from "../../component/post/top5-posts/top5-posts.component";
import {NewsletterStatsComponent} from "../../component/newsletter-stats/newsletter-stats.component";
import {SystemloadComponent} from "../../component/system/systemload/systemload.component";
import {EventsStatsComponent} from "../../component/events-stats/events-stats.component";
import {PostTypeComponent} from "../../component/post/post-type/post-type.component";
import {SearchbarComponent} from "../searchbar/searchbar.component";
import {ProfileCompletionComponent} from "../../component/profile-completion/profile-completion.component";
import {UserListComponent} from "./user/user-list/user-list.component";
import {UserComparatorComponent} from "./user/user-comparator/user-comparator.component";
@Component({
  selector: 'dash-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {
  displayContent: string = "none";

  resetSearchbar : Subject<boolean> = new Subject<boolean>();

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
      {type: UserComparatorComponent, row: 1, col: 1, height: 4, width: 6},

    ];
  }

  getUserDetailPageCards() {
    return [
      {type: ClicksComponent, row: 1, col: 1, height: 4, width: 1},
      //@ts-ignore
      {type: PostChartComponent, row: 1, col: 2, height: 2, width: 4},
      //@ts-ignore
      {type: GaugeComponent, row: 4, col: 6, height: 1, width: 1},
      {type: RelevanceComponent, row: 3, col: 6, height: 1, width: 1},
      //@ts-ignore
      {type: PostComponent, row: 1, col: 6, height: 2, width: 1},
      {type: ProfileCompletionComponent, row: 3, col: 2, height: 2, width: 2},
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

  getOverviewPageCards() {
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

  getContentPageCards() {
    return [
      {type: PodcastListComponent,  row: 1, col: 1, height: 4, width: 2},
      {type: RatgeberListComponent,  row: 1, col: 5, height: 4, width: 2}
    ];
  }

  ngOnInit(): void {
    this.pageSelected.subscribe(page => {
      SysVars.CURRENT_PAGE = page;
      this.displayContent = "grid";
      switch (page) {
        case "Anbieter":{
          this.cardsLoaded.next(this.getUserPageCards());
          this.resetSearchbar.next(true);
          this.db.resetStatus();
          break;
        }
        case "Themen":{
          this.cardsLoaded.next(this.getTagsPageCards());
          this.db.resetStatus();
          break;
        }
        case "Beiträge":{
          this.cardsLoaded.next(this.getPostsPageCards());
          this.db.resetStatus();
          break;
        }
        case "Übersicht":{
          this.cardsLoaded.next(this.getOverviewPageCards());
          this.db.resetStatus();
          break;
        }
        case "Inhalte":{
          this.cardsLoaded.next(this.getContentPageCards());
          this.db.resetStatus();
          break;
        }
        case "Login":{
          this.cardsLoaded.next(this.getLandingPageCards());
          break;
        }
        default: {
          this.cardsLoaded.next(this.getLandingPageCards());
          break;
        }
      }
    });
    SysVars.SELECTED_USER_ID.subscribe(id => {
      SysVars.USER_ID = String(id);
      this.cardsLoaded.next(this.getUserDetailPageCards());
      this.db.resetStatus();
    })

  }
}
