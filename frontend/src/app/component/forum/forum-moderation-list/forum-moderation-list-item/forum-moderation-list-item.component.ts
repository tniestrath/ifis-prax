import { Component } from '@angular/core';
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";
import {ForumPost} from "../ForumPost";
import {SysVars} from "../../../../services/sys-vars-service";

@Component({
  selector: 'dash-forum-moderation-list-item',
  templateUrl: './forum-moderation-list-item.component.html',
  styleUrls: ['./forum-moderation-list-item.component.css']
})
export class ForumModerationListItemComponent extends DashListItemComponent{
  override data : ForumPost = new ForumPost();

  override onClick(data: any): any {
    SysVars.SELECTED_FORUM_POST.next(data);
  }
}