import {Component, EventEmitter, OnDestroy, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";

@Component({
  selector: 'dash-user-plan',
  templateUrl: './user-plan.component.html',
  styleUrls: ['./user-plan.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class UserPlanComponent extends DashBaseComponent implements OnInit{

  colors : string[] = ["#5A7995", "rgb(148,28,62)", "rgb(84, 16, 35, 33)"];
  chart_total: any;

  ngOnInit(): void {
    if (this.chart != undefined) {
      this.chart.destroy();
    }

    this.chart = this.createChart("user_plan_chart", ["Basic", "Plus", "Premium"],[1173,223,35], undefined);
    this.createLegend("user-plan-content-box", this.chart);
    this.chart_total = 1173 + 223 + 35;

    this.cdr.detectChanges();
  }

  createChart(canvas_id : string, labels : string[], realData : number[], onClick : EventEmitter<number> | undefined){
    Chart.defaults.color = "#000"

    const donughtInner  = {
      id: "donughtInner",
      afterDatasetsDraw(chart: Chart, args: EmptyObject, options: 0, cancelable: false) {
        const {ctx, data, chartArea: {top, bottom, left, right, width, height}, scales: {r}} = chart;
        ctx.save();
        const x = chart.getDatasetMeta(0).data[0].x;
        const y = chart.getDatasetMeta(0).data[0].y;
        // @ts-ignore
        var max = Math.max(...realData);
        // @ts-ignore
        var maxColor: Color = chart.legend?.legendItems?.forEach((value) => {if (value.index == realData.indexOf(max)){
          // @ts-ignore
          ctx.fillStyle = value.fillStyle}
        })
        //@ts-ignore
        const total : number = data.datasets[0].data.reduce((a, b) => a + b, 0);
        ctx.beginPath();
        ctx.arc(x, y, chart.chartArea.width / 6, 0, 2 * Math.PI, false);
        ctx.closePath();
        ctx.fill();


        ctx.globalCompositeOperation = 'source-over';

        var totalText = String(total);
        if (total > 1000){
          totalText = +parseFloat(String(total / 1000)).toFixed( 1 ) + "K";
        }
        if (total > 9999){
          totalText = (total/1000).toFixed() + "K";
        }
        if (total > 1000000){
          totalText = (total/1000000).toFixed(1) + "M";
        }
        if (total > 9999999){
          totalText = (total/10000000).toFixed() + "M";
        }
        ctx.font = (chart.chartArea.height / 6.5) + "px sans-serif";
        ctx.fillStyle = "#fff";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        // @ts-ignore
        ctx.fillText(totalText, x, y);
      }
    }



    // @ts-ignore
    return new Chart(canvas_id, {
      type: "doughnut",
      data: {
        labels: labels,
        datasets: [{
          label: "",
          data: realData,
          backgroundColor: this.colors,
          borderRadius: 5,
          borderWidth: 5
        }]
      },
      options: {
        aspectRatio: 1,
        cutout: "60%",
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
            display: false
          },
          tooltip: {
            displayColors: false,
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            }
          },
        }
      },
      //@ts-ignore
      plugins: [donughtInner]
    })
  }

  createLegend(legend_class : string, chart : any){
    const legendBox = document.querySelector("."+legend_class);

    const legendContainer = document.createElement("DIV");
    legendContainer.setAttribute("id", legend_class + "_legend");

    const ul = document.createElement("UL");
    ul.style.display = "flex";
    ul.style.flexDirection = "column";
    ul.style.margin = "0";
    ul.style.padding = "0";

    chart.legend.legendItems.forEach((dataset: { text: any; index: any; fillStyle: any}, index: any) => {
      const text = dataset.text;
      const datasetIndex = dataset.index;
      const bgColor = dataset.fillStyle;

      const li = document.createElement("LI");
      li.classList.add("clicks-item-li");
      li.style.display = "flex";
      li.style.alignItems = "center";
      li.style.flexDirection = "row";
      li.style.height = "20px";
      li.style.margin = "5px";
      const spanBox = document.createElement("SPAN");
      spanBox.classList.add("clicks-item-span");
      spanBox.style.display = "inline-block";
      spanBox.style.height = "100%";
      spanBox.style.width = "20px";
      spanBox.style.marginRight = "5px";
      spanBox.style.borderRadius = "5px";
      spanBox.style.backgroundColor = bgColor;

      const p = document.createElement("P");
      p.classList.add("clicks-item-text");
      p.innerText = text + ": " + chart.data.datasets[0].data[datasetIndex];

      ul.appendChild(li);
      li.appendChild(spanBox);
      li.appendChild(p);
    });

    legendBox?.appendChild(legendContainer);
    legendContainer.appendChild(ul);
  }

}
