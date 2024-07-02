import {
  Component,
  Directive,
  OnInit,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import Util, {DashColors} from "../../../util/Util";
import {UserPlanChip} from "../user";


@Directive({
  selector: '[oaListDirective]'
})
export class OaListDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
@Directive({
  selector: '[basicListDirective]'
})
export class BasicListDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
@Directive({
  selector: '[bpListDirective]'
})
export class BpListDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
@Directive({
  selector: '[plusListDirective]'
})
export class PlusListDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
@Directive({
  selector: '[premiumListDirective]'
})
export class PremiumListDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}

@Component({
  selector: 'dash-user-plan',
  templateUrl: './user-plan.component.html',
  styleUrls: ['./user-plan.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserPlanComponent extends DashBaseComponent implements OnInit{

  colors : string[] = [DashColors.GREY, DashColors.BLUE, DashColors.DARK_BLUE, DashColors.RED, DashColors.DARK_RED, DashColors.BLACK];
  chart_total : number = 0;
  prev_total : number = 0;
  prev_total_text : any;

  labels = ["Ohne Abo", "Basic", "Basic-Plus", "Plus", "Premium"];

  data = [0,0,0,0,0];
  prev_data = [0,0,0,0,0];

  oaList: HTMLParagraphElement[] = [];
  bpList: HTMLParagraphElement[] = [];
  plusList: HTMLParagraphElement[] = [];
  premiumList: HTMLParagraphElement[] = [];
  sponsorList: HTMLParagraphElement[] = [];
  @ViewChild(OaListDirective, {static: true}) oaListDirective!: OaListDirective;
  @ViewChild(BasicListDirective, {static: true}) basicListDirective!: BasicListDirective;
  @ViewChild(BpListDirective, {static: true}) bpListDirective!: BpListDirective;
  @ViewChild(PlusListDirective, {static: true}) plusListDirective!: PlusListDirective;
  @ViewChild(PremiumListDirective, {static: true}) premiumListDirective!: PremiumListDirective;

  ngOnInit(): void {
    if (this.chart != undefined) {
      this.chart.destroy();
    }


    this.api.getUserAccountTypes().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res));
      this.readMap(map, this.data);
      this.chart = this.createChart("user_plan_chart", this.labels, this.data);
      this.chart_total = this.data.reduce((previousValue, currentValue) => previousValue + currentValue, 0);
      this.cdr.detectChanges();
    }).finally(() => {
      this.api.getUserAccountTypesAllNew().then(res => {
        // @ts-ignore
        this.appendToList(this.oaListDirective, res.ohne);
        this.prev_data[0] = res.ohneCount;
        // @ts-ignore
        this.appendToList(this.basicListDirective, res.basis);
        this.prev_data[1] = res.basisCount;
        // @ts-ignore
        this.appendToList(this.bpListDirective, res.basisPlus);
        this.prev_data[2] = res.basisPlusCount;
        // @ts-ignore
        this.appendToList(this.plusListDirective, res.plus);
        this.prev_data[3] = res.plusCount;
        // @ts-ignore
        this.appendToList(this.premiumListDirective, res.premium);
        this.prev_data[4] = res.premiumCount;

        this.prev_total = (res.ohneCount + res.basisCount + res.basisPlusCount + res.plusCount + res.premiumCount)
        this.prev_total_text = this.prev_total >= 0 ? "+" + this.prev_total : this.prev_total;
        this.cdr.detectChanges();
      })
    })

    this.setToolTip("Hier werden die aktuellen Nutzer nach ihren Abonnements, und die in den letzten 7 Tagen angemeldeten Nutzer angezeigt.");
  }


  appendToList(list : any, array: string[]) {
    for (let username of array) {
      const componentRef = list.viewContainerRef.createComponent(UserPlanChip);
      if (username.startsWith("+")){
        componentRef.instance.user = username.substring(1, username.indexOf("<&>"));
        componentRef.instance.plan = username.substring(username.indexOf("<&>"));
        componentRef.location.nativeElement.setAttribute("style", "margin: 0 1px 0 0");
      }
      else if (username.startsWith("-")){
        componentRef.instance.user = username.substring(1, username.indexOf("<&>"));
        componentRef.instance.plan = username.substring(username.indexOf("<&>"));
        componentRef.location.nativeElement.setAttribute("style", "margin: 0 1px 0 0");
      }
    }
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
