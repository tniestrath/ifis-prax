import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {DashColors} from "../../../util/Util";
import {Chart, ChartEvent, LegendElement, LegendItem} from "chart.js/auto";

@Component({
  selector: 'dash-socials-sums',
  templateUrl: './socials-sums.component.html',
  styleUrls: ['./socials-sums.component.css', "../../dash-base/dash-base.component.css"]
})
export class SocialsSumsComponent extends DashBaseComponent implements OnInit{

  ngOnInit(): void {
    this.getData();
  }

  getData(){
    this.api.getSocialsSums().then(res => {
      let labels = ["LinkedIn", "Twitter", "Facebook", "YouTube"];
      let data = [res.linkedin, res.twitter, res.facebook, res.youtube];

      this.createChart(labels, data);
    })
  }

  createChart(labels: string[], data: number[]) {
    if (this.chart){
      this.chart.destroy();
    }

    // @ts-ignore
    this.chart = new Chart(this.element.nativeElement.querySelector("#stat_chart"), {
      type: "bar",
      data: {
        labels: labels,
        datasets: [{
          label: "Platform",
          data: data,
          backgroundColor: [DashColors.LINKEDIN, DashColors.TWITTER, DashColors.FACEBOOK, DashColors.YOUTUBE],
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
            display: false,
            position: "bottom",
            //@ts-ignore
            onClick(e: ChartEvent, legendItem: LegendItem, legend: LegendElement<TType>) {
            }
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
