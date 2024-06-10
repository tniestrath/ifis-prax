import {Component, Input, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Subject} from "rxjs";
import {SelectorItem} from "../../../page/selector/selector.component";
import {UserComponent} from "../user.component";


@Component({
  selector: 'dash-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css', '../../dash-base/dash-base.component.css']
})
export class UserListComponent extends DashBaseComponent implements OnInit{
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  listItems : SelectorItem[] = [];
  @Input() parentListItems : SelectorItem[] = [];
  @Input() parentPageIndex : number = 0;
  pageIndex: number = 0;
  private pagesComplete: boolean = false;
  private lastScroll: number = 0;
  private pageSize: number = 5;

  private searchText: string = "";
  private selectedFilter = {accType : "", usrType : " ", sort : "userId"};
  private isSearching: boolean = false;
  private abortController: AbortController[] = [];

  ngOnInit(): void {
    this.setToolTip("", 1,false);
    if (this.parentListItems.length <= 0){
      this.api.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, new AbortController().signal).then(res => {
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
        let abort = new AbortController();
        this.abortController.push(abort)
        this.api.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, abort.signal).then((res : {users:  any[], count : number}) => {
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
    for (let controller of this.abortController) {
      controller.abort("newer request ahead");
    }
    let abort = new AbortController();
    this.abortController.push(abort);
    this.api.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, abort.signal).then(res => {
      this.pageIndex++;
      this.listItems = res.users.map(value => new SelectorItem(UserComponent, value));
      this.selectorItemsLoaded.next(this.listItems);
      if (res.count <= 0){
        this.pagesComplete = true;
      }
    });
  }

  onFilterClick($event: { accType: string; usrType: string; sort: string }) {
    this.selectedFilter = $event;
    this.pageIndex = 0;
    this.pagesComplete = false;
    for (let controller of this.abortController) {
      controller.abort("newer request ahead");
    }
    let abort = new AbortController();
    this.abortController.push(abort);
    this.api.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, abort.signal).then(res => {
      this.pageIndex++;
      this.listItems = res.users.map(value => new SelectorItem(UserComponent, value));
      this.selectorItemsLoaded.next(this.listItems);
      if (res.count <= 0){
        this.pagesComplete = true;
      }
    });
  }

  onFreeSpace(space: number) {
    if (space >0){
      if (!this.pagesComplete){
        let abort = new AbortController();
        this.abortController.push(abort)
        this.api.getAllUsers(this.pageIndex, this.pageSize, this.searchText, this.selectedFilter, abort.signal).then((res : {users:  any[], count : number}) => {
          this.listItems.push(...res.users.map(value => new SelectorItem(UserComponent, value)));
          this.selectorItemsLoaded.next(this.listItems);
          if (res.count <= 0){
            this.pagesComplete = true;
          }
        });
        this.pageIndex++;
      }
    }
  }
}
