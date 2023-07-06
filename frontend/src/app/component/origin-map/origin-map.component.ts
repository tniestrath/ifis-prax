import {AfterViewInit, Component, ElementRef, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";



export class Location{
  public country;
  public region;
  public city;

  constructor(country :string, region? :string, city? :string) {
    this.country = country;
    this.region = region;
    this.city = city;
  }
}


@Component({
  selector: 'dash-origin-map',
  templateUrl: './origin-map.component.html',
  styleUrls: ['./origin-map.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class OriginMapComponent extends DashBaseComponent implements AfterViewInit{
  total: number = 0;


  ngAfterViewInit() {
    const svgElement = this.element.nativeElement.querySelector('#Ebene_1');
    if (svgElement) {
      var pathElement = svgElement.querySelector("#NW")
      pathElement.classList.replace("st0", "st1");
    }
  }

}
