import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import ChartDataLabels from 'chartjs-plugin-datalabels';
import {User} from "../../page/page-einzel/user/user";
import {SysVars} from "../../services/sys-vars-service";

@Component({
  selector: 'dash-clicks-by-time',
  templateUrl: './clicks-by-time.component.html',
  styleUrls: ['./clicks-by-time.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksByTimeComponent extends DashBaseComponent implements OnInit{

  colors : string[] = [];
  chart: any;

  labels = [["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"],["12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"]];
  private maxClicks: number = 10;
  ngOnInit(): void {

    this.db.getClicksByTime(Number(1)).then(res => {
      var i = 0;
      for (var clicks of res) {
        this.colors.push( this.interpolateColor("rgb(90, 121, 149)", "rgb(122, 24, 51)", this.maxClicks, clicks / this.maxClicks));
        i++;
      }
    })

    this.chart = this.createChart("time_clicks", this.colors, this.labels, undefined);
  }

  createChart(canvas_id : string, colors : string[], labels: string[][], onClick : EventEmitter<number> | undefined){
    Chart.defaults.color = "#000"
    Chart.register(ChartDataLabels)
    // @ts-ignore
    return new Chart(canvas_id, {
      type: "doughnut",
      data: {
        datasets: [{
          rotation: -15,
          data: [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
          backgroundColor: this.colors.slice(0, 11),
          borderRadius: 5,
          borderWidth: 5,
        },
          {
            rotation: -15,
            data: [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
            backgroundColor: this.colors.slice(12),
            borderRadius: 5,
            borderWidth: 5
          }]
      },
      options: {
        aspectRatio: 1,
        cutout: "20%",
        plugins: {
          datalabels: {
            color: "#ffffff",
            formatter: function(value, context) {
              // @ts-ignore
              return  labels[context.datasetIndex][context.dataIndex];
            }
          },
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
            display: false
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

  interpolateColor(color1 : string, color2 : string, steps : number, step : number) {
    // @ts-ignore
    var color1Arr = color1.match(/\d+/g).map(Number);
    // @ts-ignore
    var color2Arr = color2.match(/\d+/g).map(Number);

    var r = Math.round(color1Arr[0] + (color2Arr[0] - color1Arr[0]) * (step / steps));
    var g = Math.round(color1Arr[1] + (color2Arr[1] - color1Arr[1]) * (step / steps));
    var b = Math.round(color1Arr[2] + (color2Arr[2] - color1Arr[2]) * (step / steps));

    return 'rgb(' + r + ',' + g + ',' + b + ')';
  }

}
