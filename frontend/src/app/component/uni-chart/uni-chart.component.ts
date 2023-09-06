import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "../post/Post";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";

class UniStat {
}

@Component({
  selector: 'dash-uni-chart',
  templateUrl: './uni-chart.component.html',
  styleUrls: ['./uni-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class UniChartComponent extends DashBaseComponent implements OnInit {
  visibility: string = "hidden";

  canvas_id: string = "uni-chart";

  timeSpan : string = "all_time";
  uniStat : string = "clicks";

  data : Promise<UniStat[]> | undefined;

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7],
    ["day", 0]
  ]);


  getData(event?: Event) {
    if (event !== undefined) {
      if ((event?.target as HTMLInputElement).type == "select-one") this.uniStat = (event?.target as HTMLInputElement).value;
      if ((event?.target as HTMLInputElement).type == "radio") this.timeSpan = (event?.target as HTMLInputElement).value;
    }
  }

  ngOnInit(): void {
    this.getData();
  }

  createChart(labels: string[], fullLabels : string[], data: number[], onClick: (index : number) => void){
    if (this.chart){
      this.chart.destroy();
    }
    // @ts-ignore
    this.chart = new Chart(this.canvas_id, {
      type: "line",
      data: {
        labels: labels,
        datasets: [{
          label: "Performance",
          data: data,
          backgroundColor: "rgb(148,28,62)",
          borderColor: "rgb(148,28,62)",
          borderJoinStyle: 'round',
          borderWidth: 5
        }]
      },
      options: {
        aspectRatio: 2.8,
        layout: {
          padding: {
            bottom: -50
          }
        },
        scales: {
          y: {
            min: 0,
            max: 100,
          },
          x: {
            display: true
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
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
            display: true,
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
              //@ts-ignore
              title(tooltipItems): string {
                // @ts-ignore
                return fullLabels[tooltipItems.at(0).dataIndex];
              }
            }
          }
        },
        interaction: {
          mode: "nearest",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
          onClick(elements[0].index);
        },
      }
    })
  }
}
