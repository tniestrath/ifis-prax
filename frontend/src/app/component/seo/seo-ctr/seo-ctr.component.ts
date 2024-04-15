import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart, TooltipItem} from "chart.js/auto";
import Util, {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-seo-ctr',
  templateUrl: './seo-ctr.component.html',
  styleUrls: ['./seo-ctr.component.css', "../../dash-base/dash-base.component.css"]
})
export class SeoCtrComponent extends DashBaseComponent implements OnInit{

  labels : string[] = [];
  data_imp : number[] = [];
  data_clicks : number[] = [];
  data_ctr : number[] = [];

  ngOnInit(): void {
    this.api.getSeoImpCtrNow().then(res => {
      this.labels = res.map((r) => {
        let result = new Date(r.keys[0]).toLocaleDateString();
        return result.substring(0, result.lastIndexOf("."))});
      this.data_clicks = res.map(r => r.clicks);
      this.data_imp = res.map(r => r.impressions);
      this.data_ctr = res.map(r => r.ctr);
      this.createChart(this.labels, this.data_clicks, this.data_imp, this.data_ctr);
    })
  }


  createChart(labels : string[], dataClicks : number[], dataImp : number[], dataCtr : number[]) {
    if (this.chart) {
      this.chart.destroy();
    }
    this.chart = new Chart("seo-ctr-chart", {
      type: "line",
      data: {
        labels: labels,
        datasets: [{
          label: "Impressionen",
          data: dataImp,
          backgroundColor: DashColors.RED,
          borderColor: DashColors.RED,
          borderJoinStyle: 'round',
          borderWidth: 5
        },
          {
            label: "Clicks",
            data: dataClicks,
            backgroundColor: DashColors.BLUE,
            borderColor: DashColors.BLUE,
            borderJoinStyle: 'round',
            borderWidth: 5
          }]
      },
      options: {
        clip: false,
        aspectRatio: 2.8,
        maintainAspectRatio: false,
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
              size: 12
            },
            footerFont: {
              size: 15
            },
            callbacks: {
              footer(tooltipItem): string | string[] | void {
                // @ts-ignore
                return "CTR: " + Number(dataCtr[tooltipItem.at(0).dataIndex]).toFixed(4);
              }
            }
          }
        },
        interaction: {
          mode: "x",
          intersect: true
        },
        onClick: undefined
      }
    })
  }
}
