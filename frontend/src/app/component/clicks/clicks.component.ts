import {Component, EventEmitter, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent, ChartType} from "chart.js/auto";

@Component({
  selector: 'dash-clicks',
  templateUrl: './clicks.component.html',
  styleUrls: ['./clicks.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class ClicksComponent extends DashBaseComponent implements OnInit{

  canvas_id: string = "clicks";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];
  c_chart: any;
  p_chart: any;

  c_chart_total : number = 0;
  p_chart_total : number  = 0;

  createChart(canvas_id : string, labels : string[], data : number[], onClick : EventEmitter<number> | undefined){
    Chart.defaults.color = "#000"
    return new Chart(canvas_id, {
      type: "pie",
      data: {
        labels: labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: this.colors
        }]
      },
      options: {
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
          }
        }
      }
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
      li.style.display = "flex";
      li.style.alignItems = "center";
      li.style.flexDirection = "row";
      li.style.height = "20px";
      li.style.margin = "5px";
      const spanBox = document.createElement("SPAN");
      spanBox.style.display = "inline-block";
      spanBox.style.height = "20px";
      spanBox.style.width = "30px";
      spanBox.style.marginRight = "5px";
      spanBox.style.borderRadius = "5px";
      spanBox.style.backgroundColor = bgColor;

      const p = document.createElement("P");
      p.innerText = text + ": " + chart.data.datasets[0].data[datasetIndex];

      ul.appendChild(li);
      li.appendChild(spanBox);
      li.appendChild(p);
    });

    legendBox?.appendChild(legendContainer);
    console.log(legendBox)
    legendContainer.appendChild(ul);
  }



  ngOnInit(): void {
    if (this.c_chart || this.p_chart) {
      this.c_chart.destroy();
      this.p_chart.destroy();
      this.c_chart_total = 0;
      this.p_chart_total = 0;
    }
    this.c_chart = this.createChart("c_clicks", ["Direkt", "Suche", "Register"], [12,34,56], undefined);
    this.p_chart = this.createChart("p_clicks", ["Direkt", "Suche", "Register", "Artikel"], [1,2,3,4], undefined);
    this.createLegend("clicks-content-box", this.c_chart);
    this.createLegend("clicks-profile-box", this.p_chart);
    this.c_chart.data.datasets[0].data.forEach((item : number) => this.c_chart_total += item);
    this.p_chart.data.datasets[0].data.forEach((item : number) => this.p_chart_total += item);
  }


}
