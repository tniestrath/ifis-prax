import { Component } from '@angular/core';
import {DbObject} from "../../../../services/DbObject";
import {SysVars} from "../../../../services/sys-vars-service";
import Util from "../../../../util/Util";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";


export class SearchItem extends DbObject{
  public search : string;
  public count : number;
  constructor(id : string, searchString : string, count : number) {
    super(id, searchString);
    this.search = searchString;
    this.count = count;
  }
}
export class SearchAnbieterItem extends SearchItem{
  public city : string;

  constructor(id : string, searchString : string, city : string, count : number) {
    super(id, searchString, count);
    this.search = searchString;
    this.city = city;
  }

}
export class SearchSS extends DbObject{
  public rank : number;
  public query : string;
  public foundCount : number;
  public searchedCount : number;
  public sSCount : number;
  constructor(id : string, query : string, sSCount : number, foundCount : number, searchedCount : number, rank : number) {
    super(id, query);
    this.query = query;
    this.sSCount = sSCount;
    this.foundCount = foundCount;
    this.searchedCount = searchedCount;
    this.rank = rank;
  }
}


@Component({
  selector: 'dash-search-list-item',
  templateUrl: './search-list-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListItemComponent extends DashListItemComponent{
  override data : DbObject = new DbObject("","");
  public Util = Util;

  override onClick(){
    SysVars.SELECTED_SEARCH.emit({item: this.data, operation: "IGNORE"});
  }

  protected readonly SearchSS = SearchSS;
}
@Component({
  selector: 'dash-search-no-results-list-item',
  templateUrl: './search-list-no-results-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListNoResultsItemComponent extends SearchListItemComponent{
  override data : SearchItem = new SearchItem("", "", 0);
}
@Component({
  selector: 'dash-search-ss-list-item',
  templateUrl: './search-list-ss-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListSSItemComponent extends SearchListItemComponent{
  override data : SearchSS = new SearchSS("", "", 0, 0, 0,0);
}

@Component({
  selector: 'dash-search-anbieter-list-item',
  templateUrl: './search-list-anbieter-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListAnbieterItemComponent extends SearchListItemComponent{
  override data : SearchAnbieterItem = new SearchAnbieterItem("","", "", 0);

  override onClick(){
    SysVars.SELECTED_SEARCH.emit({item: this.data, operation: "DELETE"});
  }
}
