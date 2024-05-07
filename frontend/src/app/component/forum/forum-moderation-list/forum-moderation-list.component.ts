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
    SysVars.SELECTED_FORUM_POST.subscribe(post => {
        if (SysVars.CURRENT_FORUM_POST) {
          this.selectorItems.push(new SelectorItem(ForumModerationListItemComponent, SysVars.CURRENT_FORUM_POST));
          SysVars.CURRENT_FORUM_POST = SysVars.BUFFERED_FORUM_POST;
        }
        if (post != false) {
          this.selectorItems.splice(this.selectorItems.findIndex(item => {
            return item.data.id == (post as ForumPost).id
          }), 1);
        } else {
          SysVars.CURRENT_FORUM_POST = this.selectorItems.splice(0, 1)[0].data as ForumPost;
        }
        this.selectorItemsLoaded.next(this.selectorItems);
    })

    this.load( this.api.getUnmoderatedForumPosts().then(data  =>{
      // @ts-ignore
      SysVars.SELECTED_FORUM_POST.next(data.at(0));
      // @ts-ignore
      SysVars.CURRENT_FORUM_POST = data.at(0);
      data.splice(0, 1);
      return data;
    }), ForumModerationListItemComponent);
  }

  override onScrollEnd() {
  }
}
