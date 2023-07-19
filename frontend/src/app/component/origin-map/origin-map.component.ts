import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SysVars} from "../../services/sys-vars-service";
import {User} from "../../page/page-einzel/user/user";
import {DbObject} from "../../services/DbObject";
import {DbService} from "../../services/db.service";
import {CookieService} from "ngx-cookie-service";
import {PdfService} from "../../services/pdf.service";


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


@Component({
  selector: 'dash-origin-map',
  templateUrl: './origin-map.component.html',
  styleUrls: ['./origin-map.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class OriginMapComponent extends DashBaseComponent implements OnInit{
  totalDE: number = 0;
  totalGlobal: number = 0;

  ngOnInit() {
    const svgElement = this.element.nativeElement.querySelector('#Ebene_1');
    if (svgElement) {
      // @ts-ignore
      if (SysVars.CURRENT_PAGE == "Users"){
        this.db.getOriginMapByUser(Number.parseInt(SysVars.USER_ID)).then(res => {
          this.buildMap(res, svgElement);
          this.cdr.detectChanges();
        });
      } else{
        this.db.getOriginMapAll().then(res => {
          this.buildMap(res, svgElement);
          this.cdr.detectChanges();
        })
      }

    }
  }

buildMap(ip_map: { [x: string]: any; hasOwnProperty: (arg0: string) => any; }, svgElement: any){
    var global_gesamt = 0;
    for (const country in ip_map) {
      var country_gesamt = 0;
      if (ip_map.hasOwnProperty(country)) {
        // @ts-ignore
        const regions = ip_map[country];
        for (const region in regions) {
          if (regions.hasOwnProperty(region)) {
            const cities = regions[region];

            var cityArray = [];
            var region_gesamt = 0;
            for (const name in cities) {
              if (cities.hasOwnProperty(name)) {
                const clicks: number = cities[name];
                cityArray.push({name, clicks})
                if (name == "gesamt") {
                  region_gesamt = clicks;
                }
              }
            }
            if (country == "DE") {
              if (region == "gesamt") {
                this.totalDE = region_gesamt;
              }
            } else {
              this.totalGlobal = region_gesamt - this.totalDE;

            }
            if (region != "gesamt") {
              if (country == "BG") {

              } else if (country == "BE") {
                if (region == "BE") {
                  this.setRegionTooltip(svgElement, "BG", cityArray);
                  this.setRegionColor(svgElement, "BG", region_gesamt);
                }
              } else {
                this.setRegionTooltip(svgElement, region, cityArray);
                this.setRegionColor(svgElement, region, region_gesamt);
              }
            }
          }
        }
      }
    }
  }

  setRegionColor(svg : any, region : string, clicks : number){
    var pathElement = svg.querySelector("#" + region) ?? null;
    if (pathElement == null){return}
    pathElement.style =
      "fill:" + this.interpolateColor( "rgb(90, 121, 149)", "rgb(122, 24, 51)", 100, Math.max(Math.min(clicks/this.totalDE, .5), .1)*200) +
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
      var pathCenterX = pathBoundingBox.x + pathBoundingBox.width / 2;
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
        let cttyClicks = document.createElement('div');
        cityElement.style.fontSize = "12px";
        cityElement.style.display = "flex";
        cityElement.style.flexDirection = "row";
        cityElement.style.justifyContent = "space-between";
        if (city.name == "gesamt"){
          cityElement.style.paddingTop = "10px";
        }
        cityName.innerText = city.name;
        if (city.clicks >= 1000000){
          cttyClicks.innerText = String((city.clicks/1000000).toFixed(1) + "M");
        }
        else if (city.clicks >= 1000){
          cttyClicks.innerText = String((city.clicks/1000).toFixed(1) + "K");
        }
        else {
          cttyClicks.innerText = String(city.clicks);
        }


        cityElement.appendChild(cityName);
        cityElement.appendChild(cttyClicks);
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
