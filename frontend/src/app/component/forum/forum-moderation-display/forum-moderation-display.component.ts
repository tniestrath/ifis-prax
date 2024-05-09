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
    var style = "width: 15%; display: inline-flex; align-items: center; justify-content: center; border-radius: 5px; background-color: ";
    this.setToolTip(
 "<span>Hier können Beiträge aus dem Forum Moderiert werden</span>" +
      "<br><br>" +
      "<div style=\"" + style + DashColors.RED  + "\"><span>Löschen</span></div><span> : Der Beitrag wird aus der Liste entfernt und nicht im Forum angezeigt</span><br>" +
      "<div style=\"" + style + DashColors.GREEN  + "\"><span>Freigeben</span></div><span> : Der Beitrag wird aus der Liste entfernt und im Forum angezeigt</span><br>" +
      "<div style=\""+ style + DashColors.DARK_GREY + "\"><span>➜</span></div><span> : Der Beitrag wird zurück in die Liste geschoben</span>"
      , 3)
  }

  onDeleteClick = () : void => {};
  onAcceptClick = () : void => {};
  onStackClick = () : void => {};

  protected readonly DashColors = DashColors;
}
