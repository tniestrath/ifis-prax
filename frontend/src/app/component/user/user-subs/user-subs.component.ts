import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-user-subs',
  templateUrl: './user-subs.component.html',
  styleUrls: ['./user-subs.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserSubsComponent extends DashBaseComponent implements OnInit{


  ngOnInit(): void {

  }

  createChart(data : number[]){
    if (this.chart){
      this.chart.destroy();
    }

    let max = Math.max(...data);
    let cpu_avg = 0;
    data.forEach(value => {cpu_avg += value});
    cpu_avg = cpu_avg / data.length;

    // @ts-ignore
    this.chart = new Chart("subs-chart", {
      type: "polarArea",
      data: {
        labels: data,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: DashColors.BLUE,
          borderColor: DashColors.BLUE,
          borderJoinStyle: 'round',
          borderWidth: 1
        }]
      },
      options: {
        clip: false,
        aspectRatio: .5,
        maintainAspectRatio: false,
        scales: {
          y: {
            min: 0,
            max: 1,
            ticks: {
              callback: tickValue =>{
                return String(Number(tickValue) * 100) + "%";
              },
              stepSize: .25,
              font: {
                size: 10
              }
            }
          },
          x: {
            display: false,
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          annotation: {
            annotations: [
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.RED,
                value: max,
                borderWidth: 2,
                label: {
                  content: String((max * 100).toFixed()) + "%",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 12
                  }
                }
              },
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.BLACK,
                value: cpu_avg,
                borderWidth: 2,
                label: {
                  content: String((cpu_avg * 100).toFixed()) + "%",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 12
                  }
                }
              }
            ]
          },
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
            display: false,
            position: "bottom"
          },
          tooltip: {
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            },
            callbacks: {
            }
          }
        },
        interaction: {
          intersect: false
        },
        events: []
      }
    })
  }
}
