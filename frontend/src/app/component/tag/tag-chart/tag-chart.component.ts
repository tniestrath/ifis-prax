import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SysVars} from "../../../services/sys-vars-service";
import {Tag, TagStats} from "../Tag";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../../util/Util";


@Component({
  selector: 'dash-tag-chart',
  templateUrl: './tag-chart.component.html',
  styleUrls: ['./tag-chart.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagChartComponent extends DashBaseComponent implements OnInit{
  canvas_id: string = "tag-chart";
  private visibility: string = "hidden";

  selectedTag : Tag = SysVars.CURRENT_TAG;

  timeSpan : string = "month";
  data : Promise<TagStats[]> | undefined;

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7]
  ]);

  getData(event?: Event) {
    if (event !== undefined) {
      if ((event?.target as HTMLInputElement).type == "radio") this.timeSpan = (event?.target as HTMLInputElement).value;
    }
    this.api.getTagStatsByID(Number.parseInt(this.selectedTag.id),this.timeSpanMap.get(this.timeSpan) || 365*2).then((res : TagStats[]) => {
      var tagViewsPosts : number[] = [];
      var tagViewsCat : number[] = [];
      var tagCount : number[] = [];
      var tagDate : string[] = [];

      /*res.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      })*/

      for (var tagStats of res) {
        tagCount.push(Number(tagStats.count));
        tagViewsPosts.push(Number(tagStats.viewsPosts));
        tagViewsCat.push(Number(tagStats.viewsCat));
        tagDate.push(Util.formatDate(tagStats.date, true));
      }
      this.createChart(tagViewsPosts, tagViewsCat, tagCount, tagDate, [DashColors.RED, DashColors.DARK_RED, DashColors.BLUE]);

    }).finally(() => {this.visibility = "visible"});
  }

  ngOnInit(): void {
    this.setToolTip("Diese Grafik zeigt die Views und Anzahl aller Beiträge zum Thema, im angegebenen Zeitraum.<br><br>" +
      "Sie können einen Tag anwählen, um dessen Informationen hier anzuzeigen.");
    this.getData();
    SysVars.SELECTED_TAG.subscribe((tag) => {
      this.selectedTag = tag;
      SysVars.CURRENT_TAG = tag;
      this.getData()
    })
  }

  private createChart(dataViewsPost: number[], dataViewsCat: number[],dataCount: number[], dates: string[], colors : string[]) {
    if (this.chart){
      this.chart.destroy();
    }
    let timestamps : string[] = [];
    dates.forEach((value, index) => timestamps[index] = value.substring(0, 5));

    // @ts-ignore
    this.chart = new Chart("tag_chart", {
      type: "line",
      data: {
        labels: timestamps,
        datasets: [{
          label: "Views (Beiträge)",
          data: dataViewsPost,
          backgroundColor: colors[0],
          borderColor: colors[0],
          borderJoinStyle: 'round',
          pointHoverRadius: 10,
          borderWidth: 5
        },
        {
          label: "Views (Kategorie)",
          data: dataViewsCat,
          backgroundColor: colors[1],
          borderColor: colors[1],
          borderJoinStyle: 'round',
          pointHoverRadius: 10,
          borderWidth: 5
        },
        {
          label: "Beiträge zum Thema",
          data: dataCount,
          backgroundColor: colors[2],
          borderColor: colors[2],
          borderJoinStyle: 'round',
          pointHoverRadius: 10,
          borderWidth: 5,
          yAxisID: "yCount"
        }]
      },
      options: {
        aspectRatio: 2.8,
        maintainAspectRatio: false,
        clip: false,
        layout: {
          padding: {
            bottom: 0
          }
        },
        scales: {
          y: {
            min: 0,
            ticks: {
              color : colors[0]
            },
            grid: {
              drawOnChartArea: true
            }
          },
          yCount: {
            min: 0,
            position: "right",
            ticks: {
              color : colors[2]
            },
            grid: {
              drawOnChartArea: false
            }
          },
          x: {
            display: true,
            ticks: {
              autoSkip: true,
              maxRotation: 0
            }
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          title: {
            display: false
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
                title(tooltipItems): string | string[] | void {
                  // @ts-ignore
                  return Util.getDayString(Util.readFormattedDate(dates[tooltipItems.at(0).dataIndex].replaceAll(".", "-")).getDay()) + " - " + dates[tooltipItems.at(0).dataIndex];
                }
            }
          }
        },
        interaction: {
          mode: "x",
          intersect: true
        }
      }
    })
  }
}

