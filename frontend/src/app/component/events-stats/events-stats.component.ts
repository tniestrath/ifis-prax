import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import Util, {DashColors} from "../../util/Util";

@Component({
  selector: 'dash-newsletter-stats',
  templateUrl: './events-stats.component.html',
  styleUrls: ['./events-stats.component.css', "../dash-base/dash-base.component.css"]
})
export class EventsStatsComponent extends DashBaseComponent implements OnInit{
  protected readonly Util = Util;
  protected readonly DashColors = DashColors;

  upcomming : number = 0;
  upcomming_names : string[] = [];

  current : number = 0;
  current_names : string[] = [];

  ngOnInit(): void {

  }
}
