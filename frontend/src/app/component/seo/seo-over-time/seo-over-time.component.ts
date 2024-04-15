import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import Util, {DashColors} from "../../../util/Util";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-seo-over-time',
  templateUrl: './seo-over-time.component.html',
  styleUrls: ['./seo-over-time.component.css', "../../dash-base/dash-base.component.css"]
})
export class SeoOverTimeComponent extends DashBaseComponent implements OnInit{

  private label_desktop : string[] = [];
  private data_desktop : number[] = [];
  private data_mobile : number[] = [];

  ngOnInit(): void {
    this.label_desktop = [];
    this.data_desktop = [];
    this.data_mobile = [];
    this.api.getSeoIndexOverTime("false").then(value => {
      this.label_desktop = value.map((r) => {
        let result = new Date(r.date).toLocaleDateString();
        return result.substring(0, result.lastIndexOf("."))});
      this.data_desktop = value.map(value1 => {return value1.sichtbarkeitsindex});
    }).then(() => {
      this.api.getSeoIndexOverTime("true").then(value2 => {
        this.data_mobile = value2.map(value3 => {return value3.sichtbarkeitsindex});
      }).then(() => {
        // @ts-ignore
        SysVars.SEO_DATA.next({mobile: {now: this.data_mobile.at(this.data_mobile.length-1), last: this.data_mobile.at(this.data_mobile.length-2)}, desktop: {now: this.data_desktop.at(this.data_desktop.length-1), last: this.data_desktop.at(this.data_desktop.length-2)}})
        this.createChart(this.label_desktop, this.data_desktop, this.data_mobile);
      });
    })
  }

  createChart(labelsDesktop : string[], dataDesktop : number[], dataMobile : number[]) {
    if (this.chart) {
      this.chart.destroy();
    }
    this.chart = new Chart("seo-chart", {
      type: "line",
      data: {
        labels: labelsDesktop,
        datasets: [{
          label: "Sichtbarkeitsindex (Desktop)",
          data: dataDesktop,
          backgroundColor: DashColors.RED,
          borderColor: DashColors.RED,
          borderJoinStyle: 'round',
          borderWidth: 5
        },
          {
            label: "Sichtbarkeitsindex (Mobile)",
            data: dataMobile,
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
              size: 15
            },
            callbacks: {
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
