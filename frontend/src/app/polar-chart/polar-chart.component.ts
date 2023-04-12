import {Component, Input, OnInit} from '@angular/core';
import { Chart } from 'chart.js/auto';
import _default from "chart.js/dist/plugins/plugin.tooltip";

@Component({
  selector: 'dash-polar-chart',
  templateUrl: './polar-chart.component.html',
  styleUrls: ['./polar-chart.component.css']
})
export class PolarChartComponent implements OnInit{
  chart : any;

  @Input() labels : string[] = [];
  @Input() data : string[] = [];

  createChart(){
    this.chart = new Chart("chart", {
      type: 'polarArea', data: {
        labels: ["#Hardware", "#Software", "#Home", "#VoIP"],
        datasets: [{
          label: "",
          data: [3, 1, 2, 6],
          backgroundColor: [
            "rgb(255, 0, 0)",
            "rgb(255, 255, 0)",
            "rgb(0, 255, 255)",
            "rgb(0, 0, 255)"
          ]
        }]
      }
    })
  }

  ngOnInit(): void {
    this.createChart();
  }
}


