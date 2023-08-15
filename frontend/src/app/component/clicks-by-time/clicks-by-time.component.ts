import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {SysVars} from "../../services/sys-vars-service";

@Component({
  selector: 'dash-clicks-by-time',
  templateUrl: './clicks-by-time.component.html',
  styleUrls: ['./clicks-by-time.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksByTimeComponent extends DashBaseComponent implements OnInit{

  colors : string[] = [];

  labels = ["0/12", "1/13", "2/14", "3/15", "4/16", "5/17", "6/18", "7/19", "8/20", "9/21", "10/22", "11/23"];
  ngOnInit(): void {
    this.setToolTip("Hier wird angezeigt, zu welcher Zeit wie viele Zugriffe auf den Marktplatz" +
      "stattgefunden haben. In Rot die Stunden Nachmittags, in Blau die Vormittags. Durch hovern der Maus über deb Graphen" +
      "erhalten Sie mehr Informationen.");
    if (SysVars.CURRENT_PAGE == "Users") {
      this.db.getClicksByTime(Number(SysVars.USER_ID)).then(res => {
        this.chart = this.createChart2("time_clicks", this.labels, res.slice(0, 11), res.slice(12), undefined);
      });
    } else if (SysVars.CURRENT_PAGE == "Overview") {
      this.db.getClicksByTimeAll().then(res => {
        this.chart = this.createChart2("time_clicks", this.labels, res.slice(0, 11), res.slice(12), undefined);
      });
    }
  }
  createChart2(canvas_id : string, labels: string[], data: number[], data2: number[], onClick : EventEmitter<number> | undefined){
    // @ts-ignore
    return new Chart(canvas_id, {
      type: "line",
      data: {
        labels : labels,
        datasets: [{
          label: "Vormittags",
          data: data,
          backgroundColor: "rgb(90, 121, 149)",
          //borderRadius: 5,
          borderWidth: 2,
          borderColor : "rgb(90, 121, 149)",
          borderJoinStyle: "round",
          tension: 0.2
        },{
          label: "Nachmittags",
          data: data2,
          backgroundColor: "rgb(122, 24, 51)",
          //borderRadius: 5,
          borderWidth: 2,
          borderColor: "rgb(122, 24, 51)",
          borderJoinStyle: "round",
          tension: 0.2
         }]
      },
      options: {
        aspectRatio: 1,
        plugins: {
          datalabels: {
            display: false
          },
          /*datalabels: {
            color: "#ffffff",
            formatter: function(value, context) {
              var valueString = String(value);
              if (value > 1000){
                valueString = +parseFloat(String(value / 1000)).toFixed( 1 ) + "K";
              }
              else if (value > 9999){
                valueString = (value/1000).toFixed() + "K";
              }
              else if (value > 1000000){
                valueString = (value/1000000).toFixed(1) + "M";
              }
              else if (value > 9999999){
                valueString = (value/1000000).toFixed() + "M";
              }
              return valueString;
            },
            rotation: -90
          },*/
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
            display: true
          },
          tooltip: {
            displayColors: false,
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
                if (tooltipItems.at(0).datasetIndex == 0){
                  // @ts-ignore
                  return labels[tooltipItems.at(0).dataIndex].split("/", 1) + " Uhr";
                }
                // @ts-ignore
                else if (tooltipItems.at(0).datasetIndex == 1){
                  // @ts-ignore
                  return labels[tooltipItems.at(0).dataIndex].split("/", 2).at(1)+ " Uhr";
                }

              },
              //@ts-ignore
              label: ((tooltipItem) => {
                if (tooltipItem.datasetIndex == 0){
                  // @ts-ignore
                  return "Clicks: " + data[tooltipItem.dataIndex].toFixed();
                }
                // @ts-ignore
                else if (tooltipItem.datasetIndex == 1){
                  // @ts-ignore
                  return "Clicks: " + data2[tooltipItem.dataIndex].toFixed();
                }

              })
            }
          },
        }
      }
    })
  }
}
