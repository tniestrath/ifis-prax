import {Component, OnInit} from '@angular/core';
import {Post} from "../../Post";
import Util, {DashColors} from "../../../../util/Util";
import {SysVars} from "../../../../services/sys-vars-service";
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";

@Component({
  selector: 'dash-post-list-item',
  templateUrl: './post-list-item.component.html',
  styleUrls: ['./post-list-item.component.css']
})
export class PostListItemComponent extends DashListItemComponent implements OnInit{
  override data: Post = new Post();
  formattedDate = "DATUM FEHLT";
  formattedTags = "KEINE TAGS";
  formattedAuthors = "KEINE AUTOREN";
  typeColor = DashColors.GREY;
  override bgColor: string = "#FFFFFF";

  ngOnInit(): void {
    this.typeColor = Util.getColor("post", this.data.type);
    this.formattedDate = Util.formatDate(this.data.date, true);
    this.formattedTags = this.data.tags?.map(value => value.name).toString().replace("[", "").replace("]", "");
    this.formattedAuthors = this.data.authors?.toString().replace("[", "").replace("]", "");
  }


  override onClick(data: Post) {
    SysVars.SELECTED_POST.next(data);
  }
}
