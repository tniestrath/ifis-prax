import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {ForumModerationListComponent} from "../forum-moderation-list/forum-moderation-list.component";
import {ForumModerationDisplayComponent} from "../forum-moderation-display/forum-moderation-display.component";
import {SysVars} from "../../../services/sys-vars-service";
import {SelectorItem} from "../../../page/selector/selector.component";
import {
  ForumModerationListItemComponent
} from "../forum-moderation-list/forum-moderation-list-item/forum-moderation-list-item.component";
import {ForumPost} from "../forum-moderation-list/ForumPost";


@Component({
  selector: 'dash-forum-moderator',
  templateUrl: './forum-moderator.component.html',
  styleUrls: ['./forum-moderator.component.css', "/../../dash-base/dash-base.component.css"]
})
export class ForumModeratorComponent extends DashBaseComponent implements OnInit{

  @ViewChild(ForumModerationListComponent, {static : true}) list!: ForumModerationListComponent;
  @ViewChild(ForumModerationDisplayComponent, {static : true}) display!: ForumModerationDisplayComponent;

  ngOnInit(): void {
    this.setToolTip("", 0,false);

    SysVars.SELECTED_FORUM_POST.subscribe(post => {
      this.list.selectorItems.push(new SelectorItem(ForumModerationListItemComponent, this.display.data));
      let index = this.list.selectorItems.findIndex((item: SelectorItem) => {return item.data.id == (post as ForumPost).id});
      this.display.data = this.displayDataMapping(this.list.selectorItems.splice(index, 1)[0].data as ForumPost);
      this.list.selectorItemsLoaded.next(this.list.selectorItems);
      this.list.g_cdr.detectChanges();
    });
    this.list.load(this.api.getUnmoderatedForumPosts().then(data  =>{
      this.display.data = this.displayDataMapping(data.splice(0, 1)[0]);
      return data;
    }), ForumModerationListItemComponent);

    this.display.onDeleteClick = () => {
      this.api.deleteForumPost(this.display.data.id).then(value => {
        if(value){
          this.display.data = this.displayDataMapping(this.list.selectorItems.splice(0, 1)[0].data as ForumPost);
          this.list.selectorItemsLoaded.next(this.list.selectorItems);
          this.list.g_cdr.detectChanges();
        }
      });
    }
    this.display.onAcceptClick = () => {
      this.api.acceptForumPost(this.display.data.id).then(value => {
        if (value) {
          this.display.data = this.displayDataMapping(this.list.selectorItems.splice(0, 1)[0].data as ForumPost);
          this.list.selectorItemsLoaded.next(this.list.selectorItems);
          this.list.g_cdr.detectChanges();
        }
      });
    }
    this.display.onStackClick = () => {
      this.list.selectorItems.push(new SelectorItem(ForumModerationListItemComponent, this.display.data));
      this.display.data = this.displayDataMapping(this.list.selectorItems.splice(0, 1)[0].data as ForumPost);
      this.list.selectorItemsLoaded.next(this.list.selectorItems);
      this.list.g_cdr.detectChanges();
    }
  }

  private displayDataMapping(data : ForumPost) : ForumPost{
    let quoteRegex = new RegExp("\\[quote.* data-postid=\"(\\d+)\"\]", "gm");
    for(let match of data.body.matchAll(quoteRegex)){
      this.api.getForumPostById(match[1]).then(parent => {
        data.body = data.body.replace(match[0],
          "<div style=\"color: grey; margin-left: 5px\">Zitat von @" + parent.userName + ":</div>" +
          "<div style=\"border: 1px dotted; margin-left: 5px\">");
      });
    }
    let quoteEndRegex = new RegExp("\\[\/quote\]", "gm");
    for (let match of data.body.matchAll(quoteEndRegex)) {
      data.body = data.body.replace(match[0], "</div>");
    }

    return data;
  }

}
