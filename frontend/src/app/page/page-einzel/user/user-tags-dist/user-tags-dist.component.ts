import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
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
    this.db.getUserTagsDistributionPercentage().then((res : {name: string, count: number}[])  => {
      console.log(res)
      let totalIndex = res.findIndex((value, index) => {return value.name.includes("countTotal");})
      // @ts-ignore
      let total = res.at(totalIndex).count;
      for (let i = 0; i < res.length; i++) {
        // @ts-ignore
        if (i != totalIndex) this.selectorItems.push(new SelectorItem(TagListItemComponent, new TagRanking(String(res.at(i).name), String(res.at(i).name), String((res.at(i).count / total) * 100), String(res.at(i).count), "")));
      }
      this.selectorItems.sort((a, b) => Number((b.data as TagRanking).views) - Number((a.data as TagRanking).views));
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
    this.setToolTip("", true);
    this.element.nativeElement.getElementsByClassName("component-box")[0].classList.add("no-margin-top");
    this.element.nativeElement.getElementsByClassName("user-tags-dist-title")[0].classList.add("no-full-width");
    this.element.nativeElement.getElementsByClassName("user-tags-dist-title")[0].children[0].innerText = "Platzierung innerhalb der gewählten Themen";

    this.db.getUserTagsRanking(SysVars.USER_ID, "profile").then((res : {name : string, percentage : number, ranking : number, count : number}[])  => {
      for (let tag of res) {
        this.selectorItems.push(new SelectorItem(TagListItemComponent, new TagRanking(tag.name,tag.name, String(tag.percentage), '#' + tag.ranking + " / " + tag.count, "")));
      }
      this.selectorItems.sort((a, b) => Number((b.data as TagRanking).relevance) - Number((a.data as TagRanking).relevance))
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

}
