import {AfterViewInit, Component, EventEmitter, Input, OnInit} from '@angular/core';
import {ActiveElement, Chart, ChartEvent, ChartType, Plugin} from "chart.js/auto";
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {EmptyObject} from "chart.js/dist/types/basic";

@Component({
  selector: 'dash-podium',
  templateUrl: './podium.component.html',
  styleUrls: ['./podium.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class PodiumComponent extends DashBaseComponent implements OnInit{

  canvas_id: string = "gauge";
  chart: any;

  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];
  cutout: string = "80%";

  createChart(labels : string[], data : number[], onClick : EventEmitter<number> | null){
    Chart.defaults.color = "#000"
    if (this.chart){
      this.chart.destroy();
    }

    const gaugeChartText  = {
      id: "gaugeChartText",
      afterDatasetsDraw(chart: Chart, args: EmptyObject, options: 0, cancelable: false) {
        const { ctx, data, chartArea: {top, bottom, left, right, width, height}, scales: {r} } = chart;
        ctx.save();
        const x = chart.getDatasetMeta(0).data[0].x;
        const y = chart.getDatasetMeta(0).data[0].y;
        const score = data.datasets[0].data[0];
        //@ts-ignore
        const total = data.datasets[0].data.reduce((a,b) => a + b , 0);
        //@ts-ignore
        const angle = Math.PI + (1 / total * score * Math.PI);

        ctx.textAlign = "center";
        ctx.textBaseline = "bottom";
        ctx.font = chart.chartArea.height/2 + "px sans-serif";
        //@ts-ignore
        ctx.fillText(score,x, y+10);
      }
    }

    this.chart = new Chart(this.canvas_id, {
      type: "doughnut",
      data: {
        labels: labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: this.colors,
          //@ts-ignore
          borderWidth: 5,
          circumference: 180,
          rotation: 270,
          borderRadius: 5
        }]
      },
      options: {
        cutout: this.cutout,
        aspectRatio: 1.5,
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
            display: false
          },
          tooltip: {
            enabled: false
          }
        },
        interaction: {
          mode: "nearest"
        },
        onClick(event: ChartEvent, elements: ActiveElement[], chart: Chart) {
          onClick?.emit(elements[0].index);
        }
      },
      // @ts-ignore
      plugins: [gaugeChartText]
    })
  }


  ngOnInit(): void {
    this.createChart(["Score", "Grey"],[650, 350],null);
  }

}
