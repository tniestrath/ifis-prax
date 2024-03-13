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
  pagesComplete: boolean = false;
  lastScroll: number = 0;
  pageIndex: number = 0;
  pageSize: number = 15;

  ngOnInit(): void {
  }
  onScrollEnd() {
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
    this.setToolTip("Hier finden sie alle Suchanfragen der Hauptsuche.<br><br>Unbrauchbare Anfragen können sie auch aus der Liste entfernen.<br><br>"
      + "<img src=\"assets/repeat.png\" style=\"height: 15px; filter:invert(1)\"> Häufigkeit<br>"
      + "<img src=\"assets/trash-can.png\" style=\"height: 15px; filter:invert(1)\"> Klicken zum entfernen<br>");
    this.selectorItems = [];
    this.pageIndex = 0;
    this.db.getSearchesWithoutResults(this.pageIndex, this.pageSize).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;

    this.selectedSearchObserver = SysVars.SELECTED_SEARCH.asObservable().subscribe(selection => {
      if (selection.operation == "IGNORE") {
        this.db.ignoreSearch(selection.item.id).then(r => {
          console.log((selection.item as SearchItem).search + " : Successfully set to ignore: " + r)
          if (r) {
            this.selectorItems = [];
            this.db.getSearchesWithoutResults(0, this.pageSize).then(res => {
              for (var search of res) {
                this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, search));
              }
              this.selectorItemsLoaded.next(this.selectorItems);
            });
            this.pageIndex++;
          }
        });

      }
    });
  }
  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getSearchesWithoutResults(this.pageIndex, this.pageSize).then(res => {
          for (var search of res) {
            this.selectorItems.push(new SelectorItem(SearchListNoResultsItemComponent, search));
          }
          if (res.length <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
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
    this.setToolTip("Hier werden die Begriffe aufgelistet nach denen am meisten gesucht wurde und wie viele Ergebnisse diese erzielten.<br><br>"
                    + "<img src=\"assets/repeat.png\" style=\"height: 15px; filter:invert(1)\"> Häufigkeit<br>"
                    + "<img src=\"assets/bulb.png\" style=\"height: 15px; filter:invert(1)\"> Gefundene Ergebnisse<br>");
    this.selectorItems = [];
    this.pageIndex = 0;
    this.db.getSearchesTopN(this.pageIndex, this.pageSize).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListRankItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getSearchesTopN(this.pageIndex, this.pageSize).then(res => {
          for (var search of res) {
            this.selectorItems.push(new SelectorItem(SearchListRankItemComponent, search));
          }
          if (res.length <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
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
    this.setToolTip("Hier finden sie die erfolgreichsten Suchanfragen der Hauptsuche.<br><br>"
      + "<img src=\"assets/repeat.png\" style=\"height: 15px; filter:invert(1)\"> Häufigkeit<br>"
      + "<img src=\"assets/bulb.png\" style=\"height: 15px; filter:invert(1)\"> Gefundene Ergebnisse<br>"
      + "<img src=\"assets/target-click.png\" style=\"height: 15px; filter:invert(1)\"> Geklickte Ergebnisse<br>");
    this.selectorItems = [];
    this.pageIndex = 0;
    this.db.getSearchesTopNBySS(this.pageIndex, this.pageSize).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListSSItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getSearchesTopNBySS(this.pageIndex, this.pageSize).then(res => {
          for (var search of res) {
            this.selectorItems.push(new SelectorItem(SearchListSSItemComponent, search));
          }
          if (res.length <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
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
    this.setToolTip("Hier finden sie alle Suchanfragen der Anbietersuche.<br><br>Unbrauchbare Anfragen können sie auch aus der Liste entfernen.<br><br>"
      + "<img src=\"assets/repeat.png\" style=\"height: 15px; filter:invert(1)\"> Häufigkeit<br>"
      + "<img src=\"assets/trash-can.png\" style=\"height: 15px; filter:invert(1)\"> Klicken zum entfernen<br>");
    this.selectorItems = [];
    this.pageIndex = 0;
    this.db.getSearchesAnbieterWithoutResults(this.pageIndex, this.pageSize).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListAnbieterItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;

    this.selectedSearchObserver = SysVars.SELECTED_SEARCH.asObservable().subscribe(selection  => {
      if (selection.operation == "DELETE") {
        this.db.ignoreAnbieterSearch((selection.item as SearchAnbieterItem).id).then(r => {
          console.log((selection.item as SearchAnbieterItem).search + " : " + (selection.item as SearchAnbieterItem).city + " : Successfully set deleted: " + r)
          if (r) {
            this.selectorItems = [];
            this.db.getSearchesAnbieterWithoutResults(0, this.pageSize).then(res => {
              for (var search of res) {
                this.selectorItems.push(new SelectorItem(SearchListAnbieterItemComponent, search));
              }
              this.selectorItemsLoaded.next(this.selectorItems);
            });
            this.pageIndex++;
          }
        });

      }
    });
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getSearchesAnbieterWithoutResults(this.pageIndex, this.pageSize).then(res => {
          for (var search of res) {
            this.selectorItems.push(new SelectorItem(SearchListAnbieterItemComponent, search));
          }
          if (res.length <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }

  override ngOnDestroy() {
    this.selectedSearchObserver.unsubscribe();
  }
}
