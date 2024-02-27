import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {
  SearchItem,
  SearchListItemComponent, SearchListNoResultsItemComponent, SearchListRankItemComponent,
  SearchListSSItemComponent,
  SearchRank,
  SearchSS
} from "./search-list-item/search-list-item.component";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-search-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  title : string = "Suchanfragen";

  ngOnInit(): void {
  }
}
@Component({
  selector: 'dash-search-no-results-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListNoResultsComponent extends SearchListComponent {
  override title = "Suchanfragen ohne Ergebnis";
  override ngOnInit(): void {
    this.selectorItems = [];
    this.db.getSearchesWithoutResults().then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, new SearchItem(search.id, search.search, search.count)));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    SysVars.SELECTED_SEARCH.observers = [];
    SysVars.SELECTED_SEARCH.subscribe(selection => {
      if (selection.operation == "IGNORE") {
        this.db.ignoreSearch(selection.item.id).then(r => {
          console.log((selection.item as SearchItem).search + " : Successfully set to ignore: " + r)
          if (r) {
            this.selectorItems = [];
            this.db.getSearchesWithoutResults().then(res => {
              for (var search of res) {
                this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, new SearchItem(search.id, search.search, search.count)));
              }
              this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
              this.selectorItemsLoaded.next(this.selectorItems);
            });
          }
        });

      }
    });
  }
}

@Component({
  selector: 'dash-search-rank-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListRankComponent extends SearchListComponent {
  override title = "Häufigste Suchanfragen";
  override ngOnInit() {
    this.selectorItems = [];
    this.db.getSearchesTopN(15).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListRankItemComponent, new SearchRank(search.id, search.query, search.searchedCount, search.rank)));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
}
@Component({
  selector: 'dash-search-ss-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListSSComponent extends SearchListComponent {
  override title = "Erfolgreichste Suchanfragen";
  override ngOnInit() {
    this.selectorItems = [];
    this.db.getSearchesTopNBySS(15).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListSSItemComponent, new SearchSS(search.id, search.query, search.sSCount, search.rank)));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
}
