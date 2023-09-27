import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SysVars} from "../../services/sys-vars-service";
import {animate, state, style, transition, trigger} from "@angular/animations";
import Util, {DashColors} from "../../util/Util";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {tick} from "@angular/core/testing";

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
  NB = "Niedersachsen",
  HE = "Hessen",
  BW = "Baden-Württemberg",
  NW = "Nordrhein-Westfalen",

  NL = "Niederlande",
  BG = "Belgien",
  SW = "Schweiz",
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
  totalGlobal: number = 0;
  isScaled = false;

  strongest_region: SVG_Region = {identifier: "", cities: [], clicks: 0};
  isRegionSelected: string = "none";
  showCharts: string = "none";
  perDayRegionClicks: SVG_Region[][] = [];

  ngOnInit() {
    this.setToolTip("Dies ist eine Karte, die durch Färbung die Orte angibt, von denen am meisten auf den Marktplatz zugegriffen wird. " +
      "Mit einem Click auf eine Region werden genauere Informationen angezeigt.");
    this.isRegionSelected = "none";
    this.showCharts = "none";
    setTimeout(() => {
      this.isScaled = true;
      const svgElement = this.element.nativeElement.querySelector('#Ebene_1');
      if (svgElement) {
        // @ts-ignore
        if (SysVars.CURRENT_PAGE == "Users") {
          this.db.getOriginMapByUser(Number.parseInt(SysVars.USER_ID)).then(res => {
            this.readData(res, svgElement);
            this.setRegionTooltip(svgElement, this.strongest_region.identifier, this.strongest_region.cities);
            this.cdr.detectChanges();
          });
        } else {
          this.db.getOriginMapAll().then(res => {
            this.readData(res, svgElement);
            this.setRegionTooltip(svgElement, this.strongest_region.identifier, this.strongest_region.cities);
            this.cdr.detectChanges();
          })
          this.db.getViewsByLocationLas14().then(res => {
            this.readOldData(res);
          })
        }
      }
    }, 100);
  }

  createChart(perDayRegionClicks : SVG_Region[][], region: string){
    this.chart?.destroy();
    var date = new Date(Date.now());

    var timestamps : string[] = [
      Util.formatDate(new Date(date.setDate(date.getDate()))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1))),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)))];
    timestamps.reverse();
    var clicksData : number[] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0];
    for (var regionClicks of perDayRegionClicks) {
      for (var clicks of regionClicks){
        if (clicks.identifier == region){
          let index = perDayRegionClicks.indexOf(regionClicks);
          clicksData[index] = clicks.clicks;
        }
      }
    }

    const max = Math.max.apply(null, clicksData);


    // @ts-ignore
    this.chart = new Chart("region-by-date", {
      type: "line",
      data: {
        labels: timestamps,
        datasets: [{
          label: "Aufrufe",
          data: clicksData,
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
            min: 0,
            max: max
          },
          x: {
            display: true,
            ticks: {
              maxRotation: 0,
              font: {
                size: ctx => {return ctx.chart.width / 25},
              },
              callback: (tickValue, index) => {
                return Util.getDayString(Util.readFormattedDate(timestamps[index]).getDay());
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

  readOldData(globals: { [x: string]: any}[]){
    for (let i = 0; i < globals.length; i++) {
      const region_clicks: SVG_Region[] = [];
      for (const country in globals[i]){
        if (country == "DE"){
          for (const region in globals[i]["DE"]){
            let clicks = globals[i][country][region]["gesamt"];
            let cityArray: SVG_City[] = [];
            for (const city in globals[i][country][region]) {
              if (city != "gesamt") {
                cityArray.push({clicks: globals[i][country][region][city], name: city});
              }
            }
            cityArray.sort((a, b) =>  b.clicks - a.clicks);
            cityArray.push({clicks: clicks, name: "gesamt"});
            if (Number.isNaN(clicks)) clicks = 0;
            region_clicks.push({identifier: region, clicks: clicks, cities: cityArray})
          }
        }
        else {
          let clicks = 0;
          if (globals[i][country]["gesamt"] != undefined) {
            clicks = globals[i][country]["gesamt"].gesamt;
          }
          let cityArray: SVG_City[] = [];
          for (const city in globals[i][country][country]) {
            if (city != "gesamt") {
              cityArray.push({clicks: globals[i][country][country][city], name: city});
            }
          }
          cityArray.sort((a, b) =>  b.clicks - a.clicks);
          cityArray.push({clicks: clicks, name: "gesamt"});
          if (Number.isNaN(clicks)) clicks = 0;
          if (country == "BE") region_clicks.push({identifier: "BG", clicks: clicks, cities: cityArray});
          else region_clicks.push({identifier: country, clicks: clicks, cities: cityArray});
        }
      }
      this.perDayRegionClicks.push(region_clicks);
    }
    this.perDayRegionClicks = this.perDayRegionClicks.reverse();
  }

  readData(global: { [x: string]: any}, svgElement: any){
    this.totalDE = global["DE"]["gesamt"]["gesamt"];
    this.totalGlobal = global["global"]["gesamt"]["gesamt"];
    const region_clicks: SVG_Region[] = [];

    for (const country in global){
      if (country == "DE"){
          for (const region in global["DE"]){
            let clicks = global[country][region]["gesamt"];
            let cityArray: SVG_City[] = [];
            for (const city in global[country][region]) {
              if (city != "gesamt") {
                cityArray.push({clicks: global[country][region][city], name: city});
              }
            }
            cityArray.sort((a, b) =>  b.clicks - a.clicks);
            cityArray.push({clicks: clicks, name: "gesamt"});
            region_clicks.push({identifier: region, clicks: clicks, cities: cityArray})
          }
      }
      else {
        let clicks = global[country]["gesamt"]["gesamt"];
        let cityArray: SVG_City[] = [];
        for (const city in global[country][country]) {
          if (city != "gesamt") {
            cityArray.push({clicks: global[country][country][city], name: city});
          }
        }
        cityArray.sort((a, b) =>  b.clicks - a.clicks);
        cityArray.push({clicks: clicks, name: "gesamt"});
        if (country == "BE") region_clicks.push({identifier: "BG", clicks: clicks, cities: cityArray});
        else region_clicks.push({identifier: country, clicks: clicks, cities: cityArray});
      }
    }
    for (const region of region_clicks){
      this.setRegionColor(svgElement, region.identifier, region.clicks, this.totalDE);
      this.setRegionTooltip(svgElement, region.identifier, region.cities);
      if (region.clicks > this.strongest_region.clicks) this.strongest_region = region;
    }
  }

  setRegionColor(svg : any, region : string, clicks : number, clicks_global : number){
    var pathElement = svg.querySelector("#" + region) ?? null;
    if (pathElement == null){return}
    pathElement.style =
      "fill:" + this.interpolateColor( "rgb(90, 121, 149)", "rgb(122, 24, 51)", clicks_global,  Math.max(clicks*1.5, clicks_global/8)) +
      ";stroke:#FFFFFF;stroke-width:2;stroke-linecap:round;stroke-linejoin:round;"
  }

  setRegionTooltip(svg: any, region : string, cities : {name : string, clicks : number}[]){
    var pathElement = svg.querySelector("#" + region) ?? null;
    var tooltipElement = document.getElementById("tooltip") ?? new HTMLElement();
    var tooltipCharts = document.getElementById("tooltip-charts") ?? new HTMLElement();
    var tooltipHeader = document.getElementById('tooltip-header') ?? new HTMLElement();
    var tooltipCities = document.getElementById('tooltip-cities') ?? new HTMLElement();

    if (pathElement == null){return}

    pathElement.addEventListener('click', () => {
      tooltipHeader.style.paddingBottom = "5px";
      tooltipHeader.innerText = this.getRegionFullName(region);

      tooltipCities.replaceChildren();
      if (cities.length > 20){
        let gesamt = cities[cities.length-1];
        cities = cities.slice(0, 20);
        cities.push(gesamt);
      }
      for (const city of cities) {
        let cityElement = document.createElement('div', );
        let cityName = document.createElement('div');
        let cityClicks = document.createElement('div');
        cityElement.style.fontSize = "calc((.9vw + .9vh)/2)";
        cityElement.style.display = "flex";
        cityElement.style.flexDirection = "row";
        cityElement.style.justifyContent = "space-between";
        if (city.name == "gesamt"){
          cityElement.style.marginTop = "3px";
          cityElement.style.paddingTop = "2px";
          cityElement.style.borderTop = "1px dashed #000";
        }
        cityName.innerText = city.name;
        cityClicks.innerText = Util.formatNumbers(city.clicks);


        cityElement.appendChild(cityName);
        cityElement.appendChild(cityClicks);
        tooltipCities.appendChild(cityElement);

        this.isRegionSelected = "block";
        if (SysVars.CURRENT_PAGE == "Overview") {
          this.showCharts = "block";
          tooltipElement.classList.remove("width50");
          tooltipCharts.classList.remove("hidden");
          this.createChart(this.perDayRegionClicks, region);
        }
        else {
          tooltipElement.classList.add("width50");
          tooltipCharts.classList.add("hidden");
        }
      }
    });
    pathElement.addEventListener('mouseenter', () => {
      pathElement.style.strokeWidth = "10px";
    });
    pathElement.addEventListener('mouseleave', () => {
      pathElement.style.strokeWidth = "2px";
    });
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

}
