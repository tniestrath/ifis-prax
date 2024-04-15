import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart, TooltipItem} from "chart.js/auto";
import {SysVars} from "../../services/sys-vars-service";
import {DashColors} from "../../util/Util";
import {NewsletterComponent} from "../newsletter/newsletter/newsletter.component";

@Component({
  selector: 'dash-clicks-by-time',
  templateUrl: './clicks-by-time.component.html',
  styleUrls: ['./clicks-by-time.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksByTimeComponent extends DashBaseComponent implements OnInit{

  colors : string[] = [];
  tooltipString = "Clicks";
  labels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"];
  ngOnInit(): void {
    this.setToolTip("Hier wird angezeigt, zu welcher Zeit wie viele Zugriffe auf den Marktplatz " +
      "stattgefunden haben. Durch hovern der Maus über den Graphen erhalten Sie mehr Informationen.");
    if (SysVars.CURRENT_PAGE == "Users") {
      this.api.getClicksByTime(Number(SysVars.USER_ID)).then(res => {
        this.chart = this.createChart("time_clicks", this.labels, res, undefined);
      });
    } else if (SysVars.CURRENT_PAGE == "Overview") {
      this.api.getClicksByTimeAll().then(res => {
        this.chart = this.createChart("time_clicks", this.labels, res, undefined);
      });
    }
  }
  createChart(canvas_id : string, labels: string[], data: number[], onClick : EventEmitter<number> | undefined){
    // @ts-ignore
    return new Chart(canvas_id, {
      type: "line",
      data: {
        labels : labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: DashColors.RED_50,
          //borderRadius: 5,
          borderWidth: 3,
          borderColor : DashColors.RED,
          borderJoinStyle: "round",
          fill: true,
        }]
      },
      options: {
        clip: false,
        aspectRatio: 1,
        maintainAspectRatio: false,
        scales: {
          x: {
            ticks: {
              maxRotation: 0,
            }
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          title: {
            display: false,
            text: "Aufrufe nach Uhrzeit",
            position: "top",
            fullSize: true,
            font: {
              size: 18,
              weight: "bold",
              family: "Helvetica Neue sans-serif"
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
            },
            callbacks: {
              //@ts-ignore
              title(tooltipItems): string {
                // @ts-ignore
                return labels[tooltipItems.at(0).dataIndex] + " Uhr";
              },
              //@ts-ignore
              label: ((tooltipItem) => {
                // @ts-ignore
                return this.tooltipString +": " + data[tooltipItem.dataIndex].toFixed();
              })
            }
          },
        }
      }
    })
  }
}

@Component({
  selector: 'dash-clicks-by-time-newsletter',
  templateUrl: './clicks-by-time.component.html',
  styleUrls: ['./clicks-by-time.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksByTimeNewsletterComponent extends ClicksByTimeComponent{
  override ngOnInit() {
    this.setToolTip("Hier wird angezeigt, zu welcher Zeit der Newsletter wie oft im Durchschnitt pro Stunde geöffnet wird");
    this.tooltipString = "Interaktionen";
    this.api.getNewslettersOpenTimes().then(res => {
      this.chart = this.createChart("time_clicks", this.labels, res, undefined);
    });
  }

}
