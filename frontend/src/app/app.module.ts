import {NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PolarChartComponent } from './component/polar-chart/polar-chart.component';
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
import { UserComponent } from './page/page-einzel/user/user.component';
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
import { PostListComponent } from './component/post/post-list/post-list.component';
import { PostListItemComponent } from './component/post/post-list/post-list-item/post-list-item.component';
import { TagChartComponent } from './component/tag/tag-chart/tag-chart.component';

@NgModule({
  declarations: [
    AppComponent,
    PolarChartComponent,
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
    PostListItemComponent,
    TagChartComponent
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
