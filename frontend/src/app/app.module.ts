import {ModuleWithProviders, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PolarChartComponent } from './component/polar-chart/polar-chart.component';
import { HeaderComponent } from './page/header/header.component';
import { ChartComponent } from './component/chart/chart.component';
import { CounterComponent } from './component/counter/counter.component';
import { PodiumComponent } from './component/podium/podium.component';
import { PageKennzahlenComponent } from './page/page-kennzahlen/page-kennzahlen.component';
import { PageTagComponent } from './tag/page-tag/page-tag.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import { TagListerComponent } from './tag/tag-lister/tag-lister.component';
import { TagComponent } from './tag/tag/tag.component';
import { SearchbarComponent } from './component/searchbar/searchbar.component';
import { PageEinzelComponent } from './page/page-einzel/page-einzel.component';
import {CookieService} from "ngx-cookie-service";
import {DbService} from "./services/db.service";
import { SelectorComponent } from './page/selector/selector.component';
import { SelectableDirective } from './page/selector/selectable.directive';
import { UserComponent } from './page/page-einzel/user/user.component';
import {NgOptimizedImage} from "@angular/common";

@NgModule({
  declarations: [
    AppComponent,
    PolarChartComponent,
    HeaderComponent,
    ChartComponent,
    CounterComponent,
    PodiumComponent,
    PageKennzahlenComponent,
    PageTagComponent,
    TagListerComponent,
    TagComponent,
    SearchbarComponent,
    PageEinzelComponent,
    SelectorComponent,
    SelectableDirective,
    UserComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        MatAutocompleteModule,
        MatInputModule,
        ReactiveFormsModule,
        NgOptimizedImage
    ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule {}
