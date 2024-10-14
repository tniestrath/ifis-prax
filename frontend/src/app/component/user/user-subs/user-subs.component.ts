import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-user-subs',
  templateUrl: './user-subs.component.html',
  styleUrls: ['./user-subs.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserSubsComponent extends DashBaseComponent implements OnInit{

  data : {change: number, countToday : number } = {change: 0, countToday: 0};

  ngOnInit(): void {
      this.api.getUsersSubsCount().then(value => {
        this.data.change = value.change;
        this.data.countToday = value.countToday;
      });
  }

  protected readonly Util = Util;
  protected readonly Math = Math;
  protected readonly DashColors = DashColors;
}
