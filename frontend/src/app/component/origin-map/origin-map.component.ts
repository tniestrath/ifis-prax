import {AfterViewInit, Component, ElementRef, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";


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
  NW = "Nordrhein-Westfalen"
}


@Component({
  selector: 'dash-origin-map',
  templateUrl: './origin-map.component.html',
  styleUrls: ['./origin-map.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class OriginMapComponent extends DashBaseComponent implements AfterViewInit{
  totalDE: number = 0;
  totalGlobal: number = 0;

  getRegionFullName(shortcode: string): string {
    const enumKeys = Object.keys(Region);
    const enumKey = enumKeys.find(key => key === shortcode);
    return enumKey ? Region[enumKey as keyof typeof Region] : "NONE";
  }

  ngAfterViewInit() {
    const svgElement = this.element.nativeElement.querySelector('#Ebene_1');
    if (svgElement) {
      this.totalDE = 100;
      this.totalGlobal = 10;
      this.setRegionColor(svgElement, "HH", "15");
      this.setRegionColor(svgElement, "BE", "15");
      this.setRegionColor(svgElement, "NW", "30");
      this.setRegionColor(svgElement, "BY", "5");
      this.setRegionColor(svgElement, "BW", "35");
      this.setRegionTooltip(svgElement, "BW", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "BB", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "SN", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "ST", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "BY", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "SL", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "RP", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "SH", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "HH", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "TH", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "NB", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "HB", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "HE", {name: "", clicks: ""});
      this.setRegionTooltip(svgElement, "NW", {name: "Oberhausen", clicks: "12341234"}, {name: "Bottrop", clicks: "3000"});
      this.setRegionTooltip(svgElement, "MV", {name: "", clicks: ""});
    }
  }

  setRegionColor(svg : any, region : string, clicks : string){
    var pathElement = svg.querySelector("#" + region);
    console.log(Math.min(Number.parseInt(clicks)/this.totalDE, 1));
    pathElement.style = "fill:rgba(122, 24, 51, " + (Math.min(Number.parseInt(clicks)/this.totalDE, .5)*2+.3)  + ");stroke:#FFFFFF;stroke-width:2;stroke-linecap:round;stroke-linejoin:round;"
  }

  setRegionTooltip(svg: any, region : string, ...cities : {name : string, clicks : string}[]){
    var pathElement = svg.querySelector("#" + region);
    var tooltip = document.getElementById('tooltip') ?? new HTMLDivElement();
    var tooltipHeader = document.getElementById('tooltip-header') ?? new HTMLDivElement();
    var tooltipCities = document.getElementById('tooltip-cities') ?? new HTMLDivElement();



    pathElement.addEventListener('mouseenter', () => {
      var pathBoundingBox = pathElement.getBoundingClientRect();
      var pathCenterX = pathBoundingBox.x + pathBoundingBox.width / 2;
      var pathCenterY = pathBoundingBox.y + pathBoundingBox.height / 2;

      tooltip.style.display = 'block';
      tooltip.style.left = pathCenterX - tooltip.offsetWidth / 2 + 'px';
      tooltip.style.top = pathCenterY - tooltip.offsetHeight / 2 + 'px';
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
        cityName.innerText = city.name;
        if (Number.parseInt(city.clicks) >= 1000000){
          cttyClicks.innerText = String((Number.parseInt(city.clicks)/1000000).toFixed(1) + "M");
        }
        else if (Number.parseInt(city.clicks) >= 1000){
          cttyClicks.innerText = String((Number.parseInt(city.clicks)/1000).toFixed(1) + "K");
        }
        else {
          cttyClicks.innerText = city.clicks;
        }


        cityElement.appendChild(cityName);
        cityElement.appendChild(cttyClicks);
        tooltipCities.appendChild(cityElement);
      }

    });
    tooltip.addEventListener('mouseenter', () => {
      tooltip.style.display = 'block';
    })

    pathElement.addEventListener('mouseleave', () => {
      tooltip.style.display = 'none';
    });
    tooltip.addEventListener('mouseleave', () => {
      tooltip.style.display = 'none';
    })
  }

}
