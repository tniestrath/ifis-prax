import { Component } from '@angular/core';
import Util from "../../../../util/Util";
import {UserTagDist} from "../../../tag/Tag";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";

@Component({
  selector: 'dash-user-tag-dist-item',
  templateUrl: './user-tag-dist-item.component.html',
  styleUrls: ['./user-tag-dist-item.component.css']
})
export class UserTagDistItemComponent extends DashListItemComponent{
  override data : UserTagDist = new UserTagDist();

  protected readonly Util = Util;
  protected readonly parseFloat = parseFloat;
  protected readonly String = String;

  override onClick(){

  }

}
