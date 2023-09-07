import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "../post/Post";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {SysVars} from "../../services/sys-vars-service";

export class UniStat {
  type : string = "default";
  data : number = 0;
  date : string = "00-00-0000";

  constructor(type: string, data: number, date: string) {
    this.type = type;
    this.data = data;
    this.date = date;
  }
}

@Component({
  selector: 'dash-uni-chart',
  templateUrl: './uni-chart.component.html',
  styleUrls: ['./uni-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class UniChartComponent extends DashBaseComponent implements OnInit {
  visibility: string = "visible";

  canvas_id: string = "uni-chart";

  timeSpan : string = "all_time";
  statType : string = "clicks";

  data : UniStat[] =
    [ new UniStat("clicks", 0, "2023-06-06"),
      new UniStat("clicks", 1, "2023-06-07"),
      new UniStat("clicks", 2, "2023-06-08"),
      new UniStat("clicks", 8, "2023-09-05"),
      new UniStat("clicks", 9, "2023-09-06"),
      new UniStat("clicks", 10, "2023-09-07"),
      new UniStat("clicks", 6, "2023-07-06"),
      new UniStat("clicks", 7, "2023-07-07"),
      new UniStat("clicks", 3, "2023-07-08"),
      new UniStat("clicks", 4, "2023-07-05"),
      new UniStat("clicks", 5, "2023-07-06")
    ];

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7],
    ["day", 1]
  ]);


  getData(event?: Event) {
    if (event !== undefined) {
      if ((event?.target as HTMLInputElement).type == "select-one") this.statType = (event?.target as HTMLInputElement).value;
      if ((event?.target as HTMLInputElement).type == "radio") this.timeSpan = (event?.target as HTMLInputElement).value;
    }
    /*if (this.data == undefined){this.data = this.db.getUniStatsByTypeAndTime(this.statType, (this.timeSpanMap.get(this.timeSpan) ?? 365*2))}
    this.data.then((res : UniStat[]) => {*/
      var res = this.data;
      var statLabel : string[] = [];
      var statData : number[] = [];
      var statDate : string[] = [];

      var time_filtered : UniStat[] = res.filter((stat : UniStat) => {
        var statDate = new Date(Date.parse(stat.date));
        var calcDate = new Date(Date.now() - (this.timeSpanMap.get(this.timeSpan) ?? 365*2) * 24 * 60 * 60 * 1000);
        return statDate >= calcDate;
      });
      time_filtered.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      });
      /*});*/
    for (var stat of time_filtered) {
      statLabel.push(stat.type);
      statData.push(stat.data);
      statDate.push(stat.date);
    }

    this.createChart(statDate, statLabel, statData, () => {});
  }

  ngOnInit(): void {
    this.getData();
  }

  createChart(labels: string[], fullLabels : string[], data: number[], onClick: (index : number) => void){
    if (this.chart){
      this.chart.destroy();
    }
    // @ts-ignore
    this.chart = new Chart("uni-chart", {
      type: "line",
      data: {
        labels: labels,
        datasets: [{
          label: this.statType,
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
            max: Math.max.apply(null, data)
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
