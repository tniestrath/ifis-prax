import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../../../util/Util";

@Component({
  selector: 'dash-user-clicks-chart',
  templateUrl: './user-clicks-chart.component.html',
  styleUrls: ['./user-clicks-chart.component.css', '../../../../component/dash-base/dash-base.component.css']
})
export class UserClicksChartComponent extends DashBaseComponent implements OnInit{

  dates : string[] = ["01.01.2024", "02.01.2024" , "04.01.2024", "07.01.2024"];
  postNames : string[] = ["Beitrag 1" , "Beitrag 2" , "Beitrag 3"];
  profileData = [  { date : "01.01.2024" , clicks : 10 } , { date : "02.01.2024" , clicks : 15 } , { date : "03.01.2024" , clicks : 17 } , { date : "04.01.2024" , clicks : 20 } , { date : "05.01.2024" , clicks : 18 } , { date : "06.01.2024" , clicks : 25 } , { date : "07.01.2024" , clicks : 30 } , { date : "08.01.2024" , clicks : 32 } , { date : "09.01.2024" , clicks : 33 } , { date : "10.01.2024" , clicks : 15 } , { date : "11.01.2024" , clicks : 17 }
    , { date : "12.01.2024" , clicks : 18 } , { date : "13.01.2024" , clicks : 22 } , { date : "14.01.2024" , clicks : 23 } , { date : "15.01.2024" , clicks : 27 } , { date : "16.01.2024" , clicks : 25 } , { date : "17.01.2024" , clicks : 21 } , { date : "18.01.2024" , clicks : 19 } , { date : "19.01.2024" , clicks : 13 } , { date : "20.01.2024" , clicks : 11 } , { date : "21.01.2024" , clicks : 15 } , { date : "22.01.2024" , clicks : 22 }
    , { date : "12.01.2024" , clicks : 18 } , { date : "13.01.2024" , clicks : 22 } , { date : "14.01.2024" , clicks : 23 } , { date : "15.01.2024" , clicks : 27 } , { date : "16.01.2024" , clicks : 26 } , { date : "17.01.2024" , clicks : 24 } , { date : "18.01.2024" , clicks : 19 } , { date : "19.01.2024" , clicks : 13 } , { date : "20.01.2024" , clicks : 11 } , { date : "21.01.2024" , clicks : 15 } , { date : "22.01.2024" , clicks : 22 }
    , { date : "12.01.2024" , clicks : 18 } , { date : "13.01.2024" , clicks : 22 } , { date : "14.01.2024" , clicks : 23 } , { date : "15.01.2024" , clicks : 29 } , { date : "16.01.2024" , clicks : 25 } , { date : "17.01.2024" , clicks : 23 } , { date : "18.01.2024" , clicks : 19 } , { date : "19.01.2024" , clicks : 13 } , { date : "20.01.2024" , clicks : 11 } , { date : "21.01.2024" , clicks : 15 } , { date : "22.01.2024" , clicks : 22 }
    , { date : "12.01.2024" , clicks : 18 } , { date : "13.01.2024" , clicks : 22 } , { date : "14.01.2024" , clicks : 23 } , { date : "15.01.2024" , clicks : 25 } , { date : "16.01.2024" , clicks : 23 } , { date : "17.01.2024" , clicks : 21 } , { date : "18.01.2024" , clicks : 19 } , { date : "19.01.2024" , clicks : 13 } , { date : "20.01.2024" , clicks : 11 } , { date : "21.01.2024" , clicks : 15 } , { date : "22.01.2024" , clicks : 22 }
    , { date : "12.01.2024" , clicks : 18 } , { date : "13.01.2024" , clicks : 22 } , { date : "14.01.2024" , clicks : 23 } , { date : "15.01.2024" , clicks : 23 } , { date : "16.01.2024" , clicks : 22 } , { date : "17.01.2024" , clicks : 20 } , { date : "18.01.2024" , clicks : 19 } , { date : "19.01.2024" , clicks : 13 } , { date : "20.01.2024" , clicks : 11 } , { date : "21.01.2024" , clicks : 15 } , { date : "22.01.2024" , clicks : 22 }];
  postClicksData : number[] = [5, 7, 3];
  postData  = [{date : "01.01.2024" , name : "Beitrag 1", type : "blog", clicks : 50 }, {date : "02.01.2024", name : "Beitrag 2", type: "news", clicks: 7 } , {date : "04.01.2024", name : "Beitrag 3", type: "article", clicks : 13} ,
                                         {date : "07.01.2024" , name : "Beitrag 4", type : "podcast", clicks : 25 }, {date : "14.01.2024", name : "Beitrag 5", type: "whitepaper", clicks: 17 } , {date : "20.01.2024", name : "Beitrag 6", type: "ratgeber", clicks : 30}];

  dataset : {date: string, postName: string, postClicks: number, profileClicks: number}[] = [];

  ngOnInit(): void {
    let data = this.collectData(this.profileData, this.postData);
    this.createChart(data.dates, data.postNames, data.profileClicks, data.postClicks, data.postTypes, () => {});
  }

  getData($event: Event) {
    let data = this.collectData(this.profileData, this.postData);
    this.createChart(data.dates, data.postNames, data.profileClicks, data.postClicks, data.postTypes, () => {});
  }

  collectData(profileData: {date : string, clicks : number | null}[], postData: {date: string, name: string, type: string, clicks: number | null}[]){
    let dates = [];
    let profileClicks = [];
    let postClicks = [];
    let postNames = [];
    let postTypes = [];

    for (var profile of profileData) {
      dates.push(profile.date);
      if (profile.clicks == null) profileClicks.push(0);
      else profileClicks.push(profile.clicks);
      let postFilter = postData.filter( post => {
        if (post.date == profile.date){
          postClicks.push(post.clicks);
          postNames.push(post.name);
          postTypes.push(post.type);
          return false;
        }
        return true;
      });
      if (postFilter.length >= postData.length) {
        postClicks.push(null);
        postNames.push(null);
        postTypes.push(null);
      }
      postData = postFilter;
    }

    return { dates, profileClicks, postClicks, postNames, postTypes };
  }


  createChart(dates: string[], postNames : any[], profileClicksData: any[], postClicksData: any[], postTypes : any[], onClick: (index : number) => void){
    if (this.chart) {
      this.chart.destroy();
    }

    var postClicksMax = Math.max(...postClicksData);
    console.log(postClicksData)

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
            if(postClicksData.at(ctx.dataIndex) != null){
              return Math.max(12 * (postClicksData.at(ctx.dataIndex) / postClicksMax), 5);
            }
            return 2.5;
          },
          pointBackgroundColor: (ctx, options): string => {
            if(postTypes.at(ctx.dataIndex) != null){
              return Util.getColor("post", postTypes.at(ctx.dataIndex));
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
            bottom: -50
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
                if (postNames[tooltipItems.at(0).dataIndex] == null){
                  // @ts-ignore
                  return dates[tooltipItems.at(0).dataIndex];
                }
                // @ts-ignore
                let postType : string = postTypes[tooltipItems.at(0).dataIndex];
                postType = postType.at(0)?.toUpperCase() + postType.substring(1);
                // @ts-ignore
                return postType + ": " + postNames[tooltipItems.at(0).dataIndex];

              },
              //@ts-ignore
              label: ((tooltipItem) => {
                console.log(tooltipItem.dataIndex + " : " + postNames[tooltipItem.dataIndex] + " : " + postClicksData[tooltipItem.dataIndex] + " : " + postTypes[tooltipItem.dataIndex]);
                if (postNames[tooltipItem.dataIndex] == null) {
                  return profileClicksData[tooltipItem.dataIndex].toFixed();
                } else {
                  return ["Profilaufrufe: " + profileClicksData[tooltipItem.dataIndex].toFixed(), "Beitragsaufrufe: " + postClicksData[tooltipItem.dataIndex].toFixed()];
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
      }
    })
  }
}
