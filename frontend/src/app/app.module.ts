import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CompanyDetailsComponent } from './company-details/company-details.component';
import { PolarChartComponent } from './polar-chart/polar-chart.component';
import { CompanyListerComponent } from './company-lister/company-lister.component';

@NgModule({
  declarations: [
    AppComponent,
    CompanyDetailsComponent,
    PolarChartComponent,
    CompanyListerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
