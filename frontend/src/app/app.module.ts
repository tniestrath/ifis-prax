import {NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProfileCompletionComponent } from './component/profile-completion/profile-completion.component';
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
import {UserComponent} from './page/page-einzel/user/user.component';
import {NgOptimizedImage} from "@angular/common";
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
import { UserPlanComponent } from './component/user-plan/user-plan.component';
import { LoginComponent } from './component/login/login.component';
import { OriginMapComponent } from './component/origin-map/origin-map.component';
import { ClicksByTimeComponent } from './component/clicks-by-time/clicks-by-time.component';
import { TagPieComponent } from './component/tag/tag-pie/tag-pie.component';
import { ImgFallbackDirective } from './img-fallback.directive';
import {
  PostListComponent,
  PodcastListComponent,
  RatgeberListComponent,
  UserPostListComponent,
  EventListComponent, UserEventListComponent
} from './component/post/post-list/post-list.component';
import {
  PostListItemComponent
} from './component/post/post-list/post-list-item/post-list-item.component';
import { TagChartComponent } from './component/tag/tag-chart/tag-chart.component';
import { CallUpChartComponent } from './component/call-up-chart/call-up-chart.component';
import { OriginByTimeChartComponent } from './component/origin-by-time-chart/origin-by-time-chart.component';
import { Top5PostsComponent, Top5ArticleComponent, Top5BlogComponent, Top5NewsComponent, Top5WhitepaperComponent } from './component/post/top5-posts/top5-posts.component';
import { NewsletterStatsComponent } from './component/newsletter-stats/newsletter-stats.component';
import {EventsStatsComponent, UserEventsStatsComponent} from './component/events-stats/events-stats.component';
import { SystemloadComponent } from './component/system/systemload/systemload.component';
import { PostTypeComponent } from './component/post/post-type/post-type.component';
import { UserListComponent } from './page/page-einzel/user/user-list/user-list.component';
import { UserComparatorComponent } from './page/page-einzel/user/user-comparator/user-comparator.component';
import {
  UserStatsByPlanComponent,
  UserStatsByPlanViewTypeCompareComponent
} from './page/page-einzel/user/user-stats-by-plan/user-stats-by-plan.component';
import { UserDisplayComponentComponent } from './page/page-einzel/user/user-display-component/user-display-component.component';
import { UserClicksChartComponent } from './page/page-einzel/user/user-clicks-chart/user-clicks-chart.component';
import {
  SingleUserTagsDistComponent,
  UserTagsDistComponent
} from './page/page-einzel/user/user-tags-dist/user-tags-dist.component';
import { SeoOverTimeComponent } from './component/seo/seo-over-time/seo-over-time.component';
import { SeoStatDisplayComponent } from './component/seo/seo-stat-display/seo-stat-display.component';
import { SeoCtrComponent } from './component/seo/seo-ctr/seo-ctr.component';
import { SeoKeywordListComponent } from './component/seo/seo-keyword-list/seo-keyword-list.component';
import { SeoKeywordListItemComponent } from './component/seo/seo-keyword-list/seo-keyword-list-item/seo-keyword-list-item.component';

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
    ClicksByTimeComponent,
    TagPieComponent,
    ImgFallbackDirective,
    PostListComponent,
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
    UserDisplayComponentComponent,
    UserClicksChartComponent,
    UserTagsDistComponent,
    SingleUserTagsDistComponent,
    SeoOverTimeComponent,
    SeoStatDisplayComponent,
    SeoCtrComponent,
    SeoKeywordListComponent,
    SeoKeywordListItemComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatAutocompleteModule,
        MatInputModule,
        ReactiveFormsModule,
        NgOptimizedImage,
        FormsModule
    ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule {}
