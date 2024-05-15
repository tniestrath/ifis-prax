import { Component } from '@angular/core';
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";
import {ForumPost} from "../ForumPost";
import {SysVars} from "../../../../services/sys-vars-service";
import {DashColors} from "../../../../util/Util";

@Component({
  selector: 'dash-forum-moderation-list-item',
  templateUrl: './forum-moderation-list-item.component.html',
  styleUrls: ['./forum-moderation-list-item.component.css']
})
export class ForumModerationListItemComponent extends DashListItemComponent{
  override data : ForumPost = new ForumPost();

  public regex = new RegExp("<[^>]*>", "gm");

  override onClick(data: any): any {
    SysVars.SELECTED_FORUM_POST.next(data);
  }
  getRatingColor(preRating: string, category: string) {
    if (preRating != "good" && preRating.includes(category)){
      return DashColors.RED;
    } else return "#808080";
  }

  protected readonly DashColors = DashColors;
}
