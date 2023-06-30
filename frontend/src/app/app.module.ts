import {ModuleWithProviders, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PolarChartComponent } from './component/polar-chart/polar-chart.component';
import { HeaderComponent } from './page/header/header.component';
import { ChartComponent } from './component/chart/chart.component';
import { CounterComponent } from './component/counter/counter.component';
import { GaugeComponent } from './component/gauge/gauge.component';
import { PageKennzahlenComponent } from './page/page-kennzahlen/page-kennzahlen.component';
import { PageTagComponent } from './page/tag/page-tag/page-tag.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { TagComponent } from './page/tag/tag/tag.component';
import { SearchbarComponent } from './page/searchbar/searchbar.component';
import { PageEinzelComponent } from './page/page-einzel/page-einzel.component';
import {CookieService} from "ngx-cookie-service";
import { SelectorComponent } from './page/selector/selector.component';
import { SelectableDirective } from './page/selector/selectable.directive';
import { UserComponent } from './page/page-einzel/user/user.component';
import {NgOptimizedImage} from "@angular/common";
import { DashBaseComponent } from './component/dash-base/dash-base.component';
import { ClicksComponent } from './component/clicks/clicks.component';
import { TagListComponent } from './component/tag-list/tag-list.component';
import { TagListItemComponent } from './component/tag-list/tag-list-item/tag-list-item.component';
import { PostChartComponent } from './component/post-chart/post-chart.component';
import { GridComponent } from './grid/grid.component';
import { GridCardDirective } from './grid/grid-card.directive';
import { RelevanceComponent } from './component/gauge/relevance/relevance.component';
import { PostComponent } from './component/post/post.component';
import { PotentialComponent } from './component/potential/potential.component';
import { UserPlanComponent } from './component/user-plan/user-plan.component';

@NgModule({
  declarations: [
    AppComponent,
    PolarChartComponent,
    HeaderComponent,
    ChartComponent,
    CounterComponent,
    GaugeComponent,
    PageKennzahlenComponent,
    PageTagComponent,
    TagComponent,
    SearchbarComponent,
    PageEinzelComponent,
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
    UserPlanComponent
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
