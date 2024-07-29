import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";
import {Chart} from "chart.js/auto";

@Component({
  selector: 'dash-post-types-average-views',
  templateUrl: './post-types-average-views.component.html',
  styleUrls: ['./post-types-average-views.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostTypesAverageViewsComponent extends DashBaseComponent implements OnInit{

  ngOnInit(): void {
    this.getData();
  }

  getData(){
    this.api.getPostViewsAverageByType().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res));
      let labels: string[] = [];
      let data: number[] = [];
      let color: string[] = [];
      this.readMap(map, labels, data, color);
      this.createChart(labels, data, color);
    });
  }

  private readMap(map: Map<string, number>, labels: string[], data: number[], color : string[]) {
    map.forEach((value, key) => {
      if (key == "news") {
        labels[0] = Util.firstToUpperCase(key);
        data[0] = (value == 0 || value == undefined ? 0 : value);
        color[0] = DashColors.NEWS;
      }
      if (key == "Blogs") {
        labels[1] = Util.firstToUpperCase(key);
        data[1] = (value == 0 || value == undefined ? 0 : value);
        color[1] = DashColors.BLOG;
      }
      if (key == "artikel") {
        labels[2] = Util.firstToUpperCase(key);
        data[2] = (value == 0 || value == undefined ? 0 : value);
        color[2] = DashColors.ARTICLE;
      }
      if (key == "whitepaper") {
        labels[3] = Util.firstToUpperCase(key);
        data[3] = (value == 0 || value == undefined ? 0 : value);
        color[3] = DashColors.WHITEPAPER;
      }
      if (key == "Podcasts") {
        labels[4] = Util.firstToUpperCase(key);
        data[4] = (value == 0 || value == undefined ? 0 : value);
        color[4] = DashColors.PODCAST;
      }
      if (key == "video") {
        labels[5] = Util.firstToUpperCase(key);
        data[5] = (value == 0 || value == undefined ? 0 : value);
        color[5] = DashColors.VIDEO;
      }
      if (key == "ratgeber") {
        labels[6] = Util.firstToUpperCase(key);
        data[6] = (value == 0 || value == undefined ? 0 : value);
        color[6] = DashColors.RATGEBER;
      }
    })
  }

  createChart(labels : string[], data: number[], colors : string[]) {
    if (this.chart){
      this.chart.destroy();
    }

    // @ts-ignore
    this.chart = new Chart(this.element.nativeElement.querySelector("#stat_chart"), {
      type: "bar",
      data: {
        labels: labels,
        datasets: [{
          label: "Durchschnittliche Aufrufe",
          data: data,
          backgroundColor: colors,
        }]
      },
      options: {
        maintainAspectRatio: false,
        clip: false,
        layout: {
          padding: {
            bottom: 0
          }
        },
        scales: {
          y: {
            display: false
          },
          x: {
            display: true,
            grid: {
              display: false
            }
          }
        },
        plugins: {
          datalabels: {
            display: true
          },
          title: {
            display: false,
            text: "",
            position: "top",
            fullSize: true,
            font: {
              size: 18,
              weight: "bold",
              family: "'Helvetica Neue', sans-serif"
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
        }
      }
    })
  }

}
