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

  labels = ["0/12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"];
  ngOnInit(): void {

    if (SysVars.CURRENT_PAGE == "Users") {
      this.db.getClicksByTime(Number(SysVars.USER_ID)).then(res => {
        this.chart = this.createChart2("time_clicks", res, undefined);
      });
    } else if (SysVars.CURRENT_PAGE == "Overview") {
      this.db.getClicksByTimeAll().then(res => {
        this.chart = this.createChart2("time_clicks", res, undefined);
      });
    }
  }
  createChart2(canvas_id : string, data: number[], onClick : EventEmitter<number> | undefined){
    // @ts-ignore
    return new Chart(canvas_id, {
      type: "bar",
      data: {
        labels : this.labels,
        datasets: [{
          label: "Vormittags",
          data: data.slice(0, 11),
          backgroundColor: "rgb(90, 121, 149)",
          borderRadius: 5,
          borderWidth: 0,
        },{
          label: "Nachmittags",
          data: data.slice(12),
          backgroundColor: "rgb(122, 24, 51)",
          borderRadius: 5,
          borderWidth: 0,
         }]
      },
      options: {
        aspectRatio: 1,
        plugins: {
          datalabels: {
            color: "#ffffff",
            formatter: function(value, context) {
              var valueString = String(value);
              if (value > 1000){
                valueString = +parseFloat(String(value / 1000)).toFixed( 1 ) + "K";
              }
              else if (value > 9999){
                valueString = (value/1000).toFixed() + "K";
              }
              else if (value > 1000000){
                valueString = (value/1000000).toFixed(1) + "M";
              }
              else if (value > 9999999){
                valueString = (value/1000000).toFixed() + "M";
              }
              return valueString;
            },
            rotation: -90
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

  createChart(canvas_id : string, colors : string[], labels: string[][], onClick : EventEmitter<number> | undefined){
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
