import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {ApiService} from "../../services/api.service";
import {Observable, Subject} from "rxjs";
import {ClicksComponent} from "../../component/clicks/clicks.component";
import {GaugeComponent} from "../../component/gauge/gauge.component";
import {GridCard} from "../../grid/GridCard";
import {RelevanceComponent} from "../../component/gauge/relevance/relevance.component";
import {SysVars} from "../../services/sys-vars-service";
import {UserPlanComponent} from "../../component/user/user-plan/user-plan.component";
import {LoginComponent} from "../../component/login/login.component";
import {
  OriginMapComponent,
  OriginMapNewsletterGlobalComponent
} from "../../component/origin-map/origin-map.component";
import {TagListComponent} from "../../component/tag/tag-list/tag-list.component";
import {TagPieComponent} from "../../component/tag/tag-pie/tag-pie.component";
import {
  EventListComponent,
  PodcastListComponent,
  PostListPageableComponent,
  RatgeberListComponent, UserEventListComponent,
  UserPostListComponent
} from "../../component/post/post-list/post-list-pageable.component";
import {TagChartComponent} from "../../component/tag/tag-chart/tag-chart.component";
import {CallUpChartComponent} from "../../component/call-up-chart/call-up-chart.component";
import {Top5ArticleComponent, Top5BlogComponent, Top5NewsComponent, Top5WhitepaperComponent} from "../../component/post/top5-posts/top5-posts.component";
import {NewsletterStatsComponent} from "../../component/newsletter/newsletter-stats/newsletter-stats.component";
import {SystemloadComponent} from "../../component/system/systemload/systemload.component";
import {EventsStatsComponent, UserEventsStatsComponent} from "../../component/events-stats/events-stats.component";
import {PostTypeComponent} from "../../component/post/post-type/post-type.component";
import {ProfileCompletionComponent} from "../../component/user/profile-completion/profile-completion.component";
import {UserComparatorComponent} from "../../component/user/user-comparator/user-comparator.component";
import {
  UserStatsByPlanComponent, UserStatsByPlanPlusPremiumComponent, UserStatsByPlanRedirectsComponent,
  UserStatsByPlanShortViewComponent,
  UserStatsByPlanViewTypeCompareComponent
} from "../../component/user/user-stats-by-plan/user-stats-by-plan.component";
import {UserDisplayComponent} from "../../component/user/user-display-component/user-display.component";
import {UserClicksChartComponent} from "../../component/user/user-clicks-chart/user-clicks-chart.component";
import {SingleUserTagsDistComponent, UserTagsDistComponent} from "../../component/user/user-tags-dist/user-tags-dist.component";
import {PdfService} from "../../services/pdf.service";
import {SeoOverTimeComponent} from "../../component/seo/seo-over-time/seo-over-time.component";
import {SeoStatDisplayComponent} from "../../component/seo/seo-stat-display/seo-stat-display.component";
import {SeoCtrComponent} from "../../component/seo/seo-ctr/seo-ctr.component";
import {SeoKeywordListComponent} from "../../component/seo/seo-keyword-list/seo-keyword-list.component";
import {SearchListAnbieterNoResultsComponent, SearchListCombinedComponent} from "../../component/search/search-no-results-list/search-list.component";
import {NewsletterListComponent} from "../../component/newsletter/newsletter-list/newsletter-list.component";
import {NewsletterComponent} from "../../component/newsletter/newsletter/newsletter.component";
import {ClicksByTimeNewsletterComponent} from "../../component/clicks-by-time/clicks-by-time.component";
import {PostDisplayComponent} from "../../component/post/post-display/post-display.component";
import {BlackHoleListComponent} from "../../component/system/black-hole-list/black-hole-list.component";
import {DashBaseComponent} from "../../component/dash-base/dash-base.component";
import {
  ForumModerationListComponent
} from "../../component/forum/forum-moderation-list/forum-moderation-list.component";
import {
  ForumModerationDisplayComponent
} from "../../component/forum/forum-moderation-display/forum-moderation-display.component";
import {ForumModeratorComponent} from "../../component/forum/forum-moderator/forum-moderator.component";
import {
  ForumProfanityFilterAdderComponent
} from "../../component/forum/forum-profanity-filter-adder/forum-profanity-filter-adder.component";
import {
  ExternalServicesListComponent
} from "../../component/system/external-services-list/external-services-list.component";
import {UserPlanLogComponent} from "../../component/user/user-plan-log/user-plan-log.component";
import {ForumStats, ForumStatsComponent} from "../../component/forum/forum-stats/forum-stats.component";
import {
  PostTypesAverageViewsComponent
} from "../../component/post/post-types-avarage-views/post-types-average-views.component";
import {SocialsSumsComponent} from "../../component/socials/socials-sums/socials-sums.component";
import {BounceComponent} from "../../component/bounce/bounce.component";
import {PostComparatorComponent} from "../../component/post/post-comparator/post-comparator.component";
import {UserSubsComponent} from "../../component/user/user-subs/user-subs.component";
import {
  VisitorSubscriptionChartComponent
} from "../../component/visitor/visitor-subscribtion-chart/visitor-subscription-chart.component";
import {ForumAddModeratorComponent} from "../../component/forum/forum-add-moderator/forum-add-moderator.component";
@Component({
  selector: 'dash-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {

  resetSearchbar : Subject<boolean> = new Subject<boolean>();

  cardsLoaded = new Subject<GridCard[]>();

  constructor(private cookieService : CookieService, private db : ApiService, private pdf : PdfService, private element : ElementRef) {
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
      {type: UserPlanLogComponent, row: 3, col: 4, height: 2, width: 3}
    ];
  }

  getUserDetailPageCards() {
    return [
      {type: ClicksComponent, row: 1, col: 1, height: 4, width: 1},
      {type: UserClicksChartComponent, row: 1, col: 2, height: 2, width: 3},
      {type: ProfileCompletionComponent, row: 3, col: 2, height: 2, width: 2},
      {type: UserEventsStatsComponent, row: 3, col: 4, height: 2, width: 1},
      {type: OriginMapComponent, row: 5, col: 2, height: 2, width: 3},
      {type: UserDisplayComponent, row: 1, col: 5, height: 1, width: 2},
      {type: SingleUserTagsDistComponent, row: 2, col: 5, height: 1, width: 2},
      {type: UserPostListComponent, row: 3, col: 5, height: 2, width: 2},
      {type: UserEventListComponent, row: 5, col: 5, height: 2, width: 2},
      {type: GaugeComponent, row: 5, col: 1, height: 1, width: 1},
      {type: RelevanceComponent, row: 6, col: 1, height: 1, width: 1}
    ];
  }
  getUserDetailPageCardsPRINT() {
    return [
      {type: ClicksComponent, row: 1, col: 1, height: 6, width: 2},
      {type: OriginMapComponent, row: 7, col: 1, height: 4, width: 2},
      {type: UserDisplayComponent, row: 1, col: 3, height: 2, width: 4},
      {type: SingleUserTagsDistComponent, row: 3, col: 3, height: 4, width: 4},
      {type: UserClicksChartComponent, row: 7, col: 3, height: 4, width: 4},
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
      {type: PostComparatorComponent, row: 1, col: 1, height: 4, width: 6},

      {type: Top5ArticleComponent, row: 5, col: 1, height: 1, width: 6},
      {type: Top5BlogComponent, row: 6, col: 1, height: 1, width: 6},
      {type: Top5NewsComponent, row: 7, col: 1, height: 1, width: 6},
      {type: Top5WhitepaperComponent, row: 8, col: 1, height: 1, width: 6}
    ];
  }

  getOverviewPageCards() {
    return [
      {type: UserPlanComponent, row: 1, col: 1, height: 2, width: 1},
      {type: CallUpChartComponent, row: 1, col: 2, height: 2, width: 4},
      {type: NewsletterStatsComponent, row: 1, col: 6, height: 1, width: 1},
      {type: SystemloadComponent, row: 3, col: 5, height: 2, width: 1},
      {type: PostTypeComponent, row: 3, col: 1, height: 2, width: 1},
      {type: OriginMapComponent, row: 3, col: 2, height: 2, width: 3},
      {type: EventsStatsComponent, row: 2, col: 6, height: 1, width: 1},
      {type: BounceComponent, row: 3, col: 6, height: 1, width: 1},
      {type: UserSubsComponent, row: 4, col: 6, height: 1, width: 1},
      {type: PostTypesAverageViewsComponent, row: 5, col: 1, height: 1, width: 2},
      {type: UserStatsByPlanShortViewComponent, row: 5, col: 3, height: 1, width: 2},
      {type: UserStatsByPlanRedirectsComponent, row: 5, col: 5, height: 1, width: 2},
      {type: SocialsSumsComponent, row: 6, col: 1, height: 1, width: 2},
      {type: UserStatsByPlanPlusPremiumComponent, row: 6, col: 3, height: 1, width: 2},
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
      {type: BlackHoleListComponent, row: 1, col: 3, height: 2, width: 4},
      {type: ExternalServicesListComponent, row: 3, col: 1, height: 2, width: 2},
    ];
  }

  getSearchPageCards() {
    return [
      {type: SearchListCombinedComponent, row: 1, col: 3, height: 4, width: 4},
      {type: SearchListAnbieterNoResultsComponent, row: 1, col: 1, height: 4, width: 2},
    ];
  }

  getNewsletterPageCards() {
    return [
      {type: NewsletterStatsComponent, row: 1, col: 1, height: 2, width: 1},
      {type: NewsletterComponent, row: 1, col: 2, height: 2, width: 3},
      {type: NewsletterListComponent, row: 1, col: 5, height: 4, width: 2},
      {type: OriginMapNewsletterGlobalComponent, row: 3, col: 1, height: 2, width: 2},
      {type: ClicksByTimeNewsletterComponent, row: 3, col: 3, height: 2, width: 2}
    ];
  }
  getForumPageCards(){
    return [
      {type: ForumStatsComponent, row: 1, col: 1, height: 4, width: 1},
      {type: ForumModeratorComponent, row: 1, col: 2, height: 2, width: 5},
      {type: ForumProfanityFilterAdderComponent, row: 3, col: 6, height: 2, width: 1},
      {type: ForumAddModeratorComponent, row: 3, col: 5, height: 1, width: 1}
    ]
  }
  getVisitorPageCards() {
    return [
      {type: VisitorSubscriptionChartComponent, row: 1, col: 1, height: 2, width: 2},
    ]
  }

  ngOnInit(): void {
    SysVars.SELECTED_PAGE.subscribe(page => {
      SysVars.CURRENT_PAGE = page;
      switch (page) {
        case "Anbieter":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getUserPageCards());
          this.resetSearchbar.next(true);
          break;
        }
        case "Themen":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getTagsPageCards());
          break;
        }
        case "Beiträge":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getPostsPageCards());
          break;
        }
        case "Übersicht":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getOverviewPageCards());
          break;
        }
        case "Inhalte":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getContentPageCards());
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
        case "Newsletter":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getNewsletterPageCards());
          break;
        }
        case "Forum":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getForumPageCards());
          break;
        }
        case "Besucher":{
          this.pdf.restoreStyle(this.element.nativeElement);
          this.cardsLoaded.next(this.getVisitorPageCards());
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
      /*var elements = document.querySelectorAll<HTMLDivElement>(".nav-element");
      elements.forEach((value, key) => {
        value.style.border = "none";
      })
      let elem = document.getElementById(page);
      console.log("awd: " +elem)
      if(elem != null){
        elem.style.border = "2px solid #941C3EFF";
      }*/
    });
    SysVars.SELECTED_USER_ID.subscribe(id => {
      SysVars.CURRENT_PAGE = "UserDetail";
      SysVars.USER_ID = String(id);
      this.cardsLoaded.next(this.getUserDetailPageCards());
    })
  }
}
