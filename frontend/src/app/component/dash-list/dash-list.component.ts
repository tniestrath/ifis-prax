import {Component, OnInit, Type} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SelectorItem} from "../../page/selector/selector.component";
import {Subject} from "rxjs";
import {Post} from "../post/Post";
import {PostListItemComponent} from "../post/post-list/post-list-item/post-list-item.component";
import {DbObject} from "../../services/DbObject";
import {DashListItemComponent} from "./dash-list-item/dash-list-item.component";

@Component({
  selector: 'dash-dash-list',
  templateUrl: './dash-list.component.html',
  styleUrls: ['./dash-list.component.css', "../dash-base/dash-base.component.css"]
})
export class DashListComponent<T extends DbObject, C extends DashListItemComponent> extends DashBaseComponent{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  pagesComplete: boolean = false;
  lastScroll = 0;

  private response : Promise<T[]> | undefined;
  private component : Type<C> | undefined;

  ngOnInit(): void {
  }

  load(response : Promise<T[]>, component : Type<C>): void {
    this.response = response;
    this.component = component;
    response.then((value : T[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(component, valueElement));
      }
      this.pagesComplete = value.length <= 0;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
  reload(response : Promise<T[]>, component : Type<C>): void {
    this.response = response;
    this.component = component;
    this.selectorItems = [];
    response.then((value : T[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(component, valueElement));
      }
      this.pagesComplete = value.length <= 0;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

  onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        // @ts-ignore
        this.load(this.response, this.component);
      }
      else {}
      this.lastScroll = scroll;
    }
  }
}

@Component({
  selector: 'dash-dash-list',
  templateUrl: './dash-list.component.html',
  styleUrls: ['./dash-list.component.css', "../dash-base/dash-base.component.css"]
})
export class DashListPageableComponent<T extends DbObject, C extends DashListItemComponent> extends DashListComponent<any, any>{
  pageIndex = 0;
  pageSize = 20;


  override load<T extends DbObject, C extends DashListItemComponent>(response : Promise<T[]> ,component : Type<C>): void {
    response.then((value : T[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(component, valueElement));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
  override reload<T extends DbObject, C extends DashListItemComponent>(response : Promise<T[]> ,component : Type<C>): void {
    this.selectorItems = [];
    response.then((value : T[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(component, valueElement));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        // @ts-ignore
        this.load(this.response, this.component);
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }
}
