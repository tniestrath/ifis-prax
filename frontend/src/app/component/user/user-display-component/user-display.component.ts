import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util from "../../../util/Util";
import {User} from "../user";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-user-display-component',
  templateUrl: './user-display.component.html',
  styleUrls: ['./user-display.component.css']
})
export class UserDisplayComponent extends DashBaseComponent implements OnInit{
  user : User = new User();

  ngOnInit(): void {
    this.setToolTip("Hier ist eine kurzzusammenfassung des Profils zu sehen.<br><br>" +
      "<img src='assets/profile_views.png' style='height: 15px; filter:invert(1)'> Profilaufrufe<br>" +
      "<img src='assets/pencil-solid.png' style='height: 15px; filter:invert(1)'> Inhaltsaufrufe<br>" +
      "<img src='assets/24-hours_x25.png' style='height: 15px; filter:invert(1)'> Durchschn. Aufrufe pro Tag<br>" +
      "<img src='assets/trend_up.png' style='height: 15px; filter:invert(1)'> Tendenz sinkend / stagnierend / steigend : seit letztem Monat<br>" +
      "<img src='assets/target.png' style='height: 15px; filter:invert(1)'> Weiterleitungen zur eigenen Homepage<br>" +
      "<img src='assets/redirectsMonthly.png' style='height: 15px; filter:invert(1)'> Weiterleitungen diesen Monat<br><br>" +
      "#1 (#2) -> Plazierung unter allen Anbietern (unter Anbietern der gleichen Abo-Stufe)", 2);
    this.api.getUserAllStatsById(SysVars.USER_ID).then(res => {
      this.user = res;
    });
  }

  protected readonly Util = Util;
  protected readonly Math = Math;
  protected readonly parseFloat = parseFloat;
}
