import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {TagRanking} from "../Tag";
import {TagListItemComponent} from "./tag-list-item/tag-list-item.component";

@Component({
  selector: 'dash-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagListComponent extends DashBaseComponent implements AfterViewInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  search_input : any;
  sorting_input_r : any;
  sorting_input_p : any;
  ngOnInit(): void {
    this.setToolTip("Auflistung aller #Tags, sortierbar nach Relevanz oder Performance")

    this.db.getAllTagsWithRelevanceAndPerformance().then(res => {
      for (var tag of res) {
        this.selectorItems.push(new SelectorItem(TagListItemComponent, new TagRanking(tag.id, tag.name, tag.relevance, tag.performance, tag.count)));
        this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRelevance((b.data as TagRanking)));
      }
      this.selectorItemsLoaded.next(this.selectorItems)
    })

    this.search_input = document.getElementById("tag-search");
    this.sorting_input_r = document.getElementById("tag-sort-r");
    this.sorting_input_p = document.getElementById("tag-sort-p");

    this.search_input.addEventListener("input", (event : any) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return item.data.name.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    })

    this.sorting_input_r.addEventListener("change", () => {
      this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRelevance((b.data as TagRanking)));
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.sorting_input_p.addEventListener("change", () => {
      this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByPerformance((b.data as TagRanking)));
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

  ngAfterViewInit(): void {
    this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRelevance((b.data as TagRanking)));
  }


}
