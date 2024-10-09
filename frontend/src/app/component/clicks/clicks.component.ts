import {AfterViewInit, Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {SysVars} from "../../services/sys-vars-service";
import Util, {DashColors} from "../../util/Util";

@Component({
  selector: 'dash-clicks',
  templateUrl: './clicks.component.html',
  styleUrls: ['./clicks.component.css', "../dash-base/dash-base.component.css"]
})
export class ClicksComponent extends DashBaseComponent implements OnInit, AfterViewInit{

  colors : string[] = [DashColors.ARTICLE, DashColors.BLOG, DashColors.NEWS, DashColors.PODCAST, DashColors.WHITEPAPER];
  c_chart: any;
  p_chart: any;

  c_chart_total : number = 0;
  p_chart_total : number  = 0;
  isError: boolean = false;

  createChart(canvas_id : string, labels : string[], realData : number[], realData2?: number[], onClick? : EventEmitter<number> | undefined){

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
        ctx.arc(x, y, Math.sqrt(chart.chartArea.width * chart.chartArea.height) / 6, 0, 2 * Math.PI, false);
        ctx.closePath();
        ctx.fill();


        ctx.globalCompositeOperation = 'source-over';

        var totalText = Util.formatNumbers(total);
        ctx.font = (chart.chartArea.height / 6.5) + "px sans-serif";
        ctx.fillStyle = "#fff";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        // @ts-ignore
        ctx.fillText(totalText, x, y+2);
      }
    }
    var datasets = [{
      label: "",
      data: realData,
      backgroundColor: this.colors,
      borderRadius: 5,
      borderWidth: 5
    }];
    if (realData2){
      datasets.push({
          label: "",
          data: realData2,
          backgroundColor: this.colors,
          borderRadius: 5,
          borderWidth: 5
        });
    };

    // @ts-ignore
    return new Chart(canvas_id, {
      type: "doughnut",
      data: {
        labels: labels,
        datasets: datasets
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

      if (Number(chart.data.datasets[0].data[datasetIndex]) > 0){
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
        if (chart.data.datasets[1]){
          p.innerText = text + ": " + Util.formatNumbers(Number(chart.data.datasets[0].data[datasetIndex])) + " (" + Number(chart.data.datasets[1].data[datasetIndex]) + ")";
        } else {
          p.innerText = text + ": " + Util.formatNumbers(Number(chart.data.datasets[0].data[datasetIndex]));
        }
        ul.appendChild(li);
        li.appendChild(spanBox);
        li.appendChild(p);
      }
    });

    const media_ratio = window.matchMedia("(min-aspect-ratio: 5/4)");
    const media_width = window.matchMedia("(min-width: 1500px)");
    media_ratio.addEventListener( "change",(x) => { this.onMedia(x) });
    media_width.addEventListener( "change",(x) => { this.onMedia(x) });

    legendBox?.appendChild(legendContainer);
    legendContainer.appendChild(ul);
    this.onMedia(media_ratio);
  }

  onMedia(x: MediaQueryListEvent | MediaQueryList) {
    let lis = document.querySelectorAll(".clicks-item-li");
    let spans = document.querySelectorAll(".clicks-item-span");
    let ps = document.querySelectorAll(".clicks-item-text");

    if(x.matches){
      for (let i = 0; i < lis.length; i++) {
        lis[i].setAttribute("style", "display: flex; align-items: center; flex-direction: row; height: 30px; margin: 5px;");
      }
      for (let i = 0; i < spans.length; i++) {
        let color = spans[i].getAttribute("style")?.substring(88);
        spans[i].setAttribute("style", "display: inline-block; height: 100%; width: 30px; margin-right: 5px; border-radius: 5px; " + color);
      }
      for (let i = 0; i < ps.length; i++) {
        ps[i].setAttribute("style", "font-size: large");
      }
    }
    else {
      for (let i = 0; i < lis.length; i++) {
        lis[i].setAttribute("style", "display: flex; align-items: center; flex-direction: row; height: 20px; margin: 5px;");
      }
      for (let i = 0; i < spans.length; i++) {
        let color = spans[i].getAttribute("style")?.substring(88);
        spans[i].setAttribute("style", "display: inline-block; height: 100%; width: 20px; margin-right: 5px; border-radius: 5px; " + color);
      }
      for (let i = 0; i < ps.length; i++) {
        ps[i].setAttribute("style", "font-size: medium");
      }
    }
  }

  ngOnInit(): void {
    this.setToolTip("Hier sehen Sie wie die Impressionen ihres Profils und Ihrer Beiträge auf einzelne Bereiche verteilt sind.", 1, SysVars.CURRENT_PAGE != "PRINT");
    if (this.c_chart != undefined) {
      this.c_chart.destroy();
    }
    if (this.p_chart != undefined){
      this.p_chart.destroy();
    }
    this.c_chart_total = 0;
    this.p_chart_total = 0;
    this.api.getUserPostCountByType(SysVars.USER_ID).then((res : {Whitepaper: number, Blogs: number, Artikel: number, News: number, Podcasts: number}) => {
      var realData2 = res;
      this.api.getUserClicks(SysVars.USER_ID).then((res : {viewsBlog : number, viewsArtikel : number, viewsProfile: number, viewsNews: number, viewsPodcast: number, viewsWhitepaper: number} | string) => {
        if (typeof res !== "string"){
          this.isError = false;
          this.c_chart = this.createChart("c_clicks", ["Artikel", "Blogeintrag", "News", "Podcast", "Whitepaper"], [res.viewsArtikel,res.viewsBlog, res.viewsNews, res.viewsPodcast, res.viewsWhitepaper],
                                [realData2.Artikel,realData2.Blogs,realData2.News,realData2.Podcasts,realData2.Whitepaper]);
          this.p_chart = this.createChart("p_clicks", ["Profilaufrufe", "Inhalte"], [res.viewsProfile,(res.viewsBlog + res.viewsArtikel + res.viewsNews + res.viewsPodcast + res.viewsWhitepaper)]);
          this.createLegend("clicks-content-box", this.c_chart);
          this.createLegend("clicks-profile-box", this.p_chart);
          this.c_chart_total = res.viewsArtikel + res.viewsBlog + res.viewsNews + res.viewsPodcast + res.viewsWhitepaper;
          this.p_chart_total = res.viewsProfile + this.c_chart_total;
          this.cdr.detectChanges();
        }
        else {
          this.isError = true;
          this.cdr.detectChanges();
        }

        //this.pdf.exportAsPDF(this.p_chart);
      });
    });

    this.cdr.detectChanges();
  }

  override ngOnDestroy(): void {
    if (this.c_chart != undefined) {
      this.c_chart.destroy();
    }
    if (this.p_chart != undefined){
      this.p_chart.destroy();
    }
    this.c_chart_total = 0;
    this.p_chart_total = 0;
    this.cdr.detectChanges();
  }

  ngAfterViewInit(): void {

  }


  protected readonly Util = Util;
}
