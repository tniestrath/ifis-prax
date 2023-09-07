import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {DashColors} from "../../util/Util";

export class UniStat {
  clicks : number = 0;
  visitors : number = 0;
  date : string = "00-00-0000";

  constructor(clicks : number, visitors : number, date : string) {
    this.clicks = clicks;
    this.visitors = visitors;
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

  data : UniStat[] =
    [ new UniStat(0,0, "2023-06-06"),
      new UniStat(1,1, "2023-06-07"),
      new UniStat(2,1, "2023-06-08"),
      new UniStat(80, 70, "2023-09-05"),
      new UniStat(900,800, "2023-09-06"),
      new UniStat(1000, 850, "2023-09-07"),
      new UniStat(69, 66, "2023-07-07"),
      new UniStat(75, 55, "2023-07-08"),
      new UniStat(3, 2, "2023-07-05"),
      new UniStat(4, 4, "2023-07-06")
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
      if ((event?.target as HTMLInputElement).type == "radio") this.timeSpan = (event?.target as HTMLInputElement).value;
    }
    /*if (this.data == undefined){this.data = this.db.getUniStatsByTypeAndTime(this.statType, (this.timeSpanMap.get(this.timeSpan) ?? 365*2))}
    this.data.then((res : UniStat[]) => {*/
      var res = this.data;

      var time_filtered : UniStat[] = res.filter((stat : UniStat) => {
        var statDate = new Date(Date.parse(stat.date));
        var calcDate = new Date(Date.now() - (this.timeSpanMap.get(this.timeSpan) ?? 365*2) * 24 * 60 * 60 * 1000);
        return statDate >= calcDate;
      });
      time_filtered.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      });
      /*});*/
    this.createChart(time_filtered, this.timeSpan);
  }

  ngOnInit(): void {
    this.getData();
  }

  createChart(uniStats : UniStat[], timeSpan : string){
    if (this.chart){
      this.chart.destroy();
    }

    var timestamps : string[] = [];
    var clicksData : number[] = [];
    var visitorsData : number[] = [];
    for (var unistat of uniStats) {
      if (timeSpan == "day"){
        timestamps.push(new Date(unistat.date).getHours().toString());
      }
      else {
        timestamps.push(unistat.date);
      }
      clicksData.push(unistat.clicks);
      visitorsData.push(unistat.visitors);
    }

    const max = Math.max.apply(null, clicksData);

    // @ts-ignore
    this.chart = new Chart("uni-chart", {
      type: "line",
      data: {
        labels: timestamps,
        datasets: [{
          label: "Aufrufe",
          data: clicksData,
          backgroundColor: DashColors.Red,
          borderColor: DashColors.Red,
          borderJoinStyle: 'round',
          borderWidth: 5
        },
        {
          label: "Besucher",
          data: visitorsData,
          backgroundColor: DashColors.Blue,
          borderColor: DashColors.Blue,
          borderJoinStyle: 'round',
          borderWidth: 5
        }]
      },
      options: {
        aspectRatio: 2.8,
        layout: {
          padding: {
            bottom: -45
          }
        },
        scales: {
          y: {
            min: 0,
            max: max
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
            }
          }
        },
        interaction: {
          mode: "nearest",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
        },
      }
    })
  }
}
