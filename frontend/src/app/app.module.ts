import {ModuleWithProviders, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CompanyDetailsComponent } from './company/company-details/company-details.component';
import { PolarChartComponent } from './component/polar-chart/polar-chart.component';
import { CompanyListerComponent } from './company/company-lister/company-lister.component';
import { HeaderComponent } from './header/header.component';
import { ChartComponent } from './component/chart/chart.component';
import { CounterComponent } from './component/counter/counter.component';
import { PodiumComponent } from './component/podium/podium.component';
import { PageKennzahlenComponent } from './page/page-kennzahlen/page-kennzahlen.component';
import { PageTagComponent } from './page/page-tag/page-tag.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import { TagListerComponent } from './tag/tag-lister/tag-lister.component';
import { TagDetailsComponent } from './tag/tag-details/tag-details.component';
import { SearchbarComponent } from './component/searchbar/searchbar.component';
import { PageEinzelComponent } from './page/page-einzel/page-einzel.component';
import {CookieService} from "ngx-cookie-service";
import {DbService} from "./services/db.service";
import { SelectorComponent } from './user/selector/selector.component';
import { SelectableDirective } from './user/selector/selectable.directive';
import { UserComponent } from './user/user/user.component';
import {NgOptimizedImage} from "@angular/common";

@NgModule({
  declarations: [
    AppComponent,
    CompanyDetailsComponent,
    PolarChartComponent,
    CompanyListerComponent,
    HeaderComponent,
    ChartComponent,
    CounterComponent,
    PodiumComponent,
    PageKennzahlenComponent,
    PageTagComponent,
    TagListerComponent,
    TagDetailsComponent,
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
