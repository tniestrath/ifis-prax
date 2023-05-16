import {AfterViewInit, Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {SelectorItem} from "../../page/selector/selector.component";
import {TagComponent} from "../../page/tag/tag/tag.component";
import {DbObject} from "../../services/DbObject";
import {Subject} from "rxjs";
import {TagRanking} from "../../page/tag/Tag";
import {TagListItemComponent} from "./tag-list-item/tag-list-item.component";

@Component({
  selector: 'dash-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class TagListComponent extends DashBaseComponent implements AfterViewInit{
  selectorItems : SelectorItem[] = [new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("1", "test2", "2", "99")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100")),
                                    new SelectorItem(TagListItemComponent, new TagRanking("0", "test", "1", "100"))];
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {

  }

  ngAfterViewInit(): void {
    this.selectorItemsLoaded.next(this.selectorItems);
  }


}
