import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SysVars} from "../../../services/sys-vars-service";
import {TagStats} from "../Tag";
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

  selectedTag_id : number = 0;

  dataType : string = "views"
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
      if ((event?.target as HTMLInputElement).type == "select-one") this.dataType = (event?.target as HTMLInputElement).value;
    }
    this.api.getTagStatsByID(this.selectedTag_id,this.timeSpanMap.get(this.timeSpan) || 365*2, this.dataType).then((res : TagStats[]) => {
      var tagName : string = "";
      var tagViews : number[] = [];
      var tagRelevance : number[] = [];
      var tagCount : number[] = [];
      var tagDate : string[] = [];
      var tagIds :number[] = [];

      res.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      })
      tagName = res[0].name;
      for (var tagStats of res) {
        tagCount.push(Number(tagStats.count));
        tagRelevance.push(Number(tagStats.relevance));
        tagViews.push(Number(tagStats.views));
        tagIds.push(Number(tagStats.id));
        tagDate.push(Util.formatDate(tagStats.date));
      }
      switch (this.dataType) {
        case "views":
          this.createChart("Views", tagViews, tagDate, DashColors.RED, tagName);
          break;
        case "relevance":
          this.createChart("Relevanz", tagRelevance, tagDate, DashColors.BLUE, tagName);
          break;
        case "count":
          this.createChart("Beiträge zum Thema", tagCount, tagDate, DashColors.BLACK, tagName);
          break;
      }

    }).finally(() => {this.visibility = "visible"});
  }

  ngOnInit(): void {
    this.setToolTip("Diese Grafik zeigt die Views / Relevanz / Anzahl aller Beiträge zum Thema, im angegebenen Zeitraum. " +
      "Sie können einen Tag anwählen, um dessen Informationen hier anzuzeigen.")
    SysVars.SELECTED_TAG.subscribe((id) => {
      this.selectedTag_id = id;
      this.getData()
    })
  }

  private createChart(dataLabel: string, data: number[], dates: string[], color : string, tagName : string) {
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

    const max = Math.max.apply(null, data);

    // @ts-ignore
    this.chart = new Chart("tag_chart", {
      type: "line",
      data: {
        labels: timestamps,
        datasets: [{
          label: dataLabel,
          data: data,
          backgroundColor: color,
          borderColor: color,
          borderJoinStyle: 'round',
          borderWidth: 5
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
