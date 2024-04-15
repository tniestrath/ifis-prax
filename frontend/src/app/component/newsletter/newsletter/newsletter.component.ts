import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashColors} from "../../../util/Util";
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Newsletter} from "../Newsletter";
import {SysVars} from "../../../services/sys-vars-service";
import {Chart} from "chart.js/auto";

@Component({
  selector: 'dash-newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.css', "../../dash-base/dash-base.component.css"]
})
export class NewsletterComponent extends DashBaseComponent implements OnInit{
  data : Newsletter = new Newsletter("", "_blank", "", 0, 0, 0, 0, []);
  protected interactionTimeMax = 0;
  protected readonly DashColors = DashColors;

  title : string = "Aktueller Newsletter";
  labels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"];

  ngOnInit(): void {
    this.api.getLatestNewsletter().then(res => {
      this.data = res;
      this.interactionTimeMax = this.data.interactionTimes.indexOf(Math.max(...this.data.interactionTimes, 1));
      this.createChart("newsletter-stats-chart", this.labels, this.data.interactionTimes, undefined);
    });


    SysVars.SELECTED_NEWSLETTER.subscribe( nl => {
      this.data = nl;
      this.title = "Ausgewählter Newsletter";
      this.createChart("newsletter-stats-chart", this.labels, this.data.interactionTimes, undefined);
    })
  }

  createChart(canvas_id : string, labels: string[], data: number[], onClick : EventEmitter<number> | undefined){
    if (this.chart){
      this.chart.destroy();
    }
    // @ts-ignore
    this.chart = new Chart(canvas_id, {
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
        aspectRatio: 2.2,
        maintainAspectRatio: true,
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
                return "Interaktionen: " + data[tooltipItem.dataIndex].toFixed();
              })
            }
          },
        }
      }
    })
  }

  protected readonly Number = Number;
  protected readonly Math = Math;
}
