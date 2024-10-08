import {Component, EventEmitter, Input} from '@angular/core';
import {Tag, TagRanking} from "../../Tag";
import {DbObject} from "../../../../services/DbObject";
import {SysVars} from "../../../../services/sys-vars-service";
import Util, {DashColors} from "../../../../util/Util";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";

@Component({
  selector: 'dash-tag-list-item',
  templateUrl: './tag-list-item.component.html',
  styleUrls: ['./tag-list-item.component.css']
})
export class TagListItemComponent extends DashListItemComponent{
  override data : TagRanking = new TagRanking("", "", 0, 0, 0, 0);
  protected readonly parseFloat = parseFloat;

  override onClick(){
    SysVars.SELECTED_TAG.emit(this.data as Tag);
  }

  protected readonly Util = Util;
}
