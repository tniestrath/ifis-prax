import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent, TooltipItem} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {SysVars} from "../../../services/sys-vars-service";
import {Tag} from "../../tag/Tag";
import Util from "../../../util/Util";
import {Context} from "chartjs-plugin-datalabels";
import {FilteredSub, SubWithCount} from "../../user/Sub";

@Component({
  selector: 'dash-visitor-subscription-chart',
  templateUrl: './visitor-subscription-chart.component.html',
  styleUrls: ['./visitor-subscription-chart.component.css', "../../dash-base/dash-base.component.css"]
})
export class VisitorSubscriptionChartComponent extends  DashBaseComponent implements OnInit{

  colors : string[] = ["#5A7995", "#354657", "rgb(148,28,62)", "rgb(84, 16, 35)", "#000"];
  labels : any[] = [];
  data : any[] = [];
  details : any[] = [];

  databaseMockup : FilteredSub[] = [
    new FilteredSub("Artikel", 9, [
      new SubWithCount("0", 1, "Artikel"),
      new SubWithCount("0", 2, "Artikel", "IT-Sicherheit"),
      new SubWithCount("0", 1, "Artikel", "IT-Sicherheit", "IFIS"),
      new SubWithCount("0", 5, "Artikel", "Datenschutz")
    ]),
    new FilteredSub("News", 7, [
      new SubWithCount("0", 3, "News"),
      new SubWithCount("0", 1, "News", "IT-Sicherheit", "ESET"),
      new SubWithCount("0", 2, "News", "Blockchain"),
      new SubWithCount("0", 1, "News", "Blockchain", "ESET", "DANGER!"),
      new SubWithCount("0", 2, "News", undefined, "IFIS")
    ]),
    new FilteredSub("Blog", 7, [
      new SubWithCount("0", 1, "Blog"),
      new SubWithCount("0", 1, "Blog", "Penetrationstests", "IFIS", "teekesselchen"),
      new SubWithCount("0", 4, "Blog"),
      new SubWithCount("0", 1, "Blog", undefined, "ESET"),
    ]),
  ];

  ngOnInit(): void {
    this.api.getUsersSubsFiltered("tag").then(value => {
      value.forEach(value1 => {
        this.labels.push(value1.filter);
        this.data.push(value1.total);
        this.details.push(value1.filterDetails);
      });
    });
    this.createChart(this.labels, this.data, this.details);
  }

  createChart(labels : string[], data : number[], details : SubWithCount[]){
    if (this.chart){
      this.chart.destroy();
    }

    this.chart = new Chart("visitors-subs-chart", {
      type: "polarArea",
      data: {
        labels: labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: this.colors,
          borderRadius: 5,
          borderWidth: 5,
        }]
      },
      options: {
        scales: {
          r: {
            min: 0,
            ticks: {
              callback: tickValue => {
                // @ts-ignore
                return Number.parseInt(tickValue).toFixed(0);
              }
            }
          }
        },
        plugins: {
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
            display: true,
            position: "bottom"
          },
          tooltip: {
            displayColors: false,
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            },
            callbacks:{
              beforeBody(tooltipItems: TooltipItem<any>[]): string | string[] | void {
                  // @ts-ignore
                return details.at(tooltipItems.at(0).dataIndex).toString();
              }
            }
          },
        },
        onClick(event, elements, chart: Chart) {
        },
        onHover: (event: ChartEvent, elements: ActiveElement[], chart: Chart) => {
          // @ts-ignore
          if(event.native)
            if(elements.length == 1)
            { // @ts-ignore
              event.native.target.style.cursor = "pointer"
            }
            else {
              // @ts-ignore
              event.native.target.style.cursor = "default"
            }
        }
      }
    })
  }

}
