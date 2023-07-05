import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";

@Component({
  selector: 'dash-origin-map',
  templateUrl: './origin-map.component.html',
  styleUrls: ['./origin-map.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class OriginMapComponent extends DashBaseComponent implements OnInit{


  ngOnInit(): void {
    var map = document.getElementById("desvg") ?? new HTMLElement();
    map.addEventListener("load", () => {
      var regions = map.querySelector("style");
      console.log(regions)
    })

  }

}
