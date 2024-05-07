import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {ForumPost} from "../forum-moderation-list/ForumPost";
import {SysVars} from "../../../services/sys-vars-service";
import {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-forum-moderation-display',
  templateUrl: './forum-moderation-display.component.html',
  styleUrls: ['./forum-moderation-display.component.css', "/../../dash-base/dash-base.component.css"]
})
export class ForumModerationDisplayComponent extends DashBaseComponent implements OnInit{
  data : ForumPost = new ForumPost();
  ngOnInit(): void {
    SysVars.SELECTED_FORUM_POST.subscribe(value => {
      console.log(value)
      if (value != false) {
        this.data = value as ForumPost
        SysVars.BUFFERED_FORUM_POST = value as ForumPost;
      }
      else {
        console.log(SysVars.CURRENT_FORUM_POST)
        this.data = SysVars.CURRENT_FORUM_POST;
      }
    });


  }

  onDeleteClick(){
    this.api.deleteForumPost(this.data.id).then(value => {
      if(value){
        this.data = new ForumPost();
      }
    });
  }
  onAcceptClick(){
    this.api.acceptForumPost(this.data.id).then(value => {
      if (value) {
        this.data = new ForumPost();
      }
    });
  }
  onStackClick(){
    //SysVars.SELECTED_FORUM_POST.next(false);
  }

  protected readonly DashColors = DashColors;
}
