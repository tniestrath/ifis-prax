import {Component, EventEmitter, OnInit} from '@angular/core';
import {ActiveElement, Chart, ChartEvent, ChartType} from "chart.js/auto";
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Subscription} from "rxjs";

@Component({
  selector: 'dash-post-chart',
  templateUrl: './post-chart.component.html',
  styleUrls: ['./post-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class PostChartComponent extends DashBaseComponent implements OnInit{

  visibility: string = "hidden";

  chart : any;
  canvas_id: string = "chart";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];


  createChart(type: ChartType, labels : string[], data : number[], onClick : EventEmitter<number>){
    Chart.defaults.color = "#000"
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
            text: "",
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
        },
        interaction: {
          mode: "nearest"
        },
        onClick(event: ChartEvent, elements: ActiveElement[], chart: Chart) {
          onClick.emit(elements[0].index);
        }
      }
    })
  }

  ngOnInit(): void {
    this.visibility = "visible";
  }
}
