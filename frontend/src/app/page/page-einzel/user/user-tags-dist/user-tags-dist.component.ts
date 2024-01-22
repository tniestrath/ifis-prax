import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../../../util/Util";

@Component({
  selector: 'dash-user-tags-dist',
  templateUrl: './user-tags-dist.component.html',
  styleUrls: ['./user-tags-dist.component.css', "./../../../../component/dash-base/dash-base.component.css"]
})
export class UserTagsDistComponent extends DashBaseComponent implements OnInit{


  ngOnInit(): void {
    this.setToolTip("", false);
    this.db.getUserTagsDistributionPercentage().then((res : Map<string, number>) => {
      this.createChart([...res.keys()], [...res.values()])
    })
  }

  createChart(labels: string[], data: number[], onClick?: (posts: any[]) => void){
    if (this.chart) {
      this.chart.destroy();
    }

    // @ts-ignore
    this.chart = new Chart(this.element.nativeElement.querySelector("#user-tags-dist-chart"), {
      type: "pie",
      data: {
        labels: labels,
        datasets: [{
          label: "Themen",
          data: data,
          backgroundColor: [DashColors.BLUE, DashColors.DARK_BLUE, DashColors.RED, DashColors.DARK_RED, DashColors.BLACK],
          borderWidth: 0
        }]
      },
      options: {
        maintainAspectRatio: false,
        clip: false,
        layout: {
          padding: {
            top: 5
          }
        },
        scales: {
          y: {
            display: false
          },
          x: {
            display: false
          }
        },
        plugins: {
          datalabels: {
            display: true
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
