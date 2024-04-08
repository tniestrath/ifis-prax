import { Component } from '@angular/core';
import {DbObject} from "../../../../services/DbObject";


export class BadBot extends DbObject{
  public ip : string;
  public date : string;
  public agent : string;
  constructor(ip : string, date : string, agent : string) {
    super(ip, agent);
    this.ip = ip;
    this.date = date;
    this.agent = agent;
  }

}

@Component({
  selector: 'dash-bad-bot-item',
  templateUrl: './bad-bot-item.component.html',
  styleUrls: ['./bad-bot-item.component.css']
})
export class BadBotItemComponent {
  data : BadBot = new BadBot("","", "");

}
