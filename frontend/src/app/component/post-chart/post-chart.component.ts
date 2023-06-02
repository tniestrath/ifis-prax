import {AfterViewInit, Component, EventEmitter, OnInit} from '@angular/core';
import {ActiveElement, Chart, ChartEvent, ChartType, TooltipItem} from "chart.js/auto";
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {DbService} from "../../services/db.service";
import {PostComponent} from "../post/post.component";
import {Post} from "../../Post";
import {UserService} from "../../services/user.service";
import {color} from "chart.js/helpers";


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

  data : Promise<Post[]> | undefined;
  max_performance: Promise<number> |undefined;
  max_relevance: Promise<number> |undefined;

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7]
  ]);



  createChart(labels: string[], data: number[], data2: number[], onClick: (index : number) => void){
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
          label: "Performance",
          data: data,
          backgroundColor: "rgb(148,28,62)",
          borderColor: "rgb(148,28,62)",
          borderJoinStyle: 'round',
          borderWidth: 5
        },
        {
          label: "Relevanz",
          data: data2,
          backgroundColor: "rgb(229,229,229)",
          borderColor: "rgb(229,229,229)",
          borderJoinStyle: 'round',
          borderWidth: 5
        }]
      },
      options: {
        aspectRatio: 2.8,
        layout: {
          padding: {
            bottom: -50
          }
        },
        scales: {
          y: {
            min: 0,
            max: 100,
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
              //@ts-ignore
              label: ((tooltipItem) => {
                if (tooltipItem.datasetIndex == 0){
                  return "Performance: " + data[tooltipItem.dataIndex].toFixed();
                }else if (tooltipItem.datasetIndex == 1){
                  return "Relevanz: " + data2[tooltipItem.dataIndex].toFixed();
                }
              })
            }
          },
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
    if (this.data == undefined){this.data = this.db.getUserPostsWithStats(UserService.USER_ID)}
    if (this.max_performance == undefined){this.max_performance = this.db.getMaxPerformance()}
    if (this.max_relevance == undefined){this.max_relevance = this.db.getMaxRelevance()}
    Promise.all([this.max_performance, this.max_relevance]).then((value) => {
      // @ts-ignore
      this.data.then((res : Post[]) => {
        var postLabel : string[] = [];
        var postData : number[] = [];
        var postDataRelevance : number[] = [];

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
          postData.push((post.performance / value[0])*100)
          postDataRelevance.push((post.relevance / value[1])*100)
        }
        this.createChart(postLabel, postData, postDataRelevance, (index) => {
        });
      })
        .finally(() => {this.visibility = "visible"});
    });
  }


  ngOnInit(): void {
    this.setToolTip("Diese Grafik zeigt die Performance all ihrer Beiträge, im angegebenen Zeitraum.")
    this.getData();
  }

}
