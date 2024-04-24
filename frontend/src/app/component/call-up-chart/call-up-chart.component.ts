import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../util/Util";

export class Callup {
  clicks : number = 0;
  sensibleClicks : number = 0;
  visitors : number = 0;
  date : string = "00-00-0000"; // interpreted as hour if timespan = day

  constructor(clicks : number, sensibleClicks : number, visitors : number, date : string) {
    this.clicks = clicks;
    this.sensibleClicks = sensibleClicks;
    this.visitors = visitors;
    this.date = date;
  }
}
export class CategoriesData {
  clicks : number[] = [];
  besucher : number[] = [];
  labels : string[] = [];

  constructor(clicks : number[], besucher : number[], labels : string[]) {
    this.clicks = clicks;
    this.besucher = besucher;
    this.labels = labels;
  }
}

@Component({
  selector: 'dash-call-up-chart',
  templateUrl: './call-up-chart.component.html',
  styleUrls: ['./call-up-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class CallUpChartComponent extends DashBaseComponent implements OnInit {
  categories_chart: any;
  slidedOut = false;

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

  categories =
    ["Startseite",
      "Artikel",
      "News",
      "Blog",
      "Podcast",
      "Whitepaper",
      "Ratgeber",
      "Über",
      "Impressum",
      "Preisliste",
      "Partner",
      "DSV",
      "Newsletter",
      "Ziel",
      "AGBs"];

  categoriesViews : number[] = [];
  categoriesVisitors : number[] = [];


  getData(event?: Event) {
    if (event !== undefined) {
      if ((event?.target as HTMLInputElement).type == "radio") this.timeSpan = (event?.target as HTMLInputElement).value;
    }
    var system_time: number;
    this.api.getSystemTimeHour().then(res => system_time = res).then(() => {
      this.api.getCallupsByTime((this.timeSpanMap.get(this.timeSpan) ?? 365 * 2)).then((res: Callup[]) => {
        this.data = res;

        this.time_filtered = this.data;


        if (this.timeSpan == "day") {
          this.time_filtered.sort((a, b) => {
            return Number.parseInt(a.date) - Number.parseInt(b.date);
          });
          let sublist = this.time_filtered.splice(0, system_time + 1);
          this.time_filtered.push(...sublist);
        } else {
          this.time_filtered.sort((a, b) => {
            return new Date(a.date).getTime() - new Date(b.date).getTime();
          });
        }

        this.createChart(this.time_filtered, this.timeSpan);
      });
    });
    if (this.timeSpan == "all_time") {
      this.api.getCallpusByCategoriesAllTime().then(res => {
        this.categories = res.labels;
        this.categoriesViews = res.clicks;
        this.categoriesVisitors = res.besucher;
        this.createCategoriesChart(this.categories, this.categoriesViews, this.categoriesVisitors, "Immer");
      });
    } else {
      this.api.getCallupsByCategoriesNewest().then(res => {
        this.categories = res.labels;
        this.categoriesViews = res.clicks;
        this.categoriesVisitors = res.besucher;
        this.createCategoriesChart(this.categories, this.categoriesViews, this.categoriesVisitors, "Heute");
      });
    }
  }

  ngOnInit(): void {
    this.getData();
    this.setToolTip("Hier werden die Aufrufe und einzigartigen Besucher pro Zeit dargestellt.<br><br> Auf der rechten Seite befindet sich eine Auflistung der Aufrufe, aufgeteilt nach den Unterkategorien des Marktplatzes.", 2);
  }

  createCategoriesChart(labels: string[], clicksData : number[], visitorsData: number[], timestamp : string){
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
        labels: labels,
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
        indexAxis: "y",
        layout: {
          padding: {
            right: -10
          }
        },
        scales: {
          x: {
            min: 0,
            stacked: false,
          },
          y: {
            stacked: true,
            ticks: {
              autoSkip: false
            }
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          title: {
            display: true,
            text: timestamp,
            position: "bottom",
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
          },
        },
        interaction: {
          mode: "y",
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
    var clicksDataRAW : number[] = [];
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
      clicksData.push(callup.sensibleClicks);
      clicksDataRAW.push(callup.clicks);
      visitorsData.push(callup.visitors);
    }

    // @ts-ignore
    this.chart = new Chart("uni-chart", {
      type: "line",
      data: {
        labels: timestamps,
        datasets: [{
          label: "Aufrufe (Unbereinigt)",
          data: clicksDataRAW,
          backgroundColor: DashColors.GREY,
          borderColor: DashColors.GREY,
          borderJoinStyle: 'round',
          borderWidth: 3,
          hidden: true
        },{
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
            ticks: {
              callback: tickValue => Util.formatNumbers(tickValue)
            }
          },
          x: {
            display: true,
            ticks: {
              autoSkip: true,
              maxRotation: 0,
              maxTicksLimit: 15
            }
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
          },
          annotation: {
            annotations: [
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.RED,
                value: (Number(clicksData.reduce((previousValue, currentValue) => previousValue + currentValue, 0)) / timestamps.length),
                borderWidth: 2,
                label: {
                  content: Util.formatNumbers((Number(clicksData.reduce((previousValue, currentValue) => previousValue + currentValue, 0)) / timestamps.length).toFixed()) + " Ø",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 14
                  }
                }
              },
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.BLUE,
                value: (Number(visitorsData.reduce((previousValue, currentValue) => previousValue + currentValue, 0)) / timestamps.length),
                borderWidth: 2,
                label: {
                  content: Util.formatNumbers((Number(visitorsData.reduce((previousValue, currentValue) => previousValue + currentValue, 0)) / timestamps.length).toFixed()) + " Ø",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 14
                  }
                }
              }
            ]
          },
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
    if (timespan != "day"){
      this.api.getCallupsByCategoriesByDate(date).then(res => {
        this.categories = res.labels;
        this.categoriesViews = res.clicks;
        this.categoriesVisitors = res.besucher;
        this.createCategoriesChart(this.categories, this.categoriesViews, this.categoriesVisitors, date);
      });
    } else if (timespan == "day"){
      this.api.getSystemTimeHour().then(currHour => {
        let requestDate;
        if (Number(date) > currHour) requestDate = Util.getFormattedNow(-1);
        else requestDate = Util.getFormattedNow();
        this.api.getCallupsByCategoriesByDateTime(requestDate, Number(date)).then(res => {
          this.categories = res.labels;
          this.categoriesViews = res.clicks;
          this.categoriesVisitors = res.besucher;
          this.createCategoriesChart(this.categories, this.categoriesViews, this.categoriesVisitors, date + "Uhr");
        });
      });
    }
    else if (timespan == "all_time"){
      this.api.getCallpusByCategoriesAllTime().then(res => {
        this.categories = res.labels;
        this.categoriesViews = res.clicks;
        this.categoriesVisitors = res.besucher;
        this.createCategoriesChart(this.categories, this.categoriesViews, this.categoriesVisitors, "Immer");
      });
    }
  }

}
