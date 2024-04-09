import { Component } from '@angular/core';
import Util from "../../../../util/Util";
import {DbObject} from "../../../../services/DbObject";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";


export class SEOKeyword extends DbObject{
  constructor(public clicks : number = 0, public impressions : number = 0, public override name : string = "", public url : string = "", public ctr : number = 0) {
    super(name, name);
  }
}

@Component({
  selector: 'dash-seo-keyword-list-item',
  templateUrl: './seo-keyword-list-item.component.html',
  styleUrls: ['./seo-keyword-list-item.component.css']
})
export class SeoKeywordListItemComponent extends DashListItemComponent{
  override data = new SEOKeyword();

  onOver(isOver: boolean, more?: HTMLDivElement){
      if (isOver){
        // @ts-ignore
        more.style.display = "flex";
      } else {
        // @ts-ignore
        more.style.display = "none";
      }
  }

  protected readonly Util = Util;
  protected readonly parseFloat = parseFloat;
}
