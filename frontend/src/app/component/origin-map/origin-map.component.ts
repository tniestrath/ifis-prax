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
  perDayRegionClicks: SVG_Region[][] = [];

  ngOnInit() {
    this.setToolTip("Dies ist eine Karte, die durch Färbung die Orte angibt, von denen am meisten auf den Marktplatz zugegriffen wird. " +
      "Mit einem Click auf eine Region werden genauere Informationen angezeigt.");
    this.isRegionSelected = "none";
    this.showCharts = "none";

    let startDatePicker = document.getElementById("geoStartDate") as HTMLInputElement;
    let endDatePicker = document.getElementById("geoEndDate") as HTMLInputElement;
    const svgElement = this.element.nativeElement.querySelector('#Ebene_1');

    startDatePicker.onchange = ev => {
      // @ts-ignore
      this.db.getGeoByDates(ev.target.value, endDatePicker.value).then(res => {
        this.readData(res, svgElement);
        this.cdr.detectChanges();
      });
      };
    endDatePicker.onchange = ev => {
      // @ts-ignore
      this.db.getGeoByDates(startDatePicker.value, ev.target.value).then(res => {
        this.readData(res, svgElement);
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
      let startDate = res[0].split('T')[0];
      let endDate = res[1].split('T')[0];

      startDatePicker.value = startDate;
      endDatePicker.value = endDate;

      startDatePicker.min = startDate;
      startDatePicker.max = endDate;

      endDatePicker.min = startDate;
      endDatePicker.max = endDate;

      if (startDate == endDate){
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
            this.readData(res, svgElement);
            this.cdr.detectChanges();
          });
        } else {
          this.db.getGeoAll().then(res => {
            this.readData(res, svgElement);
            this.cdr.detectChanges();
          });
        }
      }
    }, 100);
  }

  createChart(perDayRegionClicks : SVG_Region[][], region: string){
    this.chart?.destroy();
    var date = new Date(Date.now());

    var timestamps : string[] = [
      Util.formatDate(date),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true),
      Util.formatDate(new Date(date.setDate(date.getDate() - 1)), true)];
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

    const max = Math.max(...clicksData);


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
  readData(data : any, svgElement: any){
    let map : Map<string, number> = new Map(Object.entries(data));
    // @ts-ignore
    this.totalDE = map.get("total");
    // @ts-ignore
    this.percentage = map.get("totalPercentage");
    for (const region of map){
      if (String(region.at(0)) == "total" || String(region.at(0)) == "totalPercentage") continue;
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

  setRegionTooltip(svg: any, region: string, region_clicks: number){
    var pathElement = svg.querySelector("#" + region) ?? null;
    var tooltipElement = document.getElementById("tooltip") ?? new HTMLElement();
    var tooltipCharts = document.getElementById("tooltip-charts") ?? new HTMLElement();
    var tooltipHeader = document.getElementById('tooltip-header') ?? new HTMLElement();
    var tooltipCities = document.getElementById('tooltip-cities') ?? new HTMLElement();

    if (pathElement == null){return}

    pathElement.addEventListener('click', () => {
      this.db.getGeoByRegion(region).then((res: Map<string,number>) => {
        let data : Map<string, number> = new Map(Object.entries(res));
        tooltipHeader.style.paddingBottom = "5px";
        tooltipHeader.innerText = this.getRegionFullName(region);

        tooltipCities.replaceChildren();
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
          for (const city of data) {
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
          }
            this.isRegionSelected = "block";
            if (SysVars.CURRENT_PAGE == "Overview") {
              this.showCharts = "block";
              tooltipElement.classList.remove("width50");
              tooltipCharts.classList.remove("hidden");
              this.createChart(this.perDayRegionClicks, region);
            } else {
              tooltipElement.classList.add("width50");
              tooltipCharts.classList.add("hidden");
            }
        }
        citiesList.sort((a: {element: HTMLElement, clicks: number; }, b: {element: HTMLElement, clicks: number; }) => b.clicks - a.clicks);
        citiesList = citiesList.map((a: { element: HTMLElement, clicks: number }) => {return a.element});
        tooltipCities.append(...citiesList);
        cityElement.appendChild(cityName);
        cityElement.appendChild(cityClicks);
        tooltipCities.appendChild(cityElement);
        });
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

  protected readonly Math = Math;
}
