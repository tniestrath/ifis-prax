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

  upcoming : number = 0;
  upcoming_names : string[] = [];
  upcoming_today : number = 0;
  upcoming_yesterday : number = 0;

  current : number = 0;
  current_names : string[] = [];
  current_today : number = 0;
  current_yesterday : number = 0;

  ngOnInit(): void {
    this.db.getEvents().then(res => {
      for (let event of res) {
        if (event.startsWith("u")) {
          this.upcoming++;
          this.upcoming_names.push(event.split("|", 2)[1]);
        }
        if (event.startsWith("c")) {
          this.current++;
          this.current_names.push(event.split("|", 2)[1]);
        }
      }
    }).then( () =>
      this.db.getEventsYesterday().then(res => {
        for (let event of res) {
          if (event.startsWith("u")) this.upcoming_yesterday++;
          if (event.startsWith("c")) this.current_yesterday++;
        }
      })).then(() => {
      this.upcoming_today = this.upcoming - this.upcoming_yesterday;
      this.current_today = this.current - this.current_yesterday;
    });

    this.setToolTip("Hier sind die aktuellen Veranstaltungen angezeigt. Mit Hover über die Zahlen werden genauere Daten angezeigt.");
  }
}
