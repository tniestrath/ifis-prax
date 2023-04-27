import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CompanyDetailsComponent } from './company-details/company-details.component';
import { PolarChartComponent } from './polar-chart/polar-chart.component';
import { CompanyListerComponent } from './company-lister/company-lister.component';
import { HeaderComponent } from './header/header.component';
import { ChartComponent } from './chart/chart.component';
import { CounterComponent } from './counter/counter.component';
import { PodiumComponent } from './podium/podium.component';
import { PageKennzahlenComponent } from './page-kennzahlen/page-kennzahlen.component';
import { PageTagComponent } from './page-tag/page-tag.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";

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
    PageTagComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatAutocompleteModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
