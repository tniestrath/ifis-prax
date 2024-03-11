import { Component } from '@angular/core';
import {DbObject} from "../../../../services/DbObject";
import Util from "../../../../util/Util";
import {SysVars} from "../../../../services/sys-vars-service";


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
    this.city = city;
  }

}
export class SearchRank extends DbObject{
  public rank : number;
  public query : string;
  public searchedCount : number;
  constructor(id : string, query : string, searchedCount : number, rank : number) {
    super(id, query);
    this.query = query;
    this.searchedCount = searchedCount;
    this.rank = rank;
  }
}
export class SearchSS extends DbObject{
  public rank : number;
  public query : string;
  public sSCount : number;
  constructor(id : string, query : string, sSCount : number, rank : number) {
    super(id, query);
    this.query = query;
    this.sSCount = sSCount;
    this.rank = rank;
  }
}


@Component({
  selector: 'dash-search-list-item',
  templateUrl: './search-list-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListItemComponent {
  data : DbObject = new DbObject("","");

  protected onClick(){
    SysVars.SELECTED_SEARCH.emit({item: this.data, operation: "IGNORE"});
  }

  protected readonly SearchItem = SearchItem;
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
  selector: 'dash-search-rank-list-item',
  templateUrl: './search-list-rank-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListRankItemComponent extends SearchListItemComponent{
  override data : SearchRank = new SearchRank("", "", 0, 0);
}
@Component({
  selector: 'dash-search-ss-list-item',
  templateUrl: './search-list-ss-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListSSItemComponent extends SearchListItemComponent{
  override data : SearchSS = new SearchSS("", "", 0, 0);
}

@Component({
  selector: 'dash-search-anbieter-list-item',
  templateUrl: './search-list-anbieter-item.component.html',
  styleUrls: ['./search-list-item.component.css']
})
export class SearchListAnbieterItemComponent {
  data : SearchAnbieterItem = new SearchAnbieterItem("","", "", 0);

  protected onClick(){
    SysVars.SELECTED_SEARCH.emit({item: this.data, operation: "IGNORE"});
  }

  protected readonly SearchItem = SearchItem;
}
