import {Component, OnInit} from '@angular/core';
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "../../Post";
import {UserService} from "../../services/user.service";
import {EmptyObject} from "chart.js/dist/types/basic";


@Component({
  selector: 'dash-post-chart',
  templateUrl: './post-chart.component.html',
  styleUrls: ['./post-chart.component.css', "../../component/dash-base/dash-base.component.css"]
})

export class PostChartComponent extends DashBaseComponent implements OnInit{

  visibility: string = "hidden";

  chart : any;
  canvas_id: string = "chart";

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



  createChart(labels: string[], fullLabels : string[], data: number[], data2: number[], data3: string[], onClick: (index : number) => void){
    Chart.defaults.color = "#000"
    if (this.chart){
      this.chart.destroy();
    }

    const lineConnection  = {
      id: "lineConnection",
      afterDatasetsDraw(chart: Chart, args: EmptyObject, options: 0, cancelable: false) {
        const { ctx, data, chartArea: {top, bottom, left, right, width, height}, scales: {r} } = chart;
        ctx.save();
        for (let i = 0; i < chart.getDatasetMeta(0).data.length; i++) {
          let x = chart.getDatasetMeta(0).data[i].x;
          let y = chart.getDatasetMeta(0).data[i].y;
          let x2 = chart.getDatasetMeta(1).data[i].x;
          let y2 = chart.getDatasetMeta(1).data[i].y;
          if (y > y2){
            if (y-y2 > 10){
              ctx.strokeStyle = "rgb(148,28,62)";
              ctx.lineWidth = 3;
              ctx.beginPath();
              ctx.moveTo(x,y);
              ctx.lineTo(x, y+((y2-y)/2));
              ctx.stroke();
              ctx.strokeStyle = "#5A7995";
              ctx.beginPath();
              ctx.moveTo(x, y+((y2-y)/2));
              ctx.lineTo(x, y2);
              ctx.stroke();
            }
          } else {
            if (y2-y > 10){
              ctx.strokeStyle = "rgb(148,28,62)";
              ctx.lineWidth = 3;
              ctx.beginPath();
              ctx.moveTo(x,y);
              ctx.lineTo(x, y-((y-y2)/2));
              ctx.stroke();
              ctx.strokeStyle = "#5A7995";
              ctx.beginPath();
              ctx.moveTo(x, y-((y-y2)/2));
              ctx.lineTo(x, y2);
              ctx.stroke();
            }
          }

        }
      }
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
          backgroundColor: "#5A7995",
          borderColor: "#5A7995",
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
            display: true
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
              title(tooltipItems): string {
                // @ts-ignore
                return fullLabels[tooltipItems.at(0).dataIndex];
              },
              //@ts-ignore
              label: ((tooltipItem) => {
                if (tooltipItem.datasetIndex == 0) {
                  return "Performance: " + data[tooltipItem.dataIndex].toFixed() + "  Datum: " + data3[tooltipItem.dataIndex];
                } else if (tooltipItem.datasetIndex == 1) {
                  return "Relevanz: " + data2[tooltipItem.dataIndex].toFixed() + "  Datum: " + data3[tooltipItem.dataIndex];
                }
              })
            }
          }
        },
        interaction: {
          mode: "nearest",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
          onClick(elements[0].index);
        },
      },
      // @ts-ignore
      plugins: [lineConnection]
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
        var postDataDate : string[] = [];

        var postIds :number[] = [];

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
        let fullLabels : string[] = [];
        for (var post of time_filtered) {
          let label = post.title;
          fullLabels.push(post.title);
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
          postLabel.push(label);
          postData.push((post.performance / value[0])*100);
          postDataRelevance.push((post.relevance / value[1])*100);
          postDataDate.push(new Date(post.date).toLocaleDateString());
          // @ts-ignore
          postIds.push(post.id);
        }
        this.createChart(postLabel, fullLabels, postData, postDataRelevance, postDataDate, (index) => {
          UserService.SELECTED_POST_ID.emit(postIds[index]);
        });
      })
        .finally(() => {this.visibility = "visible"});
    });
  }


  ngOnInit(): void {
    this.setToolTip("Diese Grafik zeigt die Performance / Relevanz all ihrer Beiträge, im angegebenen Zeitraum.")
    this.getData();
  }

}
