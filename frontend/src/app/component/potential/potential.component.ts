import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";

@Component({
  selector: 'dash-potential',
  templateUrl: './potential.component.html',
  styleUrls: ['./potential.component.css', "../../component/dash-base/dash-base.component.html"]
})
export class PotentialComponent extends DashBaseComponent implements OnInit{
  chart: any;
  bar_chart: any;
  canvas_id : string = "potential-chart";
  bar_canvas_id : string = "bar-chart";
  colors : string[] = ["#5A7995", "rgb(148,28,62)"];

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
          backgroundColor: "#ffffff00",
          //@ts-ignore
          borderWidth: 5,
          borderColor: this.colors[1]
        },
          {
            label: "Durchschnittliches Potential",
            data: data2,
            backgroundColor: "#ffffff00",
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

  createBarChart(labels : string[], data : number[], onClick : EventEmitter<number> | null){
    Chart.defaults.color = "#000"
    if (this.bar_chart){
      this.bar_chart.destroy();
    }

    this.bar_chart = new Chart(this.bar_canvas_id, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [{
          label: "Genutztes Potential",
          data: data,
          backgroundColor: this.colors[1],
          //@ts-ignore
          borderWidth: 0
        },
        {
          label: "Durchschnittliches Potential",
          data: [2,2,2,2],
          backgroundColor: this.colors[0],
          //@ts-ignore
          borderWidth: 0
        }]
      },
      options: {
        layout: {
          padding: {
          }
        },
        scales: {
          x: {
            display: true
          },
          y: {
            display: false
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
    this.createChart(["Artikel","Blogeinträge", "Pressemitteilungen", "Interaktion"], [1,2,3, 4], [2,2,2,2], null);
    this.createBarChart(["Artikel","Blogeinträge", "Pressemitteilungen", "Interaktion"], [1,2,3, 4], null);
  }


}
