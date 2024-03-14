import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {TagListItemComponent} from "../../tag/tag-list/tag-list-item/tag-list-item.component";
import {TagRanking, UserTagDist} from "../../tag/Tag";
import {SysVars} from "../../../services/sys-vars-service";
import {UserTagDistItemComponent} from "./user-tag-dist-item/user-tag-dist-item.component";

@Component({
  selector: 'dash-user-tags-dist',
  templateUrl: './user-tags-dist.component.html',
  styleUrls: ['./user-tags-dist.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserTagsDistComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {
    this.setToolTip("", 1,false);
    this.db.getUserTagsDistributionPercentage().then((res : {name: string, count: number}[])  => {
      let totalIndex = res.findIndex((value, index) => {return value.name.includes("countTotal");})
      // @ts-ignore
      let total = res.at(totalIndex).count;
      for (let i = 0; i < res.length; i++) {
        // @ts-ignore
        if (i != totalIndex) this.selectorItems.push(new SelectorItem(UserTagDistItemComponent, new UserTagDist("", String(res.at(i).name), String(res.at(i).count), "", String((res.at(i).count / total) * 100))));
      }
      this.selectorItems.sort((a, b) => Number((b.data as UserTagDist).percentage) - Number((a.data as UserTagDist).percentage));
      this.selectorItemsLoaded.next(this.selectorItems);
    })
  }
}

@Component({
  selector: 'dash-user-tags-dist',
  templateUrl: './user-tags-dist.component.html',
  styleUrls: ['./user-tags-dist.component.css', "../../dash-base/dash-base.component.css"]
})
export class SingleUserTagsDistComponent extends UserTagsDistComponent implements OnInit{

  override ngOnInit(): void {
    this.setToolTip("", 1,SysVars.CURRENT_PAGE != "PRINT");
    this.element.nativeElement.getElementsByClassName("component-box")[0].classList.add("no-margin-top");
    this.element.nativeElement.getElementsByClassName("user-tags-dist-title")[0].classList.add("no-full-width");
    this.element.nativeElement.getElementsByClassName("user-tags-dist-title")[0].children[0].innerText = "Platzierung innerhalb der gewählten Themen";

    this.db.getUserTagsRanking(SysVars.USER_ID, "profile").then((res : {name : string, percentage : number, ranking : number, count : number}[])  => {
      for (let tag of res) {
        this.selectorItems.push(new SelectorItem(UserTagDistItemComponent, new UserTagDist("", tag.name, -1, '#' + tag.ranking + " / " + tag.count, tag.percentage)));
      }
      this.selectorItems.sort((a, b) => Number((b.data as UserTagDist).percentage) - Number((a.data as UserTagDist).percentage))
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
}
