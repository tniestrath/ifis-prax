import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {visibility} from "html2canvas/dist/types/css/property-descriptors/visibility";
import {SysVars} from "../../../services/sys-vars-service";
import {Post} from "../../post/Post";
import {Tag, TagRanking, TagStats} from "../Tag";



@Component({
  selector: 'dash-tag-chart',
  templateUrl: './tag-chart.component.html',
  styleUrls: ['./tag-chart.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagChartComponent extends DashBaseComponent implements OnInit{
  canvas_id: string = "tag-chart-canvas";
  private visibility: string = "hidden";

  selectedTag_id : number = 0;

  timeSpan : string = "all_time";
  data : Promise<TagStats[]> | undefined;

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7]
  ]);

  getData($event: Event) {
    if (this.data == undefined){this.data = this.db.getTagStatsByID(this.selectedTag_id)}
    this.data.then((res : TagStats[]) => {
      var tagLabel : string[] = [];
      var tagData : number[] = [];
      var tagDataRelevance : number[] = [];
      var tagDataDate : string[] = [];

      var tagIds :number[] = [];

      let time_filtered : TagStats[] = res.filter((tagStats : TagStats) => {
        var tagStatsDate = new Date(Date.parse(tagStats.date));
        var calcDate = new Date(Date.now() - (this.timeSpanMap.get(this.timeSpan) ?? 365*2) * 24 * 60 * 60 * 1000);
        return tagStatsDate >= calcDate;
      })
      time_filtered.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      })
      let fullLabels : string[] = [];
      for (var tagStats of time_filtered) {
        let label = tagStats.name;
        fullLabels.push(tagStats.name);
        let space_index = label.indexOf(" ");
        let sec_space_index = label.indexOf(" ", space_index+1);
        if (label.length > 10){
          if (sec_space_index < 10){
            label = label.slice(0, sec_space_index);
          }
          if (space_index < 10 !&& sec_space_index < 10){
            label = label.slice(0, space_index);
          }
          else {
            label = label.slice(0, 10)
          }
          label += "...";
        }
        tagLabel.push(label);
        tagData.push(Number(tagStats.count));
        tagDataRelevance.push(Number(tagStats.relevance));
        tagDataDate.push(new Date(tagStats.date).toLocaleDateString());
        tagIds.push(Number(tagStats.id));
      }
      this.createChart(tagLabel, fullLabels, tagData, tagDataRelevance, tagDataDate, () => {});
    }).finally(() => {this.visibility = "visible"});
  }

  ngOnInit(): void {
    SysVars.SELECTED_TAG.subscribe((tr) => {
      this.selectedTag_id = Number(tr.id);
    })
  }

  private createChart(tagLabel: string[], fullLabels: string[], tagData: number[], tagDataRelevance: number[], tagDataDate: string[], param6: () => void) {

  }
}
