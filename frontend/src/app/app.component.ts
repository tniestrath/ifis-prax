import {Component, NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";



@Component({
  selector: 'dash-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
/*
@NgModule({
  imports: [
    HttpClientModule
  ]
})*/
export class AppComponent {
  title = 'Dashboard';
  companyListShown = false;
}


