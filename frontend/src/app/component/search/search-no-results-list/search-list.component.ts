import {Component, OnDestroy, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Observable, Subject, Subscription} from "rxjs";
import {
  SearchAnbieterItem,
  SearchItem, SearchListAnbieterItemComponent,
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

  selectedSearchObserver : Subscription = new Subscription();

  title : string = "Suchanfragen";

  ngOnInit(): void {
  }
}
@Component({
  selector: 'dash-search-no-results-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListNoResultsComponent extends SearchListComponent implements OnDestroy{
  override title = "Suchanfragen ohne Ergebnis";
  override ngOnInit(): void {
    this.selectorItems = [];
    this.db.getSearchesWithoutResults().then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, search));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.selectedSearchObserver = SysVars.SELECTED_SEARCH.asObservable().subscribe(selection => {
      if (selection.operation == "IGNORE") {
        this.db.ignoreSearch(selection.item.id).then(r => {
          console.log((selection.item as SearchItem).search + " : Successfully set to ignore: " + r)
          if (r) {
            this.selectorItems = [];
            this.db.getSearchesWithoutResults().then(res => {
              for (var search of res) {
                this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, search));
              }
              this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
              this.selectorItemsLoaded.next(this.selectorItems);
            });
          }
        });

      }
    });
  }
  override ngOnDestroy() {
    this.selectedSearchObserver.unsubscribe();
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
        this.selectorItems.push(new SelectorItem(SearchListRankItemComponent, search));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchRank).foundCount - (a.data as SearchRank).foundCount);
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
        this.selectorItems.push(new SelectorItem(SearchListSSItemComponent, search));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchSS).foundCount - (a.data as SearchSS).foundCount);
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
}

@Component({
  selector: 'dash-search-anbieter-no-results-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListAnbieterNoResultsComponent extends SearchListComponent implements OnDestroy{
  override title = "Anbietersuchen ohne Ergebnis";

  override ngOnInit(): void {
    this.selectorItems = [];
    this.db.getSearchesAnbieterWithoutResults().then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListAnbieterItemComponent, search));
      }
      this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.selectedSearchObserver = SysVars.SELECTED_SEARCH.asObservable().subscribe(selection  => {
      if (selection.operation == "DELETE") {
        this.db.ignoreAnbieterSearch((selection.item as SearchAnbieterItem).search, (selection.item as SearchAnbieterItem).city).then(r => {
          console.log((selection.item as SearchAnbieterItem).search + " : " + (selection.item as SearchAnbieterItem).city + " : Successfully set deleted: " + r)
          if (r) {
            this.selectorItems = [];
            this.db.getSearchesAnbieterWithoutResults().then(res => {
              for (var search of res) {
                this.selectorItems.push(new SelectorItem(SearchListAnbieterItemComponent, search));
              }
              this.selectorItems.sort((a, b) => (b.data as SearchItem).count - (a.data as SearchItem).count);
              this.selectorItemsLoaded.next(this.selectorItems);
            });
          }
        });

      }
    });
  }
  override ngOnDestroy() {
    this.selectedSearchObserver.unsubscribe();
  }
}
