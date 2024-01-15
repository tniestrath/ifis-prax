import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../../../util/Util";
import {SysVars} from "../../../../services/sys-vars-service";

@Component({
  selector: 'dash-user-clicks-chart',
  templateUrl: './user-clicks-chart.component.html',
  styleUrls: ['./user-clicks-chart.component.css', '../../../../component/dash-base/dash-base.component.css']
})
export class UserClicksChartComponent extends DashBaseComponent implements OnInit{

  timeSpanMap = new Map<string, number>([
    ["all_time", 365*2],
    ["half_year", 182],
    ["month", 31],
    ["week", 7],
    ["day", 1]
  ]);

  selectedTimeSpan = 31;

  ngOnInit(): void {
    this.getData();
  }

  getData($event?: Event) {
    if ((event?.target as HTMLInputElement).type == "radio") { // @ts-ignore
      this.selectedTimeSpan = this.timeSpanMap.get((event?.target as HTMLInputElement).value);
    }
    let start = Util.getFormattedNow(-this.selectedTimeSpan);
    let end = Util.getFormattedNow();


    this.db.getUserClicksChartData(SysVars.USER_ID, start, end).then(res => {
      let data = this.collectData(res);
      this.createChart(data.dates, data.profileClicks, data.biggestPost, data.posts, (posts) => {
        let list  = posts.map(value => value.id).reduce((previousValue, currentValue) => previousValue + "-" + currentValue);
        SysVars.SELECTED_POST_IDS.next(list);
      });
    });
  }

  collectData(data: {date : string, profileViews : number, biggestPost: {id: number, title: string, type: string, clicks: number}, posts: {id: number, title: string, type: string, clicks: number}[]}[]){
    let dates = [];
    let profileClicks = [];
    let posts = [];
    let biggestPost = [];

    for (var day of data) {
      dates.push(day.date.substring(0, 10));
      profileClicks.push(day.profileViews);
      posts.push(day.posts);
      biggestPost.push(day.biggestPost);
    }

    return { dates, profileClicks, biggestPost, posts};
  }

  createChart(dates: string[], profileClicksData: number[], biggestPost: {id: number, title: string, clicks: number, type: string}[], posts : {id: number, title: string, clicks: number, type: string}[][], onClick: (posts: any[]) => void){
    if (this.chart) {
      this.chart.destroy();
    }

    var postMax = biggestPost.map(post => post.clicks);
    var postMaxType = biggestPost.map(post => post.type);
    var postMaxTitle = biggestPost.map(post => post.title);

    // @ts-ignore
    var postClicksMax = Math.max(...postMax);

    // @ts-ignore
    this.chart = new Chart(this.element.nativeElement.querySelector("#user-clicks-chart"), {
      type: "line",
      data: {
        labels: dates,
        datasets: [{
          label: "Profilaufrufe",
          type: "line",
          data: profileClicksData,
          backgroundColor: DashColors.GREY,
          borderColor: DashColors.GREY,
          borderJoinStyle: 'round',
          borderWidth: 2,
          pointBorderWidth: 0,
          pointRadius: (ctx, options) : number => {
            // @ts-ignore
            if(posts.at(ctx.dataIndex) != null){
              // @ts-ignore
              return Math.max(12 * (postMax.at(ctx.dataIndex) / postClicksMax), 5);
            }
            return 2.5;
          },
          pointBackgroundColor: (ctx, options): string => {
            // @ts-ignore
            if(posts.at(ctx.dataIndex) != null){
              // @ts-ignore
              return Util.getColor("post", postMaxType.at(ctx.dataIndex));
            }
            return DashColors.GREY;
          }
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
            min: 0
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
            display: false,
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
              //@ts-ignore
              title(tooltipItems): string {
                // @ts-ignore
                if (posts[tooltipItems.at(0).dataIndex].at(0) == null){
                  // @ts-ignore
                  return dates[tooltipItems.at(0).dataIndex];
                }
                // @ts-ignore
                let postType : string = postMaxType.at(tooltipItems.at(0).dataIndex);
                postType = postType.at(0)?.toUpperCase() + postType.substring(1);
                // @ts-ignore
                return postType + ": " + postMaxTitle.at(tooltipItems.at(0).dataIndex);

              },
              //@ts-ignore
              label: ((tooltipItem) => {
                if (posts[tooltipItem.dataIndex].at(0) == null) {
                  return profileClicksData[tooltipItem.dataIndex].toFixed();
                } else {
                  // @ts-ignore
                  return ["Profilaufrufe: " + profileClicksData[tooltipItem.dataIndex].toFixed(), "Beitragsaufrufe: " + postMax.at(tooltipItem.dataIndex).toFixed()];
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
          // @ts-ignore
          onClick(posts.at(elements.at(0).index));
        },
      }
    })
  }
}
