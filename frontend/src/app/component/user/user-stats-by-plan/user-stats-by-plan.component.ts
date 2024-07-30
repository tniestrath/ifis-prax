import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart, ChartEvent, LegendElement, LegendItem} from "chart.js/auto";
import Util, {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-user-stats-by-plan',
  templateUrl: './user-stats-by-plan.component.html',
  styleUrls: ['./user-stats-by-plan.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserStatsByPlanComponent extends DashBaseComponent implements OnInit{
  protected title : string = "Durchschnittliche Profilaufrufe nach Abomodell und Beitragsbesitz";
  protected datasetLabels : string[] = ["Profile ohne Beiträge", "Alle Profile", "Profile mit Beiträgen"]

  ngOnInit(): void {
    this.setToolTip("", 1,false);
    this.getData();
  }

  protected sorter(a : any, b : any){
    let c = 0;
    if (a.at(0) == "basis"){
      c = -1;
    } else if (a.at(0) == "basis-plus"){
      c = -2;
    } else  if (a.at(0) == "plus"){
      c = -3;
    } else if (a.at(0) == "premium"){
      c = -4;
    } else if (a.at(0) == "sponsor"){
      c = -5;
    }
    let d = 0;
    if (b.at(0) == "basis"){
      d = -1;
    } else if (b.at(0) == "basis-plus"){
      d = -2;
    } else  if (b.at(0) == "plus"){
      d = -3;
    } else if (b.at(0) == "premium"){
      d = -4;
    } else if (b.at(0) == "sponsor"){
      d = -5;
    }
    return d - c;
  }

  protected getData(){
    this.api.getUserProfileViewsAverageByType().then(res => {
      let map : Map<string, number> = new Map(Object.entries<number>(res[0]).sort((a ,b) => this.sorter(a, b)));
      let map1 : Map<string, number> = new Map(Object.entries<number>(res[1]).sort((a,b) => this.sorter(a, b)));
      let map2 : Map<string, number> = new Map(Object.entries<number>(res[2]).sort((a,b) => this.sorter(a, b)));
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
        data: Array.from(map.values()),
        backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
      },
        {
          label: this.datasetLabels[1],
          data: Array.from(map1.values()),
          backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
        }];
  if (map2){
    datasets.push({
      label: this.datasetLabels[2],
      // @ts-ignore
      data: Array.from(map2.values()),
      backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
    });
  }

  // @ts-ignore
    this.chart = new Chart(this.element.nativeElement.querySelector("#stat_chart"), {
      type: "bar",
      data: {
        labels: Array.from(map.keys()),
        datasets: datasets
      },
      options: {
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
            display: true,
            grid: {
              display: false
            }
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
            position: "bottom",
            //@ts-ignore
            onClick(e: ChartEvent, legendItem: LegendItem, legend: LegendElement<TType>) {
            }
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
  selector: 'dash-user-stats-by-plan-type-compare',
  templateUrl: './user-stats-by-plan.component.html',
  styleUrls: ['./user-stats-by-plan.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserStatsByPlanViewTypeCompareComponent extends UserStatsByPlanComponent implements OnInit{
  override title : string = "Durchschnittliche Profilaufrufe und Inhaltsaufrufe nach Abomodell";
  override datasetLabels : string[] = ["Profilaufrufe", "Inhaltsaufrufe"]

  override getData(){
    this.api.getUserClicksAverageByViewType().then(res => {
      let map : Map<string, number> = new Map(Object.entries<number>(res[0]).sort((a ,b) => this.sorter(a, b)));
      let map1 : Map<string, number> = new Map(Object.entries<number>(res[1]).sort((a,b) => this.sorter(a, b)));
      this.createChart(map, map1);
    });
  }
}

@Component({
  selector: 'dash-user-stats-by-plan-shortView',
  templateUrl: './user-stats-by-plan.component.html',
  styleUrls: ['./user-stats-by-plan.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserStatsByPlanShortViewComponent extends UserStatsByPlanComponent implements  OnInit{
  override title : string = "Durchschnittliche Profilaufrufe nach Abomodell";
  override datasetLabels : string[] = ["Profilaufrufe"];


  override ngOnInit(): void {
    this.setToolTip("", 1);
    this.element.nativeElement.querySelector(".comparator-child").classList.remove("comparator-child");
    this.getData();
  }
  override getData(){
    this.api.getUserProfileViewsAverageByType().then(res => {
      let map : Map<string, number> = new Map(Object.entries<number>(res[0]).sort((a ,b) => this.sorter(a, b)));
      let map2 : Map<string, number> = new Map(Object.entries<number>(res[2]).sort((a,b) => this.sorter(a, b)));

      map.delete("plus");
      map.delete("premium");

      map2.delete("basis");
      map2.delete("basis-plus");

      map = new Map<string, number>([...map, ...map2]);

      this.createChart(map);
    });
  }

  override createChart(map : Map<string, number>) {
    if (this.chart){
      this.chart.destroy();
    }
    let datasets = [];
    datasets = [{
      label: this.datasetLabels[0],
      data: Array.from(map.values()),
      backgroundColor: [DashColors.PLAN_BASIC, DashColors.PLAN_BASIC_PLUS, DashColors.PLAN_PLUS, DashColors.PLAN_PREMIUM],
    }];

    this.chart = new Chart(this.element.nativeElement.querySelector("#stat_chart"), {
      type: "bar",
      data: {
        labels: Array.from(map.keys()),
        datasets: datasets
      },
      options: {
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
            display: true,
            grid: {
              display: false
            }
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
            position: "bottom",
            //@ts-ignore
            onClick(e: ChartEvent, legendItem: LegendItem, legend: LegendElement<TType>) {
            }
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
  selector: 'dash-user-stats-by-plan-redirects',
  templateUrl: './user-stats-by-plan.component.html',
  styleUrls: ['./user-stats-by-plan.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserStatsByPlanRedirectsComponent extends UserStatsByPlanShortViewComponent implements  OnInit {
  override title: string = "Durchschnittliche Weiterleitungen nach Abomodell";
  override datasetLabels: string[] = ["Weiterleitungen"];


  override ngOnInit(): void {
    this.setToolTip("Hier ", 1);
    this.element.nativeElement.querySelector(".comparator-child").classList.remove("comparator-child");
    this.getData();
  }

  override getData() {
    this.api.getUserRedirectsByPlan().then(res => {
      let map : Map<string, number> = new Map(Object.entries<number>(res).sort((a ,b) => this.sorter(a, b)));
      this.createChart(map);
    });
  }
}
