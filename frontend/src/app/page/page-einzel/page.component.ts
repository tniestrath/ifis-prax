import {Component, Input, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbService} from "../../services/db.service";
import {Observable, Subject} from "rxjs";
import {ClicksComponent} from "../../component/clicks/clicks.component";
import {PostChartComponent} from "../../component/post/post-chart/post-chart.component";
import {GaugeComponent} from "../../component/gauge/gauge.component";
import {GridCard} from "../../grid/GridCard";
import {RelevanceComponent} from "../../component/gauge/relevance/relevance.component";
import {SysVars} from "../../services/sys-vars-service";
import {UserPlanComponent} from "../../component/user-plan/user-plan.component";
import {LoginComponent} from "../../component/login/login.component";
import {OriginMapComponent} from "../../component/origin-map/origin-map.component";
import {TagListComponent} from "../../component/tag/tag-list/tag-list.component";
import {TagPieComponent} from "../../component/tag/tag-pie/tag-pie.component";
import {
  EventListComponent,
  PodcastListComponent,
  PostListComponent,
  RatgeberListComponent, UserEventListComponent,
  UserPostListComponent
} from "../../component/post/post-list/post-list.component";
import {TagChartComponent} from "../../component/tag/tag-chart/tag-chart.component";
import {CallUpChartComponent} from "../../component/call-up-chart/call-up-chart.component";
import {Top5ArticleComponent, Top5BlogComponent, Top5NewsComponent, Top5WhitepaperComponent} from "../../component/post/top5-posts/top5-posts.component";
import {NewsletterStatsComponent} from "../../component/newsletter-stats/newsletter-stats.component";
import {SystemloadComponent} from "../../component/system/systemload/systemload.component";
import {EventsStatsComponent, UserEventsStatsComponent} from "../../component/events-stats/events-stats.component";
import {PostTypeComponent} from "../../component/post/post-type/post-type.component";
import {ProfileCompletionComponent} from "../../component/profile-completion/profile-completion.component";
import {UserComparatorComponent} from "./user/user-comparator/user-comparator.component";
import {
  UserStatsByPlanComponent,
  UserStatsByPlanViewTypeCompareComponent
} from "./user/user-stats-by-plan/user-stats-by-plan.component";
import {UserDisplayComponentComponent} from "./user/user-display-component/user-display-component.component";
import {UserClicksChartComponent} from "./user/user-clicks-chart/user-clicks-chart.component";
import {UserTagsDistComponent} from "./user/user-tags-dist/user-tags-dist.component";
@Component({
  selector: 'dash-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {

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
      {type: UserStatsByPlanComponent, row: 1, col: 4, height: 1, width: 3},
      {type: UserStatsByPlanViewTypeCompareComponent, row: 2, col: 4, height: 1, width: 3},
      {type: UserTagsDistComponent, row: 3, col: 4, height: 2, width: 2}
    ];
  }

  getUserDetailPageCards() {
    return [
      {type: ClicksComponent, row: 1, col: 1, height: 4, width: 1},
      {type: UserClicksChartComponent, row: 1, col: 2, height: 2, width: 3},
      {type: ProfileCompletionComponent, row: 3, col: 2, height: 2, width: 2},
      {type: UserEventsStatsComponent, row: 3, col: 4, height: 2, width: 1},
      {type: OriginMapComponent, row: 5, col: 2, height: 2, width: 2},
      {type: UserDisplayComponentComponent, row: 1, col: 5, height: 1, width: 2},
      {type: UserPostListComponent, row: 2, col: 5, height: 3, width: 2},
      {type: UserEventListComponent, row: 5, col: 5, height: 2, width: 2},
      {type: GaugeComponent, row: 5, col: 1, height: 1, width: 1},
      {type: RelevanceComponent, row: 6, col: 1, height: 1, width: 1}
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
      {type: EventListComponent,  row: 1, col: 3, height: 4, width: 2},
      {type: RatgeberListComponent,  row: 1, col: 5, height: 4, width: 2}
    ];
  }

  ngOnInit(): void {
    this.pageSelected.subscribe(page => {
      SysVars.CURRENT_PAGE = page;
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
