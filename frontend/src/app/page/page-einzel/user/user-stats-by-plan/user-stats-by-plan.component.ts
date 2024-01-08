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
  protected title : string = "Durchschnittliche Profilaufrufe nach Abomodell und Beitragsbesitz";
  protected datasetLabels : string[] = ["Profile ohne Beiträge", "Alle Profile", "Profile mit Beiträge"]

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

  protected createChart(map : Map<string, number>, map1 : Map<string, number>, map2? : Map<string, number> ) {
    if (this.chart){
      this.chart.destroy();
    }
    let datasets = [];
      datasets = [{
        label: this.datasetLabels[0],
        data: Array.from(map.values()).reverse(),
        backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
      },
        {
          label: this.datasetLabels[1],
          data: Array.from(map1.values()).reverse(),
          backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
        }];
  if (map2){
    datasets.push({
      label: this.datasetLabels[2],
      // @ts-ignore
      data: Array.from(map2.values()).reverse(),
      backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
    });
  }

  // @ts-ignore
    this.chart = new Chart(this.element.nativeElement.querySelector("#stat_chart"), {
      type: "bar",
      data: {
        labels: Array.from(map.keys()).reverse(),
        datasets: datasets
      },
      options: {
        aspectRatio: 4,
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
            display: true
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
            display: true,
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
        }
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
  override title : string = "Durchschnittliche Profilaufrufe und Inhaltsaufrufe nach Abomodell";
  override datasetLabels : string[] = ["Profilaufrufe", "Inhaltsaufrufe"]

  override getData(){
    this.db.getUserClicksAverageByViewType().then(res => {
      let map : Map<string, number> = new Map(Object.entries(res[0]));
      let map1 : Map<string, number> = new Map(Object.entries(res[1]));
      this.createChart(map, map1);
    });
  }
}

