import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {DashColors} from "../../../../util/Util";

@Component({
  selector: 'dash-user-stats-by-plan',
  templateUrl: './user-stats-by-plan.component.html',
  styleUrls: ['./user-stats-by-plan.component.css', "../../../../component/dash-base/dash-base.component.css"]
})
export class UserStatsByPlanComponent extends DashBaseComponent implements OnInit{

  ngOnInit(): void {
    this.setToolTip("", false);
    this.getData();
  }

  protected getData(){
    this.db.getUserProfileViewsAverageByType().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res[0]));
      let map1 : Map<string, number> = new Map(Object.entries(res[1]));
      let map2 : Map<string, number> = new Map(Object.entries(res[2]));
      this.createChart(map, map1, map2);
    });
  }

  protected createChart(map : Map<string, number>, map1 : Map<string, number>, map2 : Map<string, number>) {
    if (this.chart){
      this.chart.destroy();
    }

    // @ts-ignore
    this.chart = new Chart("stat_chart", {
      type: "bar",
      data: {
        labels: Array.from(map.keys()),
        datasets: [{
          label: "",
          data: Array.from(map.values()),
          backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
        },
        {
          label: "",
          data: Array.from(map1.values()),
          backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
        },
        {
          label: "",
          data: Array.from(map2.values()),
          backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
        }]
      },
      options: {
        aspectRatio: 2.8,
        maintainAspectRatio: false,
        clip: false,
        layout: {
          padding: {
            bottom: 0
          }
        },
        scales: {
          y: {
            display: false
          },
          x: {
            display: false
          }
        },
        plugins: {
          datalabels: {
            display: true
          },
          title: {
            display: false,
            text: "",
            position: "top",
            fullSize: true,
            font: {
              size: 18,
              weight: "bold",
              family: "'Helvetica Neue', sans-serif"
            }
          },
          legend: {
            display: false,
            position: "bottom"
          },
          tooltip: {
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            },
            callbacks: {
            }
          }
        },
        interaction: {
          mode: "nearest",
          intersect: true
        },
        onClick(event: ChartEvent, elements: ActiveElement[]) {
        },
      }
    })
  }
}

@Component({
  selector: 'dash-user-stats-by-plan',
  templateUrl: './user-stats-by-plan.component.html',
  styleUrls: ['./user-stats-by-plan.component.css', "../../../../component/dash-base/dash-base.component.css"]
})
export class UserStatsByPlanViewTypeCompareComponent extends UserStatsByPlanComponent implements OnInit{

  override getData(){
    this.db.getUserProfileViewsAverageByType().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res[0]));
      let map1 : Map<string, number> = new Map(Object.entries(res[1]));
      let map2 : Map<string, number> = new Map(Object.entries(res[2]));
      this.createChart(map, map1, map2);
    });
  }
}

