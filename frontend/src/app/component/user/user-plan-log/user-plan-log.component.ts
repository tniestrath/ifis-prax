import { Component } from '@angular/core';
import {DashListPageableComponent} from "../../dash-list/dash-list.component";
import {UserPlanLogItem, UserPlanLogItemComponent} from "./user-plan-log-item/user-plan-log-item.component";

@Component({
  selector: 'dash-user-plan-log',
  templateUrl: './user-plan-log.component.html',
  styleUrls: ['./user-plan-log.component.css']
})
export class UserPlanLogComponent extends DashListPageableComponent<UserPlanLogItem, UserPlanLogItemComponent>{

  override ngOnInit() {

  }
}
