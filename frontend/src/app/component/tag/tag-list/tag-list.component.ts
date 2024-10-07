import {Component} from '@angular/core';
import {TagRanking} from "../Tag";
import {TagListItemComponent} from "./tag-list-item/tag-list-item.component";
import {DashListPageableComponent} from "../../dash-list/dash-list.component";

@Component({
  selector: 'dash-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class TagListComponent extends DashListPageableComponent<TagRanking, TagListItemComponent>{
  search_input : any;
  sorting_input_r : any;
  sorting_input_p : any;

  selected_sorter : string = "";
  selected_search : string = "";

  override ngOnInit(): void {
    this.setToolTip("Auflistung aller #Tags / Themen, sortierbar nach Anzahl der Beiträge oder Views<br><br>" +
      "<img src=\"assets/bell.png\" style=\"height: 15px; filter:invert(1)\"> Abonenten des Themas<br>" +
      "<img src=\"assets/eye.png\" style=\"height: 15px; filter:invert(1)\"> Aufrufe<br>");
    this.selectorItems = [];
    this.pagesComplete = false;
    this.load(this.api.getAllTagsWithStats(this.pageIndex, this.pageSize, this.selected_sorter, this.selected_search), TagListItemComponent);

    this.search_input = document.getElementById("tag-search");
    this.sorting_input_r = document.getElementById("tag-sort-r");
    this.sorting_input_p = document.getElementById("tag-sort-p");

    this.search_input.addEventListener("input", (event : any) => {
      this.selected_search = event.target.value;
      this.reload(this.api.getAllTagsWithStats(0, this.pageSize, this.selected_sorter, this.selected_search), TagListItemComponent);
    })

    this.sorting_input_r.addEventListener("change", () => {
      this.selected_sorter = "count";
      this.reload(this.api.getAllTagsWithStats(0, this.pageSize, this.selected_sorter, this.selected_search), TagListItemComponent);
    });
    this.sorting_input_p.addEventListener("change", () => {
      this.selected_sorter = "viewsTotal"
      this.reload(this.api.getAllTagsWithStats(0, this.pageSize, this.selected_sorter, this.selected_search), TagListItemComponent);
    });
  }

  override onScrollEnd() {
    this.onScrollEndWithPromise(this.api.getAllTagsWithStats(this.pageIndex, this.pageSize, this.selected_sorter, this.selected_search));
  }
}
