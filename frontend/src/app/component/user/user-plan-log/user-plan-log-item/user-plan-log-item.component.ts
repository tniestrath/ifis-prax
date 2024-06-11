import {AfterViewInit, Component} from '@angular/core';
import {DbObject} from "../../../../services/DbObject";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";
import Util from "../../../../util/Util";


export class UserPlanLogItem extends DbObject{

  user : string = "";
  oldPlan : string = "";
  newPlan : string = "";
  time : string = "";

  constructor(user : string, oldPlan : string, newPlan : string, time : string) {
    super(time, user);
    this.user = user;
    this.oldPlan = oldPlan;
    this.newPlan = newPlan;
    this.time = time;
  }
}

@Component({
  selector: 'dash-user-plan-log-item',
  templateUrl: './user-plan-log-item.component.html',
  styleUrls: ['./user-plan-log-item.component.css']
})
export class UserPlanLogItemComponent extends DashListItemComponent implements AfterViewInit{
  override data : UserPlanLogItem = new UserPlanLogItem("", "", "", "");

  protected readonly Util = Util;

  ngAfterViewInit(): void {
    if (this.data.oldPlan == this.data.newPlan && this.data.oldPlan == "none"){
      this.data.newPlan = "Registriert";
    }
  }
}
