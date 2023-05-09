import {Component, OnInit} from '@angular/core';
import {Tag} from "../Tag";
import {TagComponent} from "../tag/tag.component";
import {Subject} from "rxjs";
import {DbService} from "../../../services/db.service";
import {SelectorItem} from "../../selector/selector.component";

@Component({
  selector: 'dash-page-tag',
  templateUrl: './page-tag.component.html',
  styleUrls: ['./page-tag.component.css']
})
export class PageTagComponent implements OnInit{
  displayContent: string = "none";

  postCount: string = "0";
  searchValue = "";
  selectorItems: SelectorItem[] = [];
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  constructor(private db: DbService) {
  }
  onSelected(id : string, name : string){
    if (id != "0"){
      this.displayContent = "grid";
      this.db.getTagPostCount(id).then(res => this.postCount = res);
    } else {
      this.displayContent = "none";
    }
  }

  ngOnInit(): void {
    this.loadSelector();
  }

  onSearchInput(value: string) {
    this.searchValue = value;
    this.loadSelector();
  }

  private loadSelector() {
    this.db.loadAllTags().then(() => {
      this.selectorItems = [];
      for (let t of DbService.Tags) {
        this.selectorItems.push(new SelectorItem(TagComponent, new Tag(t.id, t.name, t.img_src)));
      }
    }).then(() => {
      this.selectorItems = this.selectorItems.filter(item => item.data.name.toUpperCase().includes(this.searchValue.toUpperCase()))
    }).finally(() =>
      this.selectorItemsLoaded.next(this.selectorItems));
  }
}
