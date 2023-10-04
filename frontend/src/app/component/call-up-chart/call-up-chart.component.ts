import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent, TooltipItem} from "chart.js/auto";
import Util, {DashColors} from "../../util/Util";
import _default from "chart.js/dist/plugins/plugin.legend";
import labels = _default.defaults.labels;

export class Callup {
  clicks : number = 0;
  visitors : number = 0;
  date : string = "00-00-0000"; // interpreted as hour if timespan = day

  constructor(clicks : number, visitors : number, date : string) {
    this.clicks = clicks;
    this.visitors = visitors;
    this.date = date;
  }
}

@Component({
  selector: 'dash-call-up-chart',
  templateUrl: './call-up-chart.component.html',
  styleUrls: ['./call-up-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class CallUpChartComponent extends DashBaseComponent implements OnInit {
  visibility: string = "visible";

  canvas_id: string = "uni-chart";

  timeSpan : string = "month";

  data : Callup[] = [];

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
    var system_time : number;
    this.db.getSystemTimeHour().then(res => system_time = res).then(() => {
      this.db.getCallupsByTime((this.timeSpanMap.get(this.timeSpan) ?? 365*2)).then((res : Callup[]) => {
        this.data = res;

        var time_filtered : Callup[] = this.data;


        if (this.timeSpan == "day"){
          time_filtered.sort((a, b) => {
            return Number.parseInt(a.date) - Number.parseInt(b.date);
          });
          let sublist  = time_filtered.splice(0, system_time);
          time_filtered.push(...sublist);
        } else {
          time_filtered.sort((a, b) => {
            return new Date(a.date).getTime() - new Date(b.date).getTime();
          });
        }

        this.createChart(time_filtered, this.timeSpan);
      });
    })
  }
  ngOnInit(): void {
    this.getData();
    this.setToolTip("Hier werden die Aufrufe und einzigartigen Besucher pro Zeit dargestellt. Unter Heute befindet sich eine Auflistung der letzten 23 Stunden.");
  }

  createChart(callups : Callup[], timeSpan : string){
    if (this.chart){
      this.chart.destroy();
    }

    var timestamps : string[] = [];
    var clicksData : number[] = [];
    var visitorsData : number[] = [];
    for (var callup of callups) {
      if (timeSpan == "day"){
        timestamps.push(callup.date  + " Uhr");
      }
      else {
        timestamps.push(Util.formatDate(callup.date));
      }
      clicksData.push(callup.clicks);
      visitorsData.push(callup.visitors);
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
          backgroundColor: DashColors.RED,
          borderColor: DashColors.RED,
          borderJoinStyle: 'round',
          borderWidth: 5
        },
        {
          label: "Besucher",
          data: visitorsData,
          backgroundColor: DashColors.BLUE,
          borderColor: DashColors.BLUE,
          borderJoinStyle: 'round',
          borderWidth: 5
        }]
      },
      options: {
        clip: false,
        aspectRatio: 2.8,
        maintainAspectRatio: false,
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
              title(tooltipItems): string {
                  // @ts-ignore
                  return Util.getDayString(new Date(callups[tooltipItems.at(0).dataIndex].date).getDay()) + " - " + timestamps[tooltipItems.at(0).dataIndex];
              }
            }
          }
        },
        interaction: {
          mode: "x",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
        },
      }
    })
  }
}
