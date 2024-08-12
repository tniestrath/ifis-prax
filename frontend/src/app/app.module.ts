import {NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import {AppComponent} from './app.component';
import { ProfileCompletionComponent } from './component/user/profile-completion/profile-completion.component';
import { HeaderComponent } from './page/header/header.component';
import { CounterComponent } from './component/counter/counter.component';
import { GaugeComponent } from './component/gauge/gauge.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { TagComponent } from './component/tag/tag/tag.component';
import { SearchbarComponent } from './page/searchbar/searchbar.component';
import { PageComponent } from './page/page-einzel/page.component';
import {CookieService} from "ngx-cookie-service";
import { SelectorComponent } from './page/selector/selector.component';
import { SelectableDirective } from './page/selector/selectable.directive';
import {UserComponent} from './component/user/user.component';
import {CommonModule, NgFor, NgForOf, NgOptimizedImage} from "@angular/common";
import { DashBaseComponent } from './component/dash-base/dash-base.component';
import { ClicksComponent } from './component/clicks/clicks.component';
import { TagListComponent } from './component/tag/tag-list/tag-list.component';
import { TagListItemComponent } from './component/tag/tag-list/tag-list-item/tag-list-item.component';
import { PostChartComponent } from './component/post/post-chart/post-chart.component';
import { GridComponent } from './grid/grid.component';
import { GridCardDirective } from './grid/grid-card.directive';
import { RelevanceComponent } from './component/gauge/relevance/relevance.component';
import { PostComponent } from './component/post/post.component';
import { PotentialComponent } from './component/potential/potential.component';
import {
  BasicListDirective,
  BpListDirective,
  OaListDirective, PlusListDirective, PremiumListDirective,
  UserPlanComponent
} from './component/user/user-plan/user-plan.component';
import { LoginComponent } from './component/login/login.component';
import {
  OriginMapComponent,
  OriginMapNewsletterComponent,
  OriginMapNewsletterGlobalComponent
} from './component/origin-map/origin-map.component';
import {
  ClicksByTimeComponent,
  ClicksByTimeNewsletterComponent
} from './component/clicks-by-time/clicks-by-time.component';
import { TagPieComponent } from './component/tag/tag-pie/tag-pie.component';
import { ImgFallbackDirective } from './img-fallback.directive';
import {
  PostListPageableComponent,
  PodcastListComponent,
  RatgeberListComponent,
  UserPostListComponent,
  EventListComponent, UserEventListComponent
} from './component/post/post-list/post-list-pageable.component';
import {
  PostListItemComponent
} from './component/post/post-list/post-list-item/post-list-item.component';
import { TagChartComponent } from './component/tag/tag-chart/tag-chart.component';
import { CallUpChartComponent } from './component/call-up-chart/call-up-chart.component';
import { OriginByTimeChartComponent } from './component/origin-by-time-chart/origin-by-time-chart.component';
import { Top5PostsComponent, Top5ArticleComponent, Top5BlogComponent, Top5NewsComponent, Top5WhitepaperComponent } from './component/post/top5-posts/top5-posts.component';
import { NewsletterStatsComponent } from './component/newsletter/newsletter-stats/newsletter-stats.component';
import {EventsStatsComponent, UserEventsStatsComponent} from './component/events-stats/events-stats.component';
import { SystemloadComponent } from './component/system/systemload/systemload.component';
import { PostTypeComponent } from './component/post/post-type/post-type.component';
import { UserListComponent } from './component/user/user-list/user-list.component';
import { UserComparatorComponent } from './component/user/user-comparator/user-comparator.component';
import {
  UserStatsByPlanComponent,
  UserStatsByPlanViewTypeCompareComponent
} from './component/user/user-stats-by-plan/user-stats-by-plan.component';
import { UserDisplayComponent } from './component/user/user-display-component/user-display.component';
import { UserClicksChartComponent } from './component/user/user-clicks-chart/user-clicks-chart.component';
import {
  SingleUserTagsDistComponent,
  UserTagsDistComponent
} from './component/user/user-tags-dist/user-tags-dist.component';
import { SeoOverTimeComponent } from './component/seo/seo-over-time/seo-over-time.component';
import { SeoStatDisplayComponent } from './component/seo/seo-stat-display/seo-stat-display.component';
import { SeoCtrComponent } from './component/seo/seo-ctr/seo-ctr.component';
import { SeoKeywordListComponent } from './component/seo/seo-keyword-list/seo-keyword-list.component';
import { SeoKeywordListItemComponent } from './component/seo/seo-keyword-list/seo-keyword-list-item/seo-keyword-list-item.component';
import { UserTagDistItemComponent } from './component/user/user-tags-dist/user-tag-dist-item/user-tag-dist-item.component';
import {
  SearchListAnbieterNoResultsComponent,
  SearchListComponent,
  SearchListCombinedComponent
} from './component/search/search-no-results-list/search-list.component';
import {
  SearchListAnbieterItemComponent,
  SearchListItemComponent,
  SearchListNoResultsItemComponent,
  SearchListSSItemComponent
} from './component/search/search-no-results-list/search-list-item/search-list-item.component';
import { NewsletterListComponent } from './component/newsletter/newsletter-list/newsletter-list.component';
import { NewsletterListItemComponent } from './component/newsletter/newsletter-list/newsletter-list-item/newsletter-list-item.component';
import { NewsletterComponent } from './component/newsletter/newsletter/newsletter.component';
import { PostDisplayComponent } from './component/post/post-display/post-display.component';
import { BlackHoleListComponent } from './component/system/black-hole-list/black-hole-list.component';
import { BadBotItemComponent } from './component/system/black-hole-list/bad-bot-item/bad-bot-item.component';
import { DashListComponent } from './component/dash-list/dash-list.component';
import { ForumModerationListComponent } from './component/forum/forum-moderation-list/forum-moderation-list.component';
import {
    ForumModerationDisplayComponent,
    SafeHtmlPipe
} from './component/forum/forum-moderation-display/forum-moderation-display.component';
import { ForumModerationListItemComponent } from './component/forum/forum-moderation-list/forum-moderation-list-item/forum-moderation-list-item.component';
import { ForumModeratorComponent } from './component/forum/forum-moderator/forum-moderator.component';
import { ForumProfanityFilterAdderComponent } from './component/forum/forum-profanity-filter-adder/forum-profanity-filter-adder.component';
import { ExternalServicesListComponent } from './component/system/external-services-list/external-services-list.component';
import {
    ExternalServicesListItemComponent,
    SafeUrlPipe
} from './component/system/external-services-list/external-services-list-item/external-services-list-item.component';
import { UserPlanLogComponent } from './component/user/user-plan-log/user-plan-log.component';
import { UserPlanLogItemComponent } from './component/user/user-plan-log/user-plan-log-item/user-plan-log-item.component';
import {UserPlanChip} from "./component/user/user";
import {AreYouSureDialog, DialogDirective} from "./util/Dialog";
import {ForumStatsComponent} from "./component/forum/forum-stats/forum-stats.component";
import { PostTypesAverageViewsComponent } from './component/post/post-types-avarage-views/post-types-average-views.component';
import { SocialsSumsComponent } from './component/socials/socials-sums/socials-sums.component';
import { BounceComponent } from './component/bounce/bounce.component';

@NgModule({
  declarations: [
    AppComponent,
    ProfileCompletionComponent,
    HeaderComponent,
    CounterComponent,
    GaugeComponent,
    TagComponent,
    SearchbarComponent,
    PageComponent,
    SelectorComponent,
    SelectableDirective,
    UserComponent,
    DashBaseComponent,
    ClicksComponent,
    TagListComponent,
    TagListItemComponent,
    PostChartComponent,
    GridComponent,
    GridCardDirective,
    RelevanceComponent,
    PostComponent,
    PotentialComponent,
    UserPlanComponent,
    LoginComponent,
    OriginMapComponent,
    OriginMapNewsletterComponent,
    OriginMapNewsletterGlobalComponent,
    ClicksByTimeComponent,
    ClicksByTimeNewsletterComponent,
    TagPieComponent,
    ImgFallbackDirective,
    PostListPageableComponent,
    PodcastListComponent,
    RatgeberListComponent,
    UserPostListComponent,
    EventListComponent,
    UserEventListComponent,
    PostListItemComponent,
    TagChartComponent,
    CallUpChartComponent,
    OriginByTimeChartComponent,
    Top5PostsComponent,
    Top5ArticleComponent,
    Top5BlogComponent,
    Top5NewsComponent,
    Top5WhitepaperComponent,
    NewsletterStatsComponent,
    EventsStatsComponent,
    UserEventsStatsComponent,
    SystemloadComponent,
    PostTypeComponent,
    UserListComponent,
    UserComparatorComponent,
    UserStatsByPlanComponent,
    UserStatsByPlanViewTypeCompareComponent,
    UserDisplayComponent,
    UserClicksChartComponent,
    UserTagsDistComponent,
    SingleUserTagsDistComponent,
    SeoOverTimeComponent,
    SeoStatDisplayComponent,
    SeoCtrComponent,
    SeoKeywordListComponent,
    SeoKeywordListItemComponent,
    UserTagDistItemComponent,
    SearchListComponent,
    SearchListCombinedComponent,
    SearchListAnbieterNoResultsComponent,
    SearchListItemComponent,
    SearchListAnbieterItemComponent,
    SearchListNoResultsItemComponent,
    SearchListSSItemComponent,
    NewsletterListComponent,
    NewsletterListItemComponent,
    NewsletterComponent,
    PostDisplayComponent,
    BlackHoleListComponent,
    BadBotItemComponent,
    DashListComponent,
    ForumModerationListComponent,
    ForumModerationDisplayComponent,
    ForumModerationListItemComponent,
    ForumModeratorComponent,
    ForumStatsComponent,
    SafeHtmlPipe,
    ForumProfanityFilterAdderComponent,
    ExternalServicesListComponent,
    ExternalServicesListItemComponent,
    UserPlanLogComponent,
    UserPlanLogItemComponent,
    SafeUrlPipe,
    UserPlanChip,
    OaListDirective,
    BasicListDirective,
    BpListDirective,
    PlusListDirective,
    PremiumListDirective,
    AreYouSureDialog,
    DialogDirective,
    PostTypesAverageViewsComponent,
    SocialsSumsComponent,
    BounceComponent
  ],
    imports: [
        BrowserModule,
        CommonModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatAutocompleteModule,
        MatInputModule,
        ReactiveFormsModule,
        NgOptimizedImage,
        FormsModule
    ],
  providers: [CookieService],
  bootstrap: [AppComponent],
  schemas: [NO_ERRORS_SCHEMA]
})
export class AppModule {}
