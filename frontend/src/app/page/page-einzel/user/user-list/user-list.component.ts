import {Component, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import {Subject} from "rxjs";
import {SelectorItem} from "../../../selector/selector.component";
import {UserComponent} from "../user.component";
import {DbObject} from "../../../../services/DbObject";


@Component({
  selector: 'dash-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css', '../../../../component/dash-base/dash-base.component.css']
})
export class UserListComponent extends DashBaseComponent implements OnInit{
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  listItems : SelectorItem[] = [];
  @Input() parentListItems : SelectorItem[] = [];
  @Input() parentPageIndex : number = 0;
  pageIndex: number = 0;
  private pagesComplete: boolean = false;
  private lastScroll: number = 0;
  private pageSize: number = 30;

  private searchText: string = "";
  private selectedFilter = {accType : " ", sort : "userId"};
  private isSearching: boolean = false;
  private abortController: AbortController[] = [];

  ngOnInit(): void {
    this.setToolTip("", false);
    if (this.parentListItems.length <= 0){
      this.db.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, new AbortController().signal).then(res => {
        this.pageIndex++;
        this.listItems = res.users.map(value => new SelectorItem(UserComponent, value));
        this.selectorItemsLoaded.next(this.listItems);
      });
    }

  }

  onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, new AbortController().signal).then((res : {users:  any[], count : number}) => {
          this.listItems.push(...res.users.map(value => new SelectorItem(UserComponent, value)));
          this.selectorItemsLoaded.next(this.listItems);
          if (res.count <= 0){
            this.pagesComplete = true;
          }
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }


  onSearchInput($event: string) {
    this.searchText = $event;
    this.pageIndex = 0;
    this.pagesComplete = false;
    if (this.isSearching) {
      for (let controller of this.abortController) {
        controller.abort("newer request ahead");
      }
    }
    this.isSearching = true;
    let abort = new AbortController();
    this.abortController.push(abort);
    this.db.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, abort.signal).then(res => {
      this.pageIndex++;
      this.listItems = res.users.map(value => new SelectorItem(UserComponent, value));
      this.selectorItemsLoaded.next(this.listItems);
    }).finally(() => this.isSearching = false);
  }

  onFilterClick($event: { accType: string; sort: string }) {
    this.selectedFilter = $event;
    this.pageIndex = 0;
    this.pagesComplete = false;
    if (this.isSearching) {
      for (let controller of this.abortController) {
        controller.abort("newer request ahead");
      }
    }
    this.isSearching = true;
    let abort = new AbortController();
    this.abortController.push(abort);
    this.db.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, abort.signal).then(res => {
      this.pageIndex++;
      this.listItems = res.users.map(value => new SelectorItem(UserComponent, value));
      this.selectorItemsLoaded.next(this.listItems);
    }).finally(() => this.isSearching = false);
  }

  onItemClick($event: DbObject) {
    console.log($event);
  }
}
