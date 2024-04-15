import {Component, OnDestroy, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject, Subscription} from "rxjs";
import {
  SearchAnbieterItem,
  SearchItem,
  SearchListAnbieterItemComponent,
  SearchListNoResultsItemComponent,
  SearchListSSItemComponent, SearchSS
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
  pageSize: number = 10;

  sorter : string = "count";
  dir : string = "DESC";

  ngOnInit(): void {
  }
  onSorterSwitched(sorter : any){
  }
  onScrollEnd() {
  }
  onFreeSpace(space : number){
  }
  onDirSwitched(dir: any) {
  }
}

@Component({
  selector: 'dash-search-combined-list',
  templateUrl: './search-list.component.html',
  styleUrls: ['./search-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class SearchListCombinedComponent extends SearchListComponent implements OnDestroy{
  override title = "Suchanfragen";
  override ngOnInit() {
    this.setToolTip("Hier finden sie die erfolgreichsten Suchanfragen der Hauptsuche.<br><br>"
      + "<img src=\"assets/repeat.png\" style=\"height: 15px; filter:invert(1)\"> Häufigkeit<br>"
      + "<img src=\"assets/bulb.png\" style=\"height: 15px; filter:invert(1)\"> Gefundene Ergebnisse<br>"
      + "<img src=\"assets/target-click.png\" style=\"height: 15px; filter:invert(1)\"> Geklickte Ergebnisse<br>");
    this.selectorItems = [];
    this.pageIndex = 0;
    this.api.getSearchesCool(this.pageIndex, this.pageSize, this.sorter, this.dir).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListSSItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;

    this.selectedSearchObserver = SysVars.SELECTED_SEARCH.asObservable().subscribe(selection => {
      if (selection.operation == "IGNORE") {
        console.log(selection.item)
        this.api.flipSearch(selection.item.id).then(r => {
          console.log((selection.item as SearchSS).query + " : Successfully set to ignore: " + r)
          if (r == "DELETED") {
            let index = -1;
            for (let i = 0; i < this.selectorItems.length; i++) {
              if (this.selectorItems[i].data.id == selection.item.id){ index = i; break;}
            }
            // @ts-ignore
            (this.selectorItems.at(index).data as SearchSS).query = "GELÖSCHT";
            this.selectorItemsLoaded.next(this.selectorItems);
          } else {
            let index = -1;
            for (let i = 0; i < this.selectorItems.length; i++) {
              if (this.selectorItems[i].data.id == selection.item.id){ index = i; break;}
            }
            // @ts-ignore
            (this.selectorItems.at(index).data as SearchSS).query = r;
            this.selectorItemsLoaded.next(this.selectorItems);
          }
        });
      }
    });
  }
  override onSorterSwitched(sorter : any){
    this.sorter = (sorter.target as HTMLInputElement).value;
    this.selectorItems = [];
    this.pageIndex = 0;
    this.api.getSearchesCool(this.pageIndex, this.pageSize, this.sorter, this.dir).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListSSItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;
  }

  override onDirSwitched(dir : any){
    if((dir.target as HTMLInputElement).checked) this.dir = "ASC";
    else this.dir = "DESC";

    this.selectorItems = [];
    this.pageIndex = 0;
    this.api.getSearchesCool(this.pageIndex, this.pageSize, this.sorter, this.dir).then(res => {
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
        this.api.getSearchesCool(this.pageIndex, this.pageSize, this.sorter, this.dir).then(res => {
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

  override onFreeSpace(space: number) {
    if (space >= -100){
      if (!this.pagesComplete){
        console.log(this.pageIndex)
        this.api.getSearchesCool(this.pageIndex, this.pageSize, this.sorter, this.dir).then(res => {
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
    }
  }

  override ngOnDestroy() {
    this.selectedSearchObserver.unsubscribe();
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
    this.api.getSearchesAnbieterWithoutResults(this.pageIndex, this.pageSize).then(res => {
      for (var search of res) {
        this.selectorItems.push(new SelectorItem(SearchListAnbieterItemComponent, search));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.pageIndex++;
    //TODO: IMPLEMENT UNDO LIKE ABOVE
    this.selectedSearchObserver = SysVars.SELECTED_SEARCH.asObservable().subscribe(selection  => {
      if (selection.operation == "DELETE") {
        this.api.flipAnbieterSearch((selection.item as SearchAnbieterItem).id).then(r => {
          console.log((selection.item as SearchAnbieterItem).search + " : " + (selection.item as SearchAnbieterItem).city + " : Successfully set deleted: " + r)
          if ((r as { city: string, query : string }).query == "DELETED") {
            let index = -1;
            for (let i = 0; i < this.selectorItems.length; i++) {
              if (this.selectorItems[i].data.id == selection.item.id){ index = i; break;}
            }
            // @ts-ignore
            (this.selectorItems.at(index).data as SearchAnbieterItem).search = " - - - ";
            // @ts-ignore
            (this.selectorItems.at(index).data as SearchAnbieterItem).city = "GELÖSCHT";
            this.selectorItemsLoaded.next(this.selectorItems);
          } else {
            let index = -1;
            for (let i = 0; i < this.selectorItems.length; i++) {
              if (this.selectorItems[i].data.id == selection.item.id){ index = i; break;}
            }
            // @ts-ignore
            (this.selectorItems.at(index).data as SearchAnbieterItem).search = (r as { city: string, query : string }).query;
            // @ts-ignore
            (this.selectorItems.at(index).data as SearchAnbieterItem).city = (r as { city: string, query : string }).city;
            this.selectorItemsLoaded.next(this.selectorItems);
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
        this.api.getSearchesAnbieterWithoutResults(this.pageIndex, this.pageSize).then(res => {
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

  override onFreeSpace(space: number) {
    console.log(space + " : " + this.title)
    if (space >= -100){
      if (!this.pagesComplete){
        console.log(this.pageIndex)
        this.api.getSearchesAnbieterWithoutResults(this.pageIndex, this.pageSize).then(res => {
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
    }
  }

  override ngOnDestroy() {
    this.selectedSearchObserver.unsubscribe();
  }
}
