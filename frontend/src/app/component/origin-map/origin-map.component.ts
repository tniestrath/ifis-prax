import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SysVars} from "../../services/sys-vars-service";
import {animate, state, style, transition, trigger} from "@angular/animations";
import Util, {DashColors} from "../../util/Util";
import {Chart} from "chart.js/auto";

export enum Region {
  HH = "Hamburg",
  HB = "Bremen",
  BE = "Berlin",
  MV = "Mecklenburg-Vorpommern",
  BB = "Brandenburg",
  SN = "Sachsen",
  ST = "Sachsen-Anhalt",
  BY = "Bayern",
  SL = "Saarland",
  RP = "Rheinland-Pfalz",
  SH = "Schleswig-Holstein",
  TH = "Thüringen",
  NI = "Niedersachsen",
  HE = "Hessen",
  BW = "Baden-Württemberg",
  NW = "Nordrhein-Westfalen",

  NL = "Niederlande",
  BG = "Belgien",
  CH = "Schweiz",
  AT = "Österreich",
  LU = "Luxemburg"
}

interface SVG_Region {
  identifier : string,
  clicks : number,
  cities : SVG_City[]
}
interface SVG_City {
  name : string,
  clicks : number
}

@Component({
  selector: 'dash-origin-map',
  templateUrl: './origin-map.component.html',
  styleUrls: ['./origin-map.component.css', "../../component/dash-base/dash-base.component.css"],
  animations: [
    trigger('scaleOnLoad', [
      state('initial', style({
        transform: 'scale(0)',
        transformOrigin: '50% 50%'
      })),
      state('scaled', style({
        transform: 'scale(1)',
        transformOrigin: '50% 50%'
      })),
      transition('initial => scaled', animate('1000ms ease-in'))
    ])
  ]
})
export class OriginMapComponent extends DashBaseComponent implements OnInit{
  totalDE: number = 0;
  percentage: number = 0;
  isScaled = false;

  strongest_region: SVG_Region = {identifier: "", cities: [], clicks: 0};
  isRegionSelected: string = "none";
  showCharts: string = "none";
  selectedRegion: string = "";

  startDate : string = "";
  endDate : string = "";

  tooltipElement: HTMLElement = document.createElement("div");
  tooltipCharts: HTMLElement = document.createElement("div");
  tooltipHeader: HTMLElement = document.createElement("div");
  tooltipCities: HTMLElement = document.createElement("div");

  ngOnInit() {
    this.setToolTip("Dies ist eine Karte, die durch Färbung die Orte angibt, von denen am meisten auf den Marktplatz zugegriffen wird. " +
      "Mit einem Click auf eine Region werden genauere Informationen angezeigt.");
    this.isRegionSelected = "none";
    this.showCharts = "none";

    this.tooltipElement = document.getElementById("tooltip") ?? new HTMLElement();
    this.tooltipCharts = document.getElementById("tooltip-charts") ?? new HTMLElement();
    this.tooltipHeader = document.getElementById('tooltip-header') ?? new HTMLElement();
    this.tooltipCities = document.getElementById('tooltip-cities') ?? new HTMLElement();

    let startDatePicker = document.getElementById("geoStartDate") as HTMLInputElement;
    let endDatePicker = document.getElementById("geoEndDate") as HTMLInputElement;
    const svgElement = this.element.nativeElement.querySelector('#Ebene_1');

    startDatePicker.onchange = ev => {
      // @ts-ignore
      this.db.getGeoByDates(ev.target.value, endDatePicker.value).then(res => {
        // @ts-ignore
        this.startDate = ev.target.value;
        // @ts-ignore
        this.readData(res, svgElement, ev.target.value, endDatePicker.value);
        this.cdr.detectChanges();
      });
      };
    endDatePicker.onchange = ev => {
      // @ts-ignore
      this.db.getGeoByDates(startDatePicker.value, ev.target.value).then(res => {
        // @ts-ignore
        this.endDate = ev.target.value;
        // @ts-ignore
        this.readData(res, svgElement, startDatePicker.value, ev.target.value);
        this.cdr.detectChanges();
      });
    };

    this.db.getGeoTimespan().then(res => {
      let startDatePicker = document.getElementById("geoStartDate") as HTMLInputElement;
      let endDatePicker = document.getElementById("geoEndDate") as HTMLInputElement;
      if (res == undefined){
        startDatePicker.disabled = true;
        endDatePicker.disabled = true;
        return;
      } else {
        startDatePicker.disabled = false;
        endDatePicker.disabled = false;
      }
      this.startDate = res[0].split('T')[0];
      this.endDate = res[1].split('T')[0];

      startDatePicker.value = this.startDate;
      endDatePicker.value = this.endDate;

      startDatePicker.min = this.startDate;
      startDatePicker.max = this.endDate;

      endDatePicker.min = this.startDate;
      endDatePicker.max = this.endDate;

      if (this.startDate == this.endDate){
        startDatePicker.disabled = true;
        endDatePicker.disabled = true;
        return;
      }
    });

    setTimeout(() => {
      this.isScaled = true;
      if (svgElement) {
        // @ts-ignore
        if (SysVars.CURRENT_PAGE == "Users") {
          this.db.getOriginMapByUser(Number.parseInt(SysVars.USER_ID)).then(res => {
            this.readData(res, svgElement, startDatePicker.value, endDatePicker.value);
            this.cdr.detectChanges();
          });
        } else {
          this.db.getGeoAll().then(res => {
            this.readData(res, svgElement, startDatePicker.value, endDatePicker.value);
            this.cdr.detectChanges();
          });
        }
      }
    }, 100);
  }

  createChart(data : number[], dates: string[]){
    this.chart?.destroy();

    // @ts-ignore
    this.chart = new Chart("region-by-date", {
      type: "line",
      data: {
        labels: dates,
        datasets: [{
          label: "Aufrufe",
          data: data,
          backgroundColor: DashColors.RED,
          borderColor: DashColors.RED,
          borderJoinStyle: 'round',
          borderWidth: 5
        }]
      },
      options: {
        clip: false,
        aspectRatio: .5,
        scales: {
          y: {
            min: 0
          },
          x: {
            display: true,
            ticks: {
              maxRotation: 0,
              font: {
                size: ctx => {return ctx.chart.width / 25},
              },
              callback: (tickValue, index) => {
                return Util.getDayString(new Date(dates[index]).getDay());
              }
            }
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          title: {
            display: false,
            text: "",
            position: "bottom",
            fullSize: true,
            font: {
              size: 18,
              weight: "bold",
              family: 'Times New Roman'
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
        }
      }
    })
  }

  readData(data : any, svgElement: any, startDate : string, endDate : string){
    let map : Map<string, number> = new Map(Object.entries(data));
    // @ts-ignore
    this.totalDE = map.get("totalDACH");
    // @ts-ignore
    this.percentage = this.totalDE / map.get("total");
    for (const region of map){
      if (String(region.at(0)) == "total" || String(region.at(0)) == "totalDACH") continue;
      this.setRegionColor(svgElement, String(region.at(0)), Number(region.at(1)), this.totalDE);
      this.setRegionTooltip(svgElement, String(region.at(0)), Number(region.at(1)));
      if (Number(region.at(1)) > this.strongest_region.clicks) this.strongest_region = {identifier: String(region.at(0)), clicks: Number(region.at(1)), cities: []};
    }
  }

  setRegionColor(svg : any, region : string, clicks : number, clicks_global : number){
    var pathElement = svg.querySelector("#" + region) ?? null;
    if (pathElement == null){return}
    pathElement.style =
      "fill:" + this.interpolateColor( "rgb(90, 121, 149)", "rgb(122, 24, 51)", clicks_global,  Math.max(clicks*1.5, clicks_global/8)) +
      ";stroke:#FFFFFF;stroke-width:2;stroke-linecap:round;stroke-linejoin:round;"
  }

  setupCities(region: string, region_clicks: number){
    this.db.getGeoByRegionByDates(region, this.startDate, this.endDate).then((res: Map<string,number>) => {
      let data : Map<string, number> = new Map(Object.entries(res));
      this.tooltipHeader.style.paddingBottom = "5px";
      this.tooltipHeader.innerText = this.getRegionFullName(region);

      this.tooltipCities.replaceChildren();
      let cityElement = document.createElement('div', );
      let cityName = document.createElement('div');
      let cityClicks = document.createElement('div');

      var citiesList : any[] = [];

      cityElement.style.marginTop = "3px";
      cityElement.style.paddingTop = "2px";
      cityElement.style.borderTop = "1px dashed #000";
      cityElement.style.fontSize = "calc((1vw + 1vh)/2)";
      cityElement.style.display = "flex";
      cityElement.style.flexDirection = "row";
      cityElement.style.justifyContent = "space-between";
      cityName.innerText = "Gesamt";
      cityClicks.innerText = Util.formatNumbers(region_clicks);

      if (!data.has("error")) {
        let i = 0;
        for (const city of data) {
          if (i > 24) continue;
          let cityElement = document.createElement('div',);
          let cityName = document.createElement('div');
          let cityClicks = document.createElement('div');
          cityElement.style.fontSize = "calc((.9vw + .9vh)/2)";
          cityElement.style.display = "flex";
          cityElement.style.flexDirection = "row";
          cityElement.style.justifyContent = "space-between";
          cityName.innerText = String(city.at(0));
          cityClicks.innerText = Util.formatNumbers(Number(city.at(1)));


          cityElement.appendChild(cityName);
          cityElement.appendChild(cityClicks);
          citiesList.push({element: cityElement, clicks: city.at(1) as number});
          i++;
        }
        this.isRegionSelected = "block";
        if (SysVars.CURRENT_PAGE == "Übersicht") {
          this.showCharts = "block";
          this.tooltipElement.classList.remove("width50");
          this.tooltipCharts.classList.remove("hidden");
          this.setupHistoryChart(region);
        } else {
          this.tooltipElement.classList.add("width50");
          this.tooltipCharts.classList.add("hidden");
        }
      }
      citiesList.sort((a: {element: HTMLElement, clicks: number; }, b: {element: HTMLElement, clicks: number; }) => b.clicks - a.clicks);
      citiesList = citiesList.map((a: { element: HTMLElement, clicks: number }) => {return a.element});
      this.tooltipCities.append(...citiesList);
      cityElement.appendChild(cityName);
      cityElement.appendChild(cityClicks);
      this.tooltipCities.appendChild(cityElement);
    });
  }

  setupHistoryChart(region: string) {
    this.db.getGeoByRegionByDatesListed(region, this.startDate, this.endDate).then(res => {
      this.createChart(res.data, res.dates);
    });
  }

  setRegionTooltip(svg: any, region: string, region_clicks: number){
    var pathElement = svg.querySelector("#" + region) ?? null;
    if (pathElement == null) return;

    let fu = () => {
      this.setupCities(region, region_clicks);
      this.selectedRegion = region;
    };
    if (this.selectedRegion == region){
      fu();
    }
    if (pathElement.eventListeners().length > 0) return;
    let fa = () => {pathElement.style.strokeWidth = "10px";}
    let fum = () => {pathElement.style.strokeWidth = "2px";}

    pathElement.addEventListener('click', fu);
    pathElement.addEventListener('mouseenter', fa);
    pathElement.addEventListener('mouseleave', fum);
  }

  getRegionFullName(shortcode: string): string {
    const enumKeys = Object.keys(Region);
    const enumKey = enumKeys.find(key => key === shortcode);
    return enumKey ? Region[enumKey as keyof typeof Region] : "NONE";
  }

  interpolateColor(color1 : string, color2 : string, steps : number, step : number) {
    // @ts-ignore
    var color1Arr = color1.match(/\d+/g).map(Number);
    // @ts-ignore
    var color2Arr = color2.match(/\d+/g).map(Number);

    var r = Math.round(color1Arr[0] + (color2Arr[0] - color1Arr[0]) * (step / steps));
    var g = Math.round(color1Arr[1] + (color2Arr[1] - color1Arr[1]) * (step / steps));
    var b = Math.round(color1Arr[2] + (color2Arr[2] - color1Arr[2]) * (step / steps));

    return 'rgb(' + r + ',' + g + ',' + b + ')';
  }

  protected readonly Math = Math;


}
