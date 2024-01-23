import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import Util, {DashColors} from "../../../../util/Util";
import {SelectorItem} from "../../../selector/selector.component";
import {Subject} from "rxjs";
import {TagListItemComponent} from "../../../../component/tag/tag-list/tag-list-item/tag-list-item.component";
import {TagRanking} from "../../../../component/tag/Tag";
import {SysVars} from "../../../../services/sys-vars-service";

@Component({
  selector: 'dash-user-tags-dist',
  templateUrl: './user-tags-dist.component.html',
  styleUrls: ['./user-tags-dist.component.css', "./../../../../component/dash-base/dash-base.component.css"]
})
export class UserTagsDistComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {
    this.setToolTip("", false);
    this.db.getUserTagsDistributionPercentage().then((res : {tagLabel : string[], tagPercentages : number[], tagCounts : number[]})  => {
      for (let i = 0; i < res.tagLabel.length; i++) {
        this.selectorItems.push(new SelectorItem(TagListItemComponent, new TagRanking(String(res.tagLabel.at(i)), String(res.tagLabel.at(i)), String(res.tagPercentages.at(i)), String(res.tagCounts.at(i)), "")));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    })
  }

}

@Component({
  selector: 'dash-user-tags-dist',
  templateUrl: './user-tags-dist.component.html',
  styleUrls: ['./user-tags-dist.component.css', "./../../../../component/dash-base/dash-base.component.css"]
})
export class SingleUserTagsDistComponent extends UserTagsDistComponent implements OnInit{

  override ngOnInit(): void {
    this.setToolTip("", false);
    this.element.nativeElement.getElementsByClassName("component-box")[0].classList.add("no-margin-top");
    this.element.nativeElement.getElementsByClassName("user-tags-dist-title")[0].children[0].innerText = "Plazierung innerhalb der gewählten Themen";

    //ROADWORK AHEAD


    /*this.db.getUserTagsRanking(SysVars.USER_ID, "").then((res : {percentage : number[], ranking : number[]})  => {
      for (let i = 0; i < res.tagLabel.length; i++) {
        this.selectorItems.push(new SelectorItem(TagListItemComponent, new TagRanking(String(res.tagLabel.at(i)), String(res.tagLabel.at(i)), String(res.tagPercentages.at(i)), '#'+String(res.ranking.at(i)), "")));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    })*/
  }

}
