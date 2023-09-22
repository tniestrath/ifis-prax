import {Component} from '@angular/core';
import {Subject} from "rxjs";
import {Chart} from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";
import ChartAnnotation from "chartjs-plugin-annotation";
import Util from "./util/Util";



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
    Chart.register(ChartAnnotation);
    Chart.defaults.set('plugins.datalabels', {
      color: '#fff',
      formatter: (value: number, context: { dataIndex: string; }) => {
        return value == 0 ? "" : Util.formatNumbers(value);
      }
    });
    // @ts-ignore
    Chart.defaults.animation.duration = 2000;

  }

  select(selection : string) {
    this.selected.next(selection);
  }
}


