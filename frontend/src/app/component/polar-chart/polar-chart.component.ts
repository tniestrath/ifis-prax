import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'dash-polar-chart',
  templateUrl: './polar-chart.component.html',
  styleUrls: ['./polar-chart.component.css']
})
export class PolarChartComponent implements OnInit, AfterViewInit{
  chart : any;

  @Input() labels : string[] = [];
  @Input() data : number[] = [];
  @Input() desc : string = "";
  canvas_id: string = "chart";

  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148, 28, 62, 58)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];

  constructor() {
  }

  createChart(labels : string[], data : number[]){
    this.chart = new Chart(this.canvas_id, {
      type: 'polarArea', data: {
        labels: labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: this.colors
        }]
      }
    })
  }

  ngOnInit(): void {
    if (this.desc != ""){
      this.canvas_id = this.desc;
    }
  }

  ngAfterViewInit(): void {
    this.createChart(["#Hardware", "#Software", "#Home", "#VoIP"], this.data);
  }
}


