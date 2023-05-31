import {AfterViewInit, Component, EventEmitter, OnInit} from '@angular/core';
import {ActiveElement, Chart, ChartEvent, ChartType} from "chart.js/auto";
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {DbService} from "../../services/db.service";
import {PostComponent} from "../post/post.component";
import {Post} from "../../Post";


@Component({
  selector: 'dash-post-chart',
  templateUrl: './post-chart.component.html',
  styleUrls: ['./post-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})

export class PostChartComponent extends DashBaseComponent implements OnInit{

  visibility: string = "hidden";

  chart : any;
  canvas_id: string = "chart";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];

  timeSpan : string = "all_time";
  postType : string = "artikel";

  data :any;

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7]
  ]);


  createChart(labels: string[], data: number[], onClick: (index : number) => void){
    Chart.defaults.color = "#000"
    if (this.chart){
      this.chart.destroy();
    }
    // @ts-ignore
    this.chart = new Chart(this.canvas_id, {
      type: "line",
      data: {
        labels: labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: "rgb(148,28,62)",
          borderColor: "rgb(148,28,62)",
          borderJoinStyle: 'round',
          tension: 0.5
        }]
      },
      options: {
        aspectRatio: 2.5,
        scales: {
          y: {
            min: 0,
            max: 1
          },
          x: {
            display: false
          }
        },
        plugins: {
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
            display: false
          }
        },
        interaction: {
          mode: "nearest"
        },
        onClick(event: ChartEvent, elements: ActiveElement[], chart: Chart) {
          onClick(elements[0].index);
        }
      }
    })
  }


  getData(event?: Event){
    if (event !== undefined) {
      if ((event?.target as HTMLInputElement).type == "select-one") this.postType = (event?.target as HTMLInputElement).value;
      if ((event?.target as HTMLInputElement).type == "radio") this.timeSpan = (event?.target as HTMLInputElement).value;
    }
    if (this.data == undefined){this.data = this.db.getUserPostsWithStats("1").then();}
    this.data.then((res : Post[]) => {
      var postLabel : string[] = [];
      var postData : number[] = [];


      let time_filtered : Post[] = res.filter((post : Post) => {
        var postDate = new Date(Date.parse(post.date));
        var calcDate = new Date(Date.now() - (this.timeSpanMap.get(this.timeSpan) ?? 365*2) * 24 * 60 * 60 * 1000);
        return postDate >= calcDate;
      }).filter((post : Post) => {
        return post.type == this.postType;
      })
      time_filtered.sort((a, b) => {
        return new Date(a.date).getTime() - new Date(b.date).getTime();
      })

      for (var post of time_filtered) {
        postLabel.push(post.title);
        postData.push(Number(post.performance));
      }

      this.createChart(postLabel, postData, (index) => {this.grid_reference?.addCard({
        col: 0,
        row: 0,
        type: PostComponent,
        height: 1, width: 2})});
    })
    .finally(() => this.visibility = "visible");
  }


  ngOnInit(): void {
    this.setToolTip("Diese Grafik zeigt die Performance all ihrer Beiträge, im angegebenen Zeitraum.")
    this.getData();
  }

}
