import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";

@Component({
  selector: 'dash-clicks-by-time',
  templateUrl: './clicks-by-time.component.html',
  styleUrls: ['./clicks-by-time.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksByTimeComponent extends DashBaseComponent implements OnInit{

  colors : string[] = ["#5A7995", "rgb(148,28,62)", "rgb(84, 16, 35, 33)"];
  chart: any;
  ngOnInit(): void {
    this.chart = this.createChart("time_clicks", ["Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"], [1,2,3,4,5,6,7], undefined);
  }

  createChart(canvas_id : string, labels : string[], realData : number[], onClick : EventEmitter<number> | undefined){
    Chart.defaults.color = "#000"
    // @ts-ignore
    return new Chart(canvas_id, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [{
          label: "",
          data: realData,
          backgroundColor: this.colors,
          borderRadius: 5,
          borderWidth: 5
        }]
      },
      options: {
        aspectRatio: 1,
        plugins: {
          title: {
            display: false,
            text: "",
            position: "top",
            fullSize: true,
            font: {
              size: 50,
              weight: "bold",
              family: 'Times New Roman'
            }
          },
          legend: {
            onClick: (e) => null,
            display: true
          },
          tooltip: {
            displayColors: false,
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            }
          },
        }
      }
    })
  }

}
