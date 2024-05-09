import { Component } from '@angular/core';
import {DashListComponent} from "../../dash-list/dash-list.component";
import {ForumModerationListItemComponent} from "./forum-moderation-list-item/forum-moderation-list-item.component";
import {ForumPost} from "./ForumPost";
import {SysVars} from "../../../services/sys-vars-service";
import {SelectorItem} from "../../../page/selector/selector.component";


@Component({
  selector: 'dash-forum-moderation-list',
  templateUrl: './forum-moderation-list.component.html',
  styleUrls: ['./forum-moderation-list.component.css', "/../../dash-base/dash-base.component.css"]
})
export class ForumModerationListComponent extends DashListComponent<ForumPost, ForumModerationListItemComponent>{
  override ngOnInit() {
    this.setToolTip("Liste mit unmoderierten Beiträgen", 2)
  }

  override onScrollEnd() {
  }
}
