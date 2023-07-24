import {Component, EventEmitter, OnDestroy, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";

@Component({
  selector: 'dash-potential',
  templateUrl: './potential.component.html',
  styleUrls: ['./potential.component.css', "../../component/dash-base/dash-base.component.html"]
})
export class PotentialComponent extends DashBaseComponent implements OnInit, OnDestroy{
  canvas_id : string = "potential-chart";
  colors : string[] = ["#5A7995", "#941C3E"];

  createChart(labels : string[], data : number[], data2 : number[], onClick : EventEmitter<number> | null){
    Chart.defaults.color = "#000"
    if (this.chart){
      this.chart.destroy();
    }

    this.chart = new Chart(this.canvas_id, {
      type: "radar",
      data: {
        labels: labels,
        datasets: [{
          label: "Genutztes Potential",
          data: data,
          backgroundColor: this.colors[1] + "88",
          //@ts-ignore
          borderWidth: 5,
          borderColor: this.colors[1]
        },
          {
            label: "Durchschnittliches Potential",
            data: data2,
            backgroundColor: this.colors[0] + "88",
            //@ts-ignore
            borderWidth: 5,
            borderColor: this.colors[0]
          }]
      },
      options: {
        layout: {
          padding: {
          }
        },
        scales: {
          r: {
            angleLines: {
            },
            suggestedMin: 0,
            suggestedMax: 10,
            pointLabels: {
              font: {
                size: 15
              }
            }
          }
        },
        plugins: {
          title: {
            display: true,
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
            display: true
          },
          tooltip: {
            enabled: true
          },
        },
        interaction: {
          mode: "nearest"
        },
        onClick(event: ChartEvent, elements: ActiveElement[], chart: Chart) {
          onClick?.emit(elements[0].index);
        }
      }
    })
  }

  ngOnInit(): void {
    this.createChart(["Artikel","Blogeinträge", "Pressemitteilungen", "Interaktion"], [2,4,5, 8], [5,5,5,5], null);
  }


}
