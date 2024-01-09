import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {SelectableComponent} from "../../selector/selectable.component";
import {SafeUrl} from "@angular/platform-browser";
import {DbService} from "../../../services/db.service";
import {User} from "./user";
import Util from "../../../util/Util";
import {SysVars} from "../../../services/sys-vars-service";
import {DashBaseComponent} from "../../../component/dash-base/dash-base.component";

@Component({
  selector: 'dash-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements SelectableComponent, OnInit {
  @Input() data: User = new User();

  bgColor: string = "0";

  tendency: string = "stagniert";

  constructor(private db: DbService) {
  }

  onClick(): void {
    SysVars.SELECTED_USER_ID.emit(Number(this.data.id));
  }

  ngOnInit(): void {

  }

  protected readonly Util = Util;
  protected readonly parseFloat = parseFloat;
  protected readonly Math = Math;
}
