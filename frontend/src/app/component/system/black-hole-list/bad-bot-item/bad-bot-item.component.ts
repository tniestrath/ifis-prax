import { Component } from '@angular/core';
import {DbObject} from "../../../../services/DbObject";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";


export class BadBot extends DbObject{
  public ip_address : string;
  public date : string;
  public user_agent : string;
  constructor(ip_address : string, date : string, user_agent : string) {
    super(ip_address, user_agent);
    this.ip_address = ip_address;
    this.date = date;
    this.user_agent = user_agent;
  }

}

@Component({
  selector: 'dash-bad-bot-item',
  templateUrl: './bad-bot-item.component.html',
  styleUrls: ['./bad-bot-item.component.css']
})
export class BadBotItemComponent extends DashListItemComponent{
  override data : BadBot = new BadBot("","", "");

}
