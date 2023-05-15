import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent, ChartType} from "chart.js/auto";

@Component({
  selector: 'dash-clicks',
  templateUrl: './clicks.component.html',
  styleUrls: ['./clicks.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksComponent extends DashBaseComponent implements OnInit{

  canvas_id: string = "chart";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];
  chart: any;


  createChart(labels : string[], data : number[], onClick : EventEmitter<number> | undefined){
    Chart.defaults.color = "#000"
    this.chart = new Chart(this.canvas_id, {
      type: "pie",
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
            display: false,
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
            onClick: (e) => null,
            display: true
          }
        }
      }
    })
  }

  ngOnInit(): void {
    if (this.chart){
      this.chart.destroy();
    }
    this.createChart(["Direkt", "Per Suche", "Per Register"], [12,34,56], undefined);
  }


}
