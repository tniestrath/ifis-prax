import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {Chart, ChartConfiguration, ChartType, ChartTypeRegistry} from 'chart.js/auto';

@Component({
  selector: 'dash-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css']
})
export class ChartComponent implements OnInit, AfterViewInit{

  chart : any;
  canvas_id: string = "chart";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];

  @Input() chartType : ChartType = 'bar';
  @Input() labels : string[] = [];
  @Input() data : number[] = [];
  @Input() desc : string = "";

  createChart(type: ChartType, labels : string[], data : number[]){
    Chart.defaults.color = "#000"
    if (this.chartType == "bar" || this.chartType == "line" || this.chartType == "bubble"){
      this.chart = new Chart(this.canvas_id, {
        type: type,
        data: {
          labels: labels,
          datasets: [{
            label: "",
            data: data,
            backgroundColor: this.colors
          }]
        },
        options: {
          plugins: {
            title: {
              display: true,
              text: this.desc,
              position: "bottom",
              fullSize: true,
              font: {
                size: 18,
                weight: "bold",
                family: 'Times New Roman'
              }
            },
            legend: {
              display: false
            }
          }
        }
      })
    }
    else {
      this.chart = new Chart(this.canvas_id, {
        type: type,
        data: {
          labels: labels,
          datasets: [{
            label: "",
            data: data,
            backgroundColor: this.colors
          }]
        },
        options: {
          plugins: {
            title: {
              display: true,
              text: this.desc,
              position: "bottom",
              fullSize: true,
              font: {
                size: 18,
                weight: "bold",
                family: 'Times New Roman'
              }
            }
          }
        }

      })
    }
  }


  ngOnInit(): void {
    if (this.desc != ""){
      this.canvas_id = this.desc;
    }
  }
  ngAfterViewInit(): void {
    this.createChart(this.chartType,["#Hardware", "#Software", "#Home", "#VoIP"], this.data);
  }



}
