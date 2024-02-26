import { Component } from '@angular/core';
import {DbObject} from "../../../../services/DbObject";
import Util from "../../../../util/Util";
import {SysVars} from "../../../../services/sys-vars-service";


export class SearchItem extends DbObject{
  public search : string;
  public count : number;
  constructor(searchString : string, count : number) {
    super(searchString, searchString);
    this.search = searchString;
    this.count = count;
  }
}

@Component({
  selector: 'dash-search-no-results-list-item',
  templateUrl: './search-no-results-list-item.component.html',
  styleUrls: ['./search-no-results-list-item.component.css']
})
export class SearchNoResultsListItemComponent {
  data : SearchItem = new SearchItem("", 0);

  protected onClick(){
    SysVars.SELECTED_SEARCH.emit({item: this.data, operation: "IGNORE"});
  }
}
