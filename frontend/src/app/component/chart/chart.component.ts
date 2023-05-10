import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';
import {ActiveElement, Chart, ChartEvent, ChartType} from 'chart.js/auto';
import {Observable, Subscription} from "rxjs";
import {DashBaseComponent} from "../dash-base/dash-base.component";

export class ChartElements {
  constructor(public label: string[], public data: number[], data_context: string[]) {
  }
}

@Component({
  selector: 'dash-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css', "../../component/dash-base/dash-base.component.css"],
})
export class ChartComponent extends DashBaseComponent implements OnInit{

  toggle : boolean = true;
  displayDetails : string = "none";
  visibility: string = "hidden";

  private sub = new Subscription();
  @Output() data_click = new EventEmitter<number>();

  chart : any;
  canvas_id: string = "chart";
  colors : string[] = ["rgb(224, 43, 94, 88)", "rgb(148,28,62)", "rgb(84, 16, 35, 33)", "rgb(0, 0, 0)"];

  @Input() chartType : ChartType = 'bar';
  @Input() desc : string = "";
  @Input() details : string = "";
  @Input() size : string = "small";
  @Input() elementsObservable = new Observable<ChartElements>;

  @Input() x_axis_options : string[] = ["Datum"];
  @Input() y_axis_options : string[] = ["Beitrag", "Clicks", ""];


  constructor() {
    super();
  }

  createChart(type: ChartType, labels : string[], data : number[], onClick : EventEmitter<number>){
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
          },
          interaction: {
            mode: "nearest"
          },
          onClick(event: ChartEvent, elements: ActiveElement[], chart: Chart) {
            onClick.emit(elements[0].index);
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
    this.data_click.subscribe((index) =>{

    });


    this.sub = this.elementsObservable.subscribe(ce  => {
      if (this.chart){
        this.chart.destroy();
      }
      this.createChart(this.chartType,ce.label, ce.data, this.data_click);
      this.visibility = "visible";
    });
    if (this.desc != ""){
      this.canvas_id = this.desc;
    }
    this.visibility = "hidden";
  }

}
