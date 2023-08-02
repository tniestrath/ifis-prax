import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";

@Component({
  selector: 'dash-tag-pie',
  templateUrl: './tag-pie.component.html',
  styleUrls: ['./tag-pie.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagPieComponent extends DashBaseComponent implements OnInit{
  colors : string[] = ["#5A7995", "rgb(148,28,62)", "rgb(84, 16, 35, 33)"];

  ngOnInit(): void {
    this.createChart([""], [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20])
  }

    createChart(labels : string[], data : number[], ){
      return new Chart("tagpie", {
        type: "pie",
        data: {
          labels: labels,
          datasets: [{
            label: "",
            data: data,
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
        }
      })
    }

}
