import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
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

  oaList: HTMLParagraphElement[] = [];
  bpList: HTMLParagraphElement[] = [];
  plusList: HTMLParagraphElement[] = [];
  premiumList: HTMLParagraphElement[] = [];
  sponsorList: HTMLParagraphElement[] = [];

  ngOnInit(): void {
    if (this.chart != undefined) {
      this.chart.destroy();
    }


    this.db.getUserAccountTypes().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res));
      this.readMap(map, this.data);
      this.chart = this.createChart("user_plan_chart", this.labels, this.data);
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
        this.cdr.detectChanges();
      }).finally(() => {
        this.db.getUserAccountTypesAllNew().then(res => {
          // @ts-ignore
          document.getElementById("oaList").append(...this.formatArray(res.ohne));
          // @ts-ignore
          document.getElementById("basicList").append(...this.formatArray(res.basis));
          // @ts-ignore
          document.getElementById("bpList").append(...this.formatArray(res["basis-plus"]));
          // @ts-ignore
          document.getElementById("plusList").append(...this.formatArray(res.plus));
          // @ts-ignore
          document.getElementById("premiumList").append(...this.formatArray(res.premium));
        })
      })
    })

    this.setToolTip("Hier werden die aktuellen Nutzer nach ihren Abonnements, und die in den letzten 24 Stunden angemeldete Nutzer (hinter dem +) angezeigt. +" +
      "\n Bei Hover über die angemeldeten Nutzer werden diese angezeigt. Grün für Neuanmeldung, Blau für einen Planwechsel und Rot für jemanden der sein Abonnement gekündigt hat.");
  }


  formatArray(array: string[]) : HTMLParagraphElement[]{
    var result: HTMLParagraphElement[] = [];
    for (let username of array) {
      if (username.startsWith("+")){
        let p = document.createElement("p");
        p.style.color = DashColors.GREEN;
        p.style.margin = String(0);
        p.innerText = username.slice(1);
        result.push(p);
      }
      else if (username.startsWith("-")){
        let p1 = document.createElement("p");
        p1.style.color = DashColors.RED;
        p1.style.margin = String(0);
        p1.innerText = username.slice(1);
        result.push(p1);
      }
      else if (username.startsWith("&")){
        let p2 = document.createElement("p");
        p2.style.color = DashColors.BLUE;
        p2.style.margin = String(0);
        p2.innerText = username.slice(1);
        result.push(p2);
      }
    }
    return result;
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

  createChart(canvas_id : string, labels : string[], realData : number[]){
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
    };
    const shadowPlugin = {
      beforeDraw: (chart: Chart, args : EmptyObject, options: 0) => {
        const { ctx } = chart;
        ctx.shadowColor = "rgba(0, 0, 0, 0.2)";
        ctx.shadowBlur = 5;
        ctx.shadowOffsetX = 5;
        ctx.shadowOffsetY = 5;
      },
    };



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
      plugins: [donughtInner],
    });
  }

  protected readonly DashColors = DashColors;
}
