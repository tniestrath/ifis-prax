import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import Util, {DashColors} from "../../util/Util";

@Component({
  selector: 'dash-user-plan',
  templateUrl: './user-plan.component.html',
  styleUrls: ['./user-plan.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class UserPlanComponent extends DashBaseComponent implements OnInit{

  colors : string[] = [DashColors.GREY, DashColors.BLUE, DashColors.DARK_BLUE, DashColors.RED, DashColors.DARK_RED, DashColors.BLACK];
  chart_total : number = 0;
  prev_total : number = 0;
  prev_total_text : any;

  labels = ["Ohne Abo", "Basic", "Basic-Plus", "Plus", "Premium", "Sponsor"];

  data = [0,0,0,0,0,0];
  prev_data = [0,0,0,0,0,0];

  ngOnInit(): void {
    if (this.chart != undefined) {
      this.chart.destroy();
    }


    this.db.getUserAccountTypes().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res));
      this.readMap(map, this.data);
      this.chart = this.createChart("user_plan_chart", this.labels, this.data, undefined);
      //this.createLegend("legend-box", this.chart, this.prev_data);
      this.chart_total = this.data.reduce((previousValue, currentValue) => previousValue + currentValue, 0);
      this.cdr.detectChanges();
    }).finally(() => {
      this.db.getUserAccountTypesYesterday().then(res => {
        let map : Map<string, number> = new Map(Object.entries(res));
        this.readMap(map, this.prev_data);
        for (var i = 0; i < this.data.length; i++) {
          this.prev_data[i] = this.data[i] - this.prev_data[i];
        }
        this.prev_total = this.prev_data.reduce((previousValue, currentValue) => previousValue + currentValue, 0);
        this.prev_total_text = this.prev_total >= 0 ? "+" + this.prev_total : this.prev_total;
        //this.createLegend("legend-box", this.chart, this.prev_data);
        this.cdr.detectChanges();
      })
    })

  }

  private readMap(map: Map<string, number>, data: number[]) {
    map.delete("Administrator");
    map.forEach((value, key) => {
      if (key == "Anbieter") {
        this.labels[0] = "Ohne Abo";
        data[0] = (value == 0 || value == undefined ? 0 : value)
      }
      if (key == "Basic") {
        this.labels[1] = key;
        data[1] = (value == 0 || value == undefined ? 0 : value)
      }
      if (key == "Basic-Plus") {
        this.labels[2] = key;
        data[2] = (value == 0 || value == undefined ? 0 : value)
      }
      if (key == "Plus") {
        this.labels[3] = key;
        data[3] = (value == 0 || value == undefined ? 0 : value)
      }
      if (key == "Premium") {
        this.labels[4] = key;
        data[4] = (value == 0 || value == undefined ? 0 : value)
      }
      if (key == "Sponsor") {
        this.labels[5] = key;
        data[5] = (value == 0 || value == undefined ? 0 : value)
      }
    })
  }

  createChart(canvas_id : string, labels : string[], realData : number[], onClick : EventEmitter<number> | undefined){
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
        ctx.arc(x, y, Math.sqrt(chart.chartArea.width * chart.chartArea.height)/ 6, 0, 2 * Math.PI, false);
        ctx.closePath();
        ctx.fill();


        ctx.globalCompositeOperation = 'source-over';

        var totalText = Util.formatNumbers(total);
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

  createLegend(legend_class: string, chart: any, prev_data: number[]){
    const legendBox = document.querySelector("."+legend_class);
    const userPlanItemHead = document.querySelector("#user-plan-item-head");
    legendBox?.replaceChildren();
    // @ts-ignore
    legendBox?.appendChild(userPlanItemHead);

    const legendContainer = document.createElement("DIV");
    legendContainer.setAttribute("id", legend_class + "_legend");

    const ul = document.createElement("UL");
    ul.style.display = "flex";
    ul.style.flexDirection = "column";
    ul.style.margin = "0";
    ul.style.padding = "0";
    ul.style.height = "100%";

    chart.legend.legendItems.forEach((dataset: { text: any; index: any; fillStyle: any}, index: any) => {
      const text = dataset.text;
      const datasetIndex = dataset.index;
      const bgColor = dataset.fillStyle;

      const li = document.createElement("LI");
      li.classList.add("clicks-item-li");
      li.style.display = "flex";
      li.style.alignItems = "center";
      li.style.flexDirection = "row";
      li.style.justifyContent = "space-between";
      li.style.height = "10%";
      li.style.margin = "4px";
      li.style.fontSize = "1.15vw";

      const spanBox = document.createElement("SPAN");
      spanBox.classList.add("clicks-item-span");
      spanBox.style.display = "inline-block";
      spanBox.style.height = "2vh";
      spanBox.style.width = "2vh";
      spanBox.style.marginRight = "5px";
      spanBox.style.borderRadius = "5px";
      spanBox.style.backgroundColor = bgColor;

      const p = document.createElement("P");
      p.classList.add("clicks-item-text");
      p.innerText = text + ":";
      p.style.margin = "0";

      const pWrapper = document.createElement("DIV");
      pWrapper.style.display = "flex";
      pWrapper.style.flexDirection = "row";

      const p2 = document.createElement("P");
      p2.classList.add("clicks-item-text");
      p2.innerText = chart.data.datasets[0].data[datasetIndex];
      p2.style.width = "4vw";
      p2.style.margin = "0";

      const p3 = document.createElement("P");
      p3.classList.add("clicks-item-text");
      p3.innerText = prev_data[datasetIndex] >= 0 ? "+" + prev_data[datasetIndex] : prev_data[datasetIndex].toString();
      p3.style.margin = "0";

      const wrapper = document.createElement("DIV");
      wrapper.style.height = "20px";
      wrapper.style.display = "flex";
      wrapper.style.alignItems = "center";
      wrapper.appendChild(spanBox);
      wrapper.appendChild(p);


      ul.appendChild(li);
      li.appendChild(wrapper);
      li.appendChild(pWrapper)
      pWrapper.appendChild(p2);
      pWrapper.appendChild(p3);
    });

    legendBox?.appendChild(legendContainer);
    legendContainer.appendChild(ul);
  }

  protected readonly DashColors = DashColors;
}
