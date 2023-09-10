import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {visibility} from "html2canvas/dist/types/css/property-descriptors/visibility";
import {SysVars} from "../../../services/sys-vars-service";
import {Post} from "../../post/Post";
import {Tag, TagRanking, TagStats} from "../Tag";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {DashColors} from "../../../util/Util";



@Component({
  selector: 'dash-tag-chart',
  templateUrl: './tag-chart.component.html',
  styleUrls: ['./tag-chart.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagChartComponent extends DashBaseComponent implements OnInit{
  canvas_id: string = "tag-chart";
  private visibility: string = "hidden";

  selectedTag_id : number = 0;

  timeSpan : string = "all_time";
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
    if (this.data == undefined){this.data = this.db.getTagStatsByID(this.selectedTag_id,this.timeSpanMap.get(this.timeSpan) || 365*2)}
    this.data.then((res : TagStats[]) => {
      var tagLabel : string[] = [];
      var tagViews : number[] = [];
      var tagRelevance : number[] = [];
      var tagCount : number[] = [];
      var tagDate : string[] = [];
      var tagIds :number[] = [];

      res.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      })
      for (var tagStats of res) {
        tagLabel.push(tagStats.name);
        tagCount.push(Number(tagStats.count));
        tagRelevance.push(Number(tagStats.relevance));
        tagViews.push(Number(tagStats.views));
        tagDate.push(new Date(tagStats.date).toLocaleDateString());
        tagIds.push(Number(tagStats.id));
      }
      console.log(tagDate);
      this.createChart(tagLabel, tagViews, tagRelevance, tagCount, tagDate);
    }).finally(() => {this.visibility = "visible"});
  }

  ngOnInit(): void {
    SysVars.SELECTED_TAG.subscribe((id) => {
      this.selectedTag_id = id;
      this.getData()
    })
  }

  private createChart(tagLabel: string[], tagViews: number[], tagRelevance: number[], tagCount: number[], tagDate: string[]) {
    if (this.chart){
      this.chart.destroy();
    }
    var timestamps = [];
    for (var date of tagDate) {
      if (date == "day"){
        timestamps.push(new Date(date).getHours().toString());
      }
      else {
        timestamps.push(date);
      }
    }

    const max = Math.max.apply(null, tagViews);

    // @ts-ignore
    this.chart = new Chart("tag_chart", {
      type: "line",
      data: {
        labels: timestamps,
        datasets: [{
          label: "Views",
          data: tagViews,
          backgroundColor: DashColors.Red,
          borderColor: DashColors.Red,
          borderJoinStyle: 'round',
          borderWidth: 5
        },
        {
          label: "Relevanz",
          data: tagRelevance,
          backgroundColor: DashColors.Blue,
          borderColor: DashColors.Blue,
          borderJoinStyle: 'round',
          borderWidth: 5
        },
        {
          label: "Beiträge zum Thema",
          data: tagCount,
          backgroundColor: DashColors.Black,
          borderColor: DashColors.Black,
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
