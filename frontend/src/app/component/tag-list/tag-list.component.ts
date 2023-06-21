import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SelectorItem} from "../../page/selector/selector.component";
import {Subject} from "rxjs";
import {TagRanking} from "../../page/tag/Tag";
import {TagListItemComponent} from "./tag-list-item/tag-list-item.component";

@Component({
  selector: 'dash-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class TagListComponent extends DashBaseComponent implements AfterViewInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  search_input : any;
  sorting_input_r : any;
  sorting_input_a : any;
  ngOnInit(): void {
    this.setToolTip("Auflistung aller #Tags, sortierbar nach Relevanz oder Anzahl der globalen Beiträge zu diesem Thema")

    this.db.getAllTagsWithCountAndRelevance().then(res => {
      for (var tag of res) {
        this.selectorItems.push(new SelectorItem(TagListItemComponent, new TagRanking(tag.id, tag.name, tag.relevance, tag.count)));
        this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRelevance((b.data as TagRanking)));
      }
      this.selectorItemsLoaded.next(this.selectorItems)
    })

    this.search_input = document.getElementById("tag-search");
    this.sorting_input_r = document.getElementById("tag-sort-r");
    this.sorting_input_a = document.getElementById("tag-sort-a");

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
    this.sorting_input_a.addEventListener("change", () => {
      this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByCount((b.data as TagRanking)));
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

  ngAfterViewInit(): void {
    this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRelevance((b.data as TagRanking)));
  }


}
