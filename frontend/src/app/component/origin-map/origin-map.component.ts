import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SysVars} from "../../services/sys-vars-service";
import {User} from "../../page/page-einzel/user/user";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {CookieService} from "ngx-cookie-service";
import {PdfService} from "../../services/pdf.service";
import {animate, state, style, transition, trigger} from "@angular/animations";
import _default from "chart.js/dist/plugins/plugin.tooltip";
import numbers = _default.defaults.animations.numbers;
import Util from "../../util/Util";

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

  ngOnInit() {
    this.setToolTip("Dies ist eine Karte, die durch Färbung die Orte angibt, von denen am meisten auf den Marktplatz zugegriffen wird. " +
      "Mit einem Click auf eine Region werden genauere Informationen angezeigt.");
    setTimeout(() => {
      this.isScaled = true;
      const svgElement = this.element.nativeElement.querySelector('#Ebene_1');
      if (svgElement) {
        // @ts-ignore
        if (SysVars.CURRENT_PAGE == "Users") {
          this.db.getOriginMapByUser(Number.parseInt(SysVars.USER_ID)).then(res => {
            this.readData(res, svgElement);
            this.cdr.detectChanges();
          });
        } else {
          this.db.getOriginMapAll().then(res => {
            this.readData(res, svgElement);
            this.cdr.detectChanges();
          })
        }
      }
    }, 100);
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
      this.setRegionTooltip(svgElement, region.identifier, region.cities);
      this.setRegionColor(svgElement, region.identifier, region.clicks, this.totalDE);
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
    var tooltip = document.getElementById('tooltip') ?? new HTMLDivElement();
    var tooltipHeader = document.getElementById('tooltip-header') ?? new HTMLDivElement();
    var tooltipCities = document.getElementById('tooltip-cities') ?? new HTMLDivElement();

    if (pathElement == null){return}

    pathElement.addEventListener('click', () => {
      var pathBoundingBox = pathElement.getBoundingClientRect();
      // @ts-ignore
      var pathCenterX = pathBoundingBox.x + pathBoundingBox.width / 2 + ((visualViewport.width * .5 - pathBoundingBox.x) * .03);
      var pathCenterY = pathBoundingBox.y + pathBoundingBox.height / 2;

      tooltip.style.display = 'block';
      tooltip.style.left = pathCenterX - tooltip.offsetWidth / 2 + 'px';
      tooltip.style.top = pathCenterY - tooltip.offsetHeight / 2 + 'px';
      tooltipHeader.style.paddingBottom = "5px";
      tooltipHeader.innerText = this.getRegionFullName(region);

      tooltipCities.replaceChildren();
      for (const city of cities) {
        let cityElement = document.createElement('div');
        let cityName = document.createElement('div');
        let cityClicks = document.createElement('div');
        cityElement.style.fontSize = "12px";
        cityElement.style.display = "flex";
        cityElement.style.flexDirection = "row";
        cityElement.style.justifyContent = "space-between";
        if (city.name == "gesamt"){
          cityElement.style.paddingTop = "5px";
          cityElement.style.borderTop = "1px dotted #fff";
          cityElement.style.marginTop = "5px";
        }
        cityName.innerText = city.name;
        cityClicks.innerText = Util.formatNumbers(city.clicks);


        cityElement.appendChild(cityName);
        cityElement.appendChild(cityClicks);
        tooltipCities.appendChild(cityElement);
      }

    });
    pathElement.addEventListener('mouseenter', () => {
      pathElement.style.strokeWidth = "10px";
    });

    tooltip.addEventListener('click', () => {
      tooltip.style.display = 'block';
    })

    pathElement.addEventListener('mouseleave', () => {
      pathElement.style.strokeWidth = "2px";
    });
    tooltip.addEventListener('mouseleave', () => {
      tooltip.style.display = 'none';
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
