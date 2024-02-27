import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {DbService} from "../../services/db.service";
import {Observable, Subject} from "rxjs";
import {ClicksComponent} from "../../component/clicks/clicks.component";
import {PostChartComponent} from "../../component/post/post-chart/post-chart.component";
import {GaugeComponent} from "../../component/gauge/gauge.component";
import {GridCard} from "../../grid/GridCard";
import {RelevanceComponent} from "../../component/gauge/relevance/relevance.component";
import {SysVars} from "../../services/sys-vars-service";
import {UserPlanComponent} from "../../component/user/user-plan/user-plan.component";
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
import {ProfileCompletionComponent} from "../../component/user/profile-completion/profile-completion.component";
import {UserComparatorComponent} from "../../component/user/user-comparator/user-comparator.component";
import {
  UserStatsByPlanComponent,
  UserStatsByPlanViewTypeCompareComponent
} from "../../component/user/user-stats-by-plan/user-stats-by-plan.component";
import {UserDisplayComponentComponent} from "../../component/user/user-display-component/user-display-component.component";
import {UserClicksChartComponent} from "../../component/user/user-clicks-chart/user-clicks-chart.component";
import {SingleUserTagsDistComponent, UserTagsDistComponent} from "../../component/user/user-tags-dist/user-tags-dist.component";
import {PdfService} from "../../services/pdf.service";
import {DashBaseComponent} from "../../component/dash-base/dash-base.component";
import {SeoOverTimeComponent} from "../../component/seo/seo-over-time/seo-over-time.component";
import {SeoStatDisplayComponent} from "../../component/seo/seo-stat-display/seo-stat-display.component";
import {SeoCtrComponent} from "../../component/seo/seo-ctr/seo-ctr.component";
import {SeoKeywordListComponent} from "../../component/seo/seo-keyword-list/seo-keyword-list.component";
import {
  SearchListComponent, SearchListNoResultsComponent, SearchListRankComponent, SearchListSSComponent
} from "../../component/search/search-no-results-list/search-list.component";
@Component({
  selector: 'dash-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {

  resetSearchbar : Subject<boolean> = new Subject<boolean>();

  @Input() pageSelected = new Observable<string>;
  cardsLoaded = new Subject<GridCard[]>();

  constructor(private cookieService : CookieService, private db : DbService, private pdf : PdfService, private element : ElementRef) {
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
      {type: SingleUserTagsDistComponent, row: 2, col: 5, height: 1, width: 2},
      {type: UserPostListComponent, row: 3, col: 5, height: 2, width: 2},
      {type: UserEventListComponent, row: 5, col: 5, height: 2, width: 2},
      {type: GaugeComponent, row: 5, col: 1, height: 1, width: 1},
      {type: RelevanceComponent, row: 6, col: 1, height: 1, width: 1}
    ];
  }
  getUserDetailPageCardsPRINT() {
    return [
      {type: ClicksComponent, row: 1, col: 1, height: 4, width: 2},
      {type: ProfileCompletionComponent, row: 5, col: 1, height: 2, width: 2},
      {type: UserDisplayComponentComponent, row: 1, col: 3, height: 1, width: 4},
      {type: SingleUserTagsDistComponent, row: 2, col: 3, height: 2, width: 4},
      {type: UserClicksChartComponent, row: 4, col: 3, height: 2, width: 4},
      {type: EventListComponent, row: 6, col: 3, height: 1, width: 4}
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

  getSeoPageCards() {
    return [
      {type: SeoOverTimeComponent, row: 1, col: 1, height: 2, width: 5},
      {type: SeoStatDisplayComponent, row: 1, col: 6, height: 2, width: 1},
      {type: SeoCtrComponent, row: 3, col: 1, height: 2, width: 4},
      {type: SeoKeywordListComponent, row: 3, col: 5, height: 2, width: 2}
    ];
  }

  getSystemPageCards() {
    return [
      {type: SystemloadComponent, row: 1, col: 1, height: 2, width: 2},
    ];
  }

  getSearchPageCards() {
    return [
      {type: SearchListNoResultsComponent, row: 1, col: 1, height: 2, width: 2},
      {type: SearchListRankComponent, row: 1, col: 3, height: 2, width: 2},
      {type: SearchListSSComponent, row: 1, col: 5, height: 2, width: 2},
    ];
  }

  ngOnInit(): void {
    this.pageSelected.subscribe(page => {
      SysVars.CURRENT_PAGE = page;
      switch (page) {
        case "Anbieter":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getUserPageCards());
          this.resetSearchbar.next(true);
          this.db.resetStatus();
          break;
        }
        case "Themen":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getTagsPageCards());
          this.db.resetStatus();
          break;
        }
        case "Beiträge":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getPostsPageCards());
          this.db.resetStatus();
          break;
        }
        case "Übersicht":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getOverviewPageCards());
          this.db.resetStatus();
          break;
        }
        case "Inhalte":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getContentPageCards());
          this.db.resetStatus();
          break;
        }
        case "Login":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getLandingPageCards());
          break;
        }
        case "SEO":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getSeoPageCards());
          break;
        }
        case "System":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getSystemPageCards());
          break;
        }
        case "Suche":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getSearchPageCards());
          break;
        }
        case "PRINT":{
          this.pdf.bringInFormat(this.element.nativeElement);
          this.cardsLoaded.next(this.getUserDetailPageCardsPRINT());
          break;
        }
        default: {
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getLandingPageCards());
          break;
        }
      }
    });
    SysVars.SELECTED_USER_ID.subscribe(id => {
      SysVars.CURRENT_PAGE = "UserDetail";
      SysVars.USER_ID = String(id);
      this.cardsLoaded.next(this.getUserDetailPageCards());
      this.db.resetStatus();
    })

  }
}
