import { Component } from '@angular/core';
import {Post} from "../../post/Post";
import {DbObject} from "../../../services/DbObject";
import {SysVars} from "../../../services/sys-vars-service";
import {SelectableComponent} from "../../../page/selector/selectable.component";

@Component({
  selector: 'dash-dash-list-item',
  templateUrl: './dash-list-item.component.html',
  styleUrls: ['./dash-list-item.component.css']
})
export abstract class DashListItemComponent implements SelectableComponent{
  data: DbObject = new DbObject("", "");
  bgColor: string = "#fff";

  onClick(data : any): any {
  }

}
