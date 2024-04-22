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
    this.setToolTip("", 1,false);
    this.api.getUserAllStatsById(SysVars.USER_ID).then(res => {
      this.user = res;
    });
  }

  protected readonly Util = Util;
  protected readonly Math = Math;
  protected readonly parseFloat = parseFloat;
}
