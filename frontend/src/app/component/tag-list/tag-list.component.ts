import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SelectorItem} from "../../page/selector/selector.component";
import {TagComponent} from "../../page/tag/tag/tag.component";
import {DbObject} from "../../services/DbObject";
import {Subject} from "rxjs";
import {TagRanking} from "../../page/tag/Tag";
import {TagListItemComponent} from "./tag-list-item/tag-list-item.component";

@Component({
  selector: 'dash-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class TagListComponent extends DashBaseComponent implements AfterViewInit{
  selectorItems : SelectorItem[] = [new SelectorItem(TagListItemComponent, new TagRanking("0", "test1", "1", "1")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("1", "test2", "2", "99")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("2", "test3", "3", "101")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("3", "test4", "4", "102")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("4", "test5", "5", "103")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("5", "test6", "6", "110")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("6", "test7", "7", "98")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("7", "test8", "10", "97")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("8", "test9", "8", "96")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("9", "test10", "9", "5")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("10", "test11", "13", "7")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("11", "test12", "12", "88")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("12", "test13", "11", "100"))];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  search_input : any;
  sorting_input_p : any;
  sorting_input_a : any;

  sortByRank = true;

  ngOnInit(): void {
    this.search_input = document.getElementById("tag-search");
    this.sorting_input_p = document.getElementById("tag-sort-p");
    this.sorting_input_a = document.getElementById("tag-sort-a");

    this.search_input.addEventListener("input", (event : any) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return item.data.name.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    })

    this.sorting_input_p.addEventListener("change", () => {
      this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRank((b.data as TagRanking)));
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.sorting_input_a.addEventListener("change", () => {
      this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByCount((b.data as TagRanking)));
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

  ngAfterViewInit(): void {
    this.selectorItems.sort((a, b) => (a.data as TagRanking).compareByRank((b.data as TagRanking)));
    this.selectorItemsLoaded.next(this.selectorItems);
  }


}
