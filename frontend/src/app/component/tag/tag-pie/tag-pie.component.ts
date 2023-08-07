import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {Tag, TagRanking} from "../Tag";
import {EmptyObject} from "chart.js/dist/types/basic";

@Component({
  selector: 'dash-tag-pie',
  templateUrl: './tag-pie.component.html',
  styleUrls: ['./tag-pie.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagPieComponent extends DashBaseComponent implements OnInit{
  colors : string[] = ["#5A7995", "#354657", "rgb(148,28,62)", "rgb(84, 16, 35)", "#000"];

  ngOnInit(): void {
    this.db.getAllTagsPostCount(5).then(res => {
      let map : Map<string, number> = new Map(Object.entries(res).sort(([tag1, count1], [tag2, count2]) => count2 -count1));
      let labels :string[] = Array.from(map.keys());
      let data : number[] = Array.from(map.values());
      this.createChart(labels.slice(0, 5), data.slice(0, 5), this.colors);
    })
  }

    createChart(labels : string[], data : number[], colors : string[]){
      const donughtInner  = {
        id: "donughtInner",
        afterDatasetsDraw(chart: Chart, args: EmptyObject, options: 0, cancelable: false) {
          const {ctx, data, chartArea: {top, bottom, left, right, width, height}, scales: {r}} = chart;
          ctx.save();
          const x = chart.getDatasetMeta(0).data[0].x;
          const y = chart.getDatasetMeta(0).data[0].y;
          
          ctx.globalCompositeOperation = 'source-over';

          ctx.font = (chart.chartArea.height / 22) + "px sans-serif";
          ctx.fillStyle = "#5A7995";
          ctx.textAlign = "center";
          ctx.textBaseline = "middle";
          var index = -45;
          var first_labels = labels.slice(0, 5);
          var cut_labels : string[] = []
          // @ts-ignore
          for (var labelsKey of first_labels) {
            if (labelsKey.length >= 18) {
              cut_labels.push(labelsKey.slice(0, 18).concat("..."));
            } else {
              cut_labels.push(labelsKey);
            }
            ctx.font = ((chart.chartArea.height / 22)) + "px sans-serif";
            // @ts-ignore
            ctx.fillStyle = chart.legend?.legendItems[labels.indexOf(labelsKey)].fillStyle;
            // @ts-ignore
            ctx.fillText(cut_labels.at(labels.indexOf(labelsKey)), x, y + index);
            index = index + 25;
          }
        }
      }


      return new Chart("tagpie", {
        type: "doughnut",
        data: {
          labels: labels,
          datasets: [{
            label: "",
            data: data,
            backgroundColor: colors,
            borderRadius: 5,
            borderWidth: 5
          }]
        },
        options: {
          aspectRatio: 1,
          cutout: "60%",
          plugins: {
            title: {
              display: false,
              text: "",
              position: "top",
              fullSize: true,
              font: {
                size: 50,
                weight: "bold",
                family: 'Times New Roman'
              }
            },
            legend: {
              onClick: (e) => null,
              display: false
            },
            tooltip: {
              displayColors: false,
              titleFont: {
                size: 20
              },
              bodyFont: {
                size: 15
              }
            },
          }
        },
        //@ts-ignore
        plugins: [donughtInner]
      })
    }

}
