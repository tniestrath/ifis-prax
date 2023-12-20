import {Component, EventEmitter, Input} from '@angular/core';
import {TagRanking} from "../../Tag";
import {DbObject} from "../../../../services/DbObject";
import {SysVars} from "../../../../services/sys-vars-service";
import Util from "../../../../util/Util";

@Component({
  selector: 'dash-tag-list-item',
  templateUrl: './tag-list-item.component.html',
  styleUrls: ['./tag-list-item.component.css']
})
export class TagListItemComponent {
  data : TagRanking = new TagRanking("", "", "", "", "");
  protected readonly parseFloat = parseFloat;

  onClick(){
    SysVars.SELECTED_TAG.emit(Number(this.data?.id));
  }

  protected readonly Util = Util;
}
