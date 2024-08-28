import {Component, EventEmitter, OnInit} from '@angular/core';
import Util, {DashColors} from "../../../util/Util";
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {DbObject} from "../../../services/DbObject";
import {SysVars} from "../../../services/sys-vars-service";
import {Tag} from "../../tag/Tag";


export class PostType extends DbObject{

  link : string;
  count : number;
  constructor(name : string, link : string , count : number) {
    super(name, name);
    this.link = link;
    this.count = count;
  }
}
@Component({
  selector: 'dash-post-type',
  templateUrl: './post-type.component.html',
  styleUrls: ['./post-type.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostTypeComponent extends DashBaseComponent implements OnInit{

  protected readonly DashColors = DashColors;

  colors : string[] = [DashColors.NEWS, DashColors.BLOG, DashColors.ARTICLE, DashColors.WHITEPAPER, DashColors.PODCAST];
  chart_total : number = 0;
  prev_total : number = 0;
  prev_total_text : any;

  labels = ["News", "Blogs", "Artikel", "Whitepaper", "Podcasts"];
  links = ["","","","",""]

  data = [0,0,0,0,0];
  prev_data = [0,0,0,0,0];


  ngOnInit(): void {
    if (this.chart != undefined) {
      this.chart.destroy();
    }

    this.api.getPostsPerType().then(res => {
      this.readData(res, this.data);
      this.chart = this.createChart("post_type_chart", this.labels, this.data, undefined);
      this.chart_total = this.data.reduce((previousValue, currentValue) => previousValue + currentValue, 0);
      this.cdr.detectChanges();
    }).finally(()=> {
      this.api.getPostsPerTypeYesterday().then(res => {
        this.readData(res, this.prev_data);
        for (var i = 0; i < this.data.length; i++) {
          this.prev_data[i] = this.data[i] - this.prev_data[i];
        }
        this.prev_total = this.prev_data.reduce((previousValue, currentValue) => previousValue + currentValue, 0);
        this.prev_total_text = this.prev_total >= 0 ? "+" + this.prev_total : this.prev_total;
        this.cdr.detectChanges();
      })
    })
  this.setToolTip("Hier werden die Menge an Posts nach ihrem Typen aufgeschlüsselt.");
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
        },
        onClick: (event: any, elements, chart: Chart) =>  {
          // @ts-ignore
          window.open(this.links[elements.at(0).index], "_blank");
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
      },
      //@ts-ignore
      plugins: [donughtInner]
    })
  }

  private readData(postTypes: PostType[], data : number[]) {
    postTypes.forEach((type) => {
      if (type.name == "News") {
        data[0] = (type.count == 0 || type.count == undefined ? 0 : type.count);
        this.links[0] = type.link;
      }
      if (type.name == "Blog") {
        data[1] = (type.count == 0 || type.count == undefined ? 0 : type.count);
        this.links[1] = type.link;
      }
      if (type.name == "Artikel") {
        data[2] = (type.count == 0 || type.count == undefined ? 0 : type.count);
        this.links[2] = type.link;
      }
      if (type.name == "Whitepaper") {
        data[3] = (type.count == 0 || type.count == undefined ? 0 : type.count);
        this.links[3] = type.link;
      }
      if (type.name == "Podcasts") {
        data[4] = (type.count == 0 || type.count == undefined ? 0 : type.count);
        this.links[4] = type.link;
      }
    })
  }
  protected readonly window = window;
}
