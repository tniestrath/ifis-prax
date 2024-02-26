import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {TagListItemComponent} from "../../tag/tag-list/tag-list-item/tag-list-item.component";
import {TagRanking} from "../../tag/Tag";
import {
  SearchItem,
  SearchNoResultsListItemComponent
} from "./search-no-results-list-item/search-no-results-list-item.component";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-search-no-results-list',
  templateUrl: './search-no-results-list.component.html',
  styleUrls: ['./search-no-results-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchNoResultsListComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {
    this.db.getSearchesWithoutResults().then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchNoResultsListItemComponent, new SearchItem(search.id, search.search, search.count)));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    SysVars.SELECTED_SEARCH.observers = [];
    SysVars.SELECTED_SEARCH.subscribe(selection => {
      if (selection.operation == "IGNORE"){
        this.db.ignoreSearch(selection.item.id).then(r => {
          console.log( selection.item.search + " : Successfully set to ignore: " + r)});
      }
    });

  }

}
