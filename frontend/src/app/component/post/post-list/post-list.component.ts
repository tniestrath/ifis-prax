import {AfterViewInit, Component} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {PostComponent} from "../post.component";
import {DbObject} from "../../../services/DbObject";
import {PostListItemComponent} from "./post-list-item/post-list-item.component";
import {Post} from "../Post";

@Component({
  selector: 'dash-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostListComponent extends DashBaseComponent implements AfterViewInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  search_input : any;
  type_input_article : any;
  type_input_blog : any;
  type_input_news : any;

  ngOnInit(): void {
    this.setToolTip("Auflistung aller Posts, sie können nach den Beitrags-Typen filtern");
    //@ts-ignore
    this.db.getUserPostsWithStats("1").then( (value : Post[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    })
  }



  ngAfterViewInit(): void {

  }

}
