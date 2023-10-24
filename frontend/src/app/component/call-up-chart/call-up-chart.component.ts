import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../util/Util";

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
  categories_chart: any;

  timeSpan : string = "month";

  data : Callup[] = [];
  time_filtered : Callup[] = [];

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

        this.time_filtered = this.data;


        if (this.timeSpan == "day"){
          this.time_filtered.sort((a, b) => {
            return Number.parseInt(a.date) - Number.parseInt(b.date);
          });
          let sublist  = this.time_filtered.splice(0, system_time +1);
          this.time_filtered.push(...sublist);
        } else {
          this.time_filtered.sort((a, b) => {
            return new Date(a.date).getTime() - new Date(b.date).getTime();
          });
        }

        this.createChart(this.time_filtered, this.timeSpan);
      });
    });
    this.db.getCallupsByCategoriesNewest().then(res => {
      let views = res.slice(0,6);
      let footer_views = res.slice(7).reduce((previousValue, currentValue) => previousValue + currentValue);
      let visitors = res.slice(15,21);
      let footer_visitors = res.slice(21).reduce((previousValue, currentValue) => previousValue + currentValue);
      views.push(footer_views);
      visitors.push(footer_visitors);
      this.createCategoriesChart(views, visitors, "Heute");
    })
  }
  ngOnInit(): void {
    this.getData();
    this.setToolTip("Hier werden die Aufrufe und einzigartigen Besucher pro Zeit dargestellt. Unter \"24h\" befindet sich eine Auflistung der letzten 23 Stunden.");
    var slidedOut = false;
    var slideOutButton = document.querySelector("#slide-out-button") as HTMLDivElement;
    var uniChartBox = document.querySelector("#uni-chart-box") as HTMLDivElement;
    var categoriesChartBox = document.querySelector("#categories-chart-box") as HTMLDivElement;
    if (slideOutButton && uniChartBox && categoriesChartBox){
      slideOutButton.addEventListener("click", evt => {
        if (!slidedOut){
          uniChartBox.style.width = "49%";
          categoriesChartBox.style.width = "49%";
          slideOutButton.innerHTML = "<p>></p>"
          slidedOut = true;
        } else {
          uniChartBox.style.width = "73%";
          categoriesChartBox.style.width = "25%";
          slideOutButton.innerHTML = "<p><</p>"
          slidedOut = false;
        }
      })
    }
  }

  createCategoriesChart(clicksData : number[], visitorsData: number[], timestamp : string){
    if (this.categories_chart){
      this.categories_chart.destroy();
    }

    var c_max = Math.max(...clicksData);
    var v_max = Math.max(...visitorsData);

    // @ts-ignore
    this.categories_chart = new Chart("categories-chart", {
      type: "bar",
      data:
        {
        labels: ["Startseite","Artikel","News","Blog","Podcast","Whitepaper","Ratgeber","Footer"],
        datasets: [
          {
            label: "Besucher",
            data: visitorsData,
            backgroundColor: DashColors.BLUE,
            borderColor: DashColors.BLUE,
            stack: "1"
          },
          {
          label: "Aufrufe",
          data: clicksData,
          backgroundColor: DashColors.RED,
          borderColor: DashColors.RED,
          stack: "1"
        },
        ]
      },
      options: {
        clip: false,
        aspectRatio: .5,
        maintainAspectRatio: false,
        layout: {
        },
        scales: {
          y: {
            min: 0,
            stacked: false,
          },
          x: {
            stacked: true
          }
        },
        plugins: {
          datalabels: {
            display: (ctx) => {
              if (clicksData[ctx.dataIndex] >= (c_max * 0.1) || visitorsData[ctx.dataIndex] >= (v_max * 0.1)){
                return true;
              }
              return false;
            },
            font: {
              size: 8
            }
          },
          title: {
            display: true,
            text: timestamp,
            position: "top",
            fullSize: true,
            font: {
              size: 14,
              weight: "bold",
              family: "'Helvetica Neue', sans-serif"
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
          mode: "x",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
        },
      }
    })
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
        if (callup.date == "0"){
          timestamps.push("Heute");
        } else {
          timestamps.push(callup.date  + " Uhr");
        }
      }
      else {
        timestamps.push(Util.formatDate(callup.date));
      }
      clicksData.push(callup.clicks);
      visitorsData.push(callup.visitors);
    }

    const max = Math.max.apply(null, clicksData);

    // @ts-ignore
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
                if (timeSpan != "day"){
                  // @ts-ignore
                  return Util.getDayString(new Date(callups[tooltipItems.at(0).dataIndex].date).getDay()) + " - " + timestamps[tooltipItems.at(0).dataIndex];
                }
                  // @ts-ignore
                return timestamps[tooltipItems.at(0).dataIndex]
              }
            }
          }
        },
        interaction: {
          mode: "x",
          intersect: true
        },
        onClick: this.onClickHandler.bind(this, this.timeSpan, this.time_filtered)
      }
    })
  }

  public onClickHandler(timeSpan : string, callups : Callup[], event : ChartEvent, chartElements : any){
    this.getCategoriesData(callups[chartElements.at(0).index].date, timeSpan);
  }

  public getCategoriesData(date : string, timespan : string){
    let views = [];
    let visitors = [];

    let footer_views = 0;
    let footer_visitors = 0;
    if (timespan != "day"){
      this.db.getCallupsByCategoriesByDate(date).then(res => {
        views = res.slice(0,6);
        footer_views = res.slice(7).reduce((previousValue, currentValue) => previousValue + currentValue);
        visitors = res.slice(15,21);
        footer_visitors = res.slice(21).reduce((previousValue, currentValue) => previousValue + currentValue);
        views.push(footer_views);
        visitors.push(footer_visitors);
        this.createCategoriesChart(views, visitors, date);
      });
    } else if (timespan == "day"){
      this.db.getCallupsByCategoriesByDateTime(Util.getFormattedNow(), Number(date)).then(res => {
        views = res.slice(0,6);
        footer_views = res.slice(7).reduce((previousValue, currentValue) => previousValue + currentValue);
        visitors = res.slice(15,21);
        footer_visitors = res.slice(21).reduce((previousValue, currentValue) => previousValue + currentValue);
        views.push(footer_views);
        visitors.push(footer_visitors);
        this.createCategoriesChart(views, visitors, date + " Uhr");
      });
    }
  }

}
