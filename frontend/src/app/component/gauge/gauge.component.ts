import {Component, EventEmitter, OnInit} from '@angular/core';
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {EmptyObject} from "chart.js/dist/types/basic";
import {SysVars} from "../../services/sys-vars-service";
import {Post} from "../post/Post";

@Component({
  selector: 'dash-performance',
  templateUrl: './gauge.component.html',
  styleUrls: ['./gauge.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class GaugeComponent extends DashBaseComponent implements OnInit{

  canvas_id: string = "gauge";

  colors : string[] = ["rgb(149,29,64)", "#5A7995"];
  cutout: string = "60%";

  type : string = "perf";
  postID : string = "10445";
  postName: string = "";


  createChart(labels : string[], data : number[], onClick : EventEmitter<number> | null){
    Chart.defaults.color = "#000"
    if (this.chart){
      this.chart.destroy();
    }

    const canvas  = document.querySelector("#gauge");

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
        // @ts-ignore
        ctx.font = canvas.width/4 + "px sans-serif";

        //@ts-ignore
        ctx.fillText(score.toFixed(), x, y + chart.chartArea.height/8);

        ctx.font = chart.chartArea.height/8 + "px sans-serif";
        ctx.textBaseline = "top";
        ctx.textAlign = "left";
        ctx.fillText("Score", 5, y);
        ctx.textAlign = "right";
        ctx.fillText("100", chart.chartArea.width-5, y);
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
        layout: {
          padding: {
            bottom: -30,
            top: 30
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
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
          },
        },
        interaction: {
          mode: "nearest"
        },
        events: [],
        onHover(event: ChartEvent, elements: ActiveElement[], chart: Chart) {
          return;
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
    this.setToolTip("Ihr Beitrag mit der höchsten berechneten Performance (aufg. Aufrufe der ersten 7 Tage)");

      this.db.getUserBestPost(SysVars.USER_ID, "performance").then(data => {
        let post : Post = data;
        this.createChart(["Score", "Grey"],[(post.performance || 0)*100 , 100-((post.performance || 0)*100)],null);

        if (post.title.length > 30){
          this.postName = post.title.slice(0, 25) + " ...";
        } else {
          this.postName = post.title;
        }

        this.cdr.detectChanges();
      });
  }

}
