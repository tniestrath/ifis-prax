import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {Tag, TagRanking} from "../Tag";

@Component({
  selector: 'dash-tag-pie',
  templateUrl: './tag-pie.component.html',
  styleUrls: ['./tag-pie.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagPieComponent extends DashBaseComponent implements OnInit{
  colors : string[] = ["#5A7995", "rgb(148,28,62)", "rgb(84, 16, 35, 33)"];

  ngOnInit(): void {
    this.db.getAllTagsPostCount(15).then(res => {
      let map : Map<string, number> = new Map(Object.entries(res));
      let labels :string[] = Array.from(map.keys());
      let data : number[] = Array.from(map.values());
      console.log(labels)
      this.createChart(labels, data);
    })
  }

    createChart(labels : string[], data : number[], ){
      return new Chart("tagpie", {
        type: "pie",
        data: {
          labels: labels,
          datasets: [{
            label: "",
            data: data,
            backgroundColor: this.colors,
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
        }
      })
    }

}
