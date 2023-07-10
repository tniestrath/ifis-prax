import {AfterViewInit, Component, OnInit} from '@angular/core';
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
export class OriginMapComponent extends DashBaseComponent implements OnInit{
  totalDE: number = 0;
  totalGlobal: number = 0;

  ngOnInit() {
    const svgElement = this.element.nativeElement.querySelector('#Ebene_1');
    if (svgElement) {
      const ip_map = {
        "DE": {
          "gesamt": {
            "gesamt": 5210
          },
          "NW": {
            "Oberhausen": 200,
            "Bottrop": 1000,
            "gesamt": 1200
          },
          "BY": {
            "München": 10,
            "gesamt": 1010
          }
        },
        "global": {
          "gesamt": {
            "gesamt": 5300
          }
        }
      }
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
              if (country == "DE"){
                if (region == "gesamt"){
                  this.totalDE = region_gesamt;
                }
              }
              else {
                this.totalGlobal = region_gesamt - this.totalDE;

              }
              if (region != "gesamt"){
                console.log(this.totalDE)
                this.setRegionTooltip(svgElement, region, cityArray);
                this.setRegionColor(svgElement, region, region_gesamt);
              }
            }
          }
        }
      }
      this.cdr.detectChanges();
    }
  }

  setRegionColor(svg : any, region : string, clicks : number){
    var pathElement = svg.querySelector("#" + region);
    console.log("REGION COLOR CALC" + (Math.min(clicks/this.totalDE, .5)*2+.3));
    pathElement.style = "fill:rgba(122, 24, 51, " + (Math.min(clicks/this.totalDE, .5)*2+.3)  + ");stroke:#FFFFFF;stroke-width:2;stroke-linecap:round;stroke-linejoin:round;"
  }

  setRegionTooltip(svg: any, region : string, cities : {name : string, clicks : number}[]){
    var pathElement = svg.querySelector("#" + region);
    var tooltip = document.getElementById('tooltip') ?? new HTMLDivElement();
    var tooltipHeader = document.getElementById('tooltip-header') ?? new HTMLDivElement();
    var tooltipCities = document.getElementById('tooltip-cities') ?? new HTMLDivElement();



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

}
