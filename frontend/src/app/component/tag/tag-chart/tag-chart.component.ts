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

  selectedTag : Tag = new Tag("378", "IT-Sicherheit");

  timeSpan : string = "month";
  data : Promise<TagStats[]> | undefined;

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
        tagDate.push(Util.formatDate(tagStats.date));
      }
      this.createChart(tagViewsPosts, tagViewsCat, tagCount, tagDate, [DashColors.RED, DashColors.DARK_RED, DashColors.BLUE], res[0].name);

    }).finally(() => {this.visibility = "visible"});
  }

  ngOnInit(): void {
    this.setToolTip("Diese Grafik zeigt die Views und Anzahl aller Beiträge zum Thema, im angegebenen Zeitraum.<br><br>" +
      "Sie können einen Tag anwählen, um dessen Informationen hier anzuzeigen.");
    this.getData();
    SysVars.SELECTED_TAG.subscribe((tag) => {
      this.selectedTag = tag;
      this.getData()
    })
  }

  private createChart(dataViewsPost: number[], dataViewsCat: number[],dataCount: number[], dates: string[], colors : string[], tagName : string) {
    if (this.chart){
      this.chart.destroy();
    }
    var timestamps = [];
    for (var date of dates) {
      if (date == "day"){
        timestamps.push(new Date(date).getHours().toString());
      }
      else {
        timestamps.push(date);
      }
    }

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
          borderWidth: 5
        },
        {
          label: "Views (Kategorie)",
          data: dataViewsCat,
          backgroundColor: colors[1],
          borderColor: colors[1],
          borderJoinStyle: 'round',
          borderWidth: 5
        },
        {
          label: "Beiträge zum Thema",
          data: dataCount,
          backgroundColor: colors[2],
          borderColor: colors[2],
          borderJoinStyle: 'round',
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
            min: 0
          },
          yCount: {
            min: 0,
            position: "right"
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
            display: true,
            text: tagName,
            position: "top",
            fullSize: true,
            font: {
              size: 18,
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
          mode: "nearest",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
        },
      }
    })
  }
}
