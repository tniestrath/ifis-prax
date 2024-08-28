import {Component} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SysVars} from "../../../services/sys-vars-service";
import {Post} from "../../post/Post";
import {ActiveElement, Chart, ChartEvent, TooltipItem} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-relevance',
  templateUrl: './relevance.component.html',
  styleUrls: ['./relevance.component.css', "../../dash-base/dash-base.component.css"]
})
export class RelevanceComponent extends DashBaseComponent {
  canvas_id: string = "rel";

  colors : string[] = [DashColors.RED, DashColors.BLUE];

  type : string = "rel";
  postName: string = "";

  createChart(value : number){

    const canvas  = document.querySelector("#rel");
    // @ts-ignore
    var ctx : CanvasRenderingContext2D = (canvas as HTMLCanvasElement).getContext("2d");
    let img = new Image();
    img.src = "../../assets/flame_thicc.png";

    // @ts-ignore
    let fillHeight = value * 100;

    const relevanceChartTextAndDecoration  = {
      id: "relevanceChartTextAndDecoration",
      afterDatasetsDraw(chart: Chart, args: EmptyObject, options: 0, cancelable: false) {
        const {ctx, data, chartArea: {top, bottom, left, right, width, height}, scales: {r}} = chart;
        ctx.save();

        ctx.globalCompositeOperation = 'destination-atop';
        // @ts-ignore
        ctx.drawImage(img, 0,0, chart.chartArea.width, chart.chartArea.height);
        ctx.save();

        ctx.globalCompositeOperation = 'source-over';

        ctx.fillStyle = "#000";
        ctx.textAlign = "center";
        ctx.textBaseline = "bottom";
        // @ts-ignore
        ctx.font = canvas.width/6 + "px sans-serif";
        // @ts-ignore
        ctx.fillText(  ((value) * 100).toFixed(), chart.chartArea.width/2 - chart.chartArea.width/12, chart.chartArea.height);
        // @ts-ignore
        ctx.font = canvas.width/12 + "px sans-serif";
        // @ts-ignore
        ctx.fillText(  "/100", chart.chartArea.width/2 + chart.chartArea.width/12, chart.chartArea.height);
      }
    }

    this.chart = new Chart(this.canvas_id, {
      type: "bar",
      data: {
        labels : ["Score"],
        datasets: [{
          label: "",
          data: [fillHeight],
          backgroundColor: "rgb(122, 24, 51)",
          borderRadius: 5,
          borderWidth: 6,
          barThickness: 1000,
          borderColor: "#fff",
          stack: "1"
          },
          {
          label: "",
          // @ts-ignore
          data: [100 - fillHeight],
          backgroundColor: "rgb(90, 121, 149)",
          borderRadius: 5,
          borderWidth: 0,
          barThickness: 1000,
          stack: "1"
        }]
      },
      options: {
        aspectRatio: .8,
        scales : {
          x: {
            stacked : true,
            display: false
          },
          y: {
            stacked : true,
            display: false,
            max : 100,
            min: 0
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
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
            display: false
          },
          tooltip: {
            filter: function (tooltipItem) {
              return tooltipItem.datasetIndex != 1;
            },
            displayColors: false,
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            },
            callbacks: {
              title(tooltipItems) {
                if(tooltipItems.length > 0 && tooltipItems[0].datasetIndex != 1) return "Score";
                else return ;
              }
            }
          },
        },
        interaction: {
          mode: "nearest"
        }
      },
      // @ts-ignore
      plugins: [relevanceChartTextAndDecoration]
    })
  }

  ngOnInit(): void {
    this.setToolTip("Ihr Beitrag mit der höchsten berechneten Relevanz (aufg. Aufrufe der letzten 7 Tage)", 1, SysVars.CURRENT_PAGE != "PRINT");

      this.api.getUserBestPost(SysVars.USER_ID, "relevance").then(data => {
        let post : Post = data;
        this.createChart(post.relevance || 0);

        if (post.title.length > 30){
          this.postName = post.title.slice(0, 25) + " ...";
        } else {
          this.postName = post.title;
        }

        this.cdr.detectChanges();
      });
  }

}
