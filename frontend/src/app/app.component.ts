import {Component, EventEmitter} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {Observable, Subject} from "rxjs";
import {Chart} from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";



@Component({
  selector: 'dash-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Dashboard';
  selected = new Subject<string>();
  tag : string = "";

  selectedSearch : string = "";

  constructor() {
    Chart.register(ChartDataLabels);
  }

  select(selection : string) {
    this.selected.next(selection);
  }
}


