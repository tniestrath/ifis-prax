import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {TagListItemComponent} from "../../tag/tag-list/tag-list-item/tag-list-item.component";
import {TagRanking} from "../../tag/Tag";
import {SEOKeyword, SeoKeywordListItemComponent} from "./seo-keyword-list-item/seo-keyword-list-item.component";

@Component({
  selector: 'dash-seo-keyword-list',
  templateUrl: './seo-keyword-list.component.html',
  styleUrls: ['./seo-keyword-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SeoKeywordListComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {
    this.db.getSeoKeywordsNow().then(value => {
      for (let element of value) {
        this.selectorItems.push(new SelectorItem(SeoKeywordListItemComponent,
          new SEOKeyword(element.clicks, element.impressions, element.keys.at(1), element.keys.at(0), element.ctr)));
      }
      this.selectorItems.sort((a, b) => Number((b.data as TagRanking).relevance) - Number((a.data as TagRanking).relevance));
      this.selectorItemsLoaded.next(this.selectorItems);
    })
  }

}