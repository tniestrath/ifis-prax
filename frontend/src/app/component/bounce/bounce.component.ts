import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import Util, {DashColors} from "../../util/Util";

@Component({
  selector: 'dash-bounce',
  templateUrl: './bounce.component.html',
  styleUrls: ['./bounce.component.css', "../dash-base/dash-base.component.css"]
})
export class BounceComponent extends DashBaseComponent implements OnInit{
  bounceRate : number = 0;
  totalBounces : number = 0;

  ngOnInit(): void {
    this.api.getBounces().then(value => {
      this.bounceRate = value.bounceRate;
      this.totalBounces = value.totalBounces;
    });
  }

  protected readonly DashColors = DashColors;
  protected readonly Util = Util;
}
