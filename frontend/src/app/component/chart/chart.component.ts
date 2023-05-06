import {AfterViewInit, Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';
import {Chart, ChartConfiguration, ChartData, ChartType, ChartTypeRegistry} from 'chart.js/auto';
import _default from "chart.js/dist/plugins/plugin.tooltip";
import {Observable, Subscription} from "rxjs";

export class ChartElements {
  constructor(public label : string[], public data : number[]) {
  }
}

@Component({
  selector: 'dash-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css'],
})
export class ChartComponent implements OnInit{

  toggle : boolean = true;
  displayDetails : string = "none";
  visibility: string = "hidden";

  private sub = new Subscription();

  chart : any;
  canvas_id: string = "chart";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];

  @Input() chartType : ChartType = 'bar';
  @Input() desc : string = "";
  @Input() details : string = "";
  @Input() size : string = "small";
  @Input() elementsObservable = new Observable<ChartElements>;


  @HostBinding('class.big') get isBig() {
    return this.size === "big"
  }
  @HostBinding('class.small') get isSmall() {
    return this.size === "small"
  }
  @HostBinding('class.double-big') get isDoubleBig() {
    return this.size === "double-big"
  }
  @HostBinding('class.double') get isDouble() {
    return this.size === "double"
  }

  constructor() {

  }

  onToggle(){
    if (this.toggle){
      if (this.size == "small"){
        this.size = "big";
      }
      if (this.size == "double") {
        this.size = "double-big"
      }
      this.toggle = !this.toggle;
      this.displayDetails = "flex";
    } else {
      if (this.size == "big"){
        this.size = "small";
      }
      if (this.size == "double-big") {
        this.size = "double"
      }
      this.toggle = !this.toggle;
      this.displayDetails = "none";
    }
  }

  createChart(type: ChartType, labels : string[], data : number[]){
    Chart.defaults.color = "#000"
    if (this.chartType == "bar" || this.chartType == "line" || this.chartType == "bubble"){
      this.chart = new Chart(this.canvas_id, {
        type: type,
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
              display: true,
              text: this.desc,
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
            }
          }
        }
      })
    }
    else {
      this.chart = new Chart(this.canvas_id, {
        type: type,
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
              display: true,
              text: this.desc,
              position: "bottom",
              fullSize: true,
              font: {
                size: 18,
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
  }


  ngOnInit(): void {
    this.sub = this.elementsObservable.subscribe(ce  => {
      if (this.chart){
        this.chart.destroy();
      }
      this.createChart(this.chartType,ce.label, ce.data);
      this.visibility = "visible";
    });
    if (this.desc != ""){
      this.canvas_id = this.desc;
    }
    this.visibility = "hidden";
  }
}
