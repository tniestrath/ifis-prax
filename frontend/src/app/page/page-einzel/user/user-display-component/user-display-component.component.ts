import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../../../component/dash-base/dash-base.component";
import Util from "../../../../util/Util";
import {User} from "../user";
import {SysVars} from "../../../../services/sys-vars-service";

@Component({
  selector: 'dash-user-display-component',
  templateUrl: './user-display-component.component.html',
  styleUrls: ['./user-display-component.component.css']
})
export class UserDisplayComponentComponent extends DashBaseComponent implements OnInit{
  user : User = new User();

  ngOnInit(): void {
    this.setToolTip("",false);
    this.db.getUserAllStatsById(SysVars.USER_ID).then(res => {
      this.user = res;
    });
    this.db.getUserRankings(SysVars.USER_ID).then(res => {
      this.user.rankingContent = res.rankingContent;
      this.user.rankingContentByGroup = res.rankingContentByGroup;
      this.user.rankingProfile = res.rankingProfile;
      this.user.rankingProfileByGroup = res.rankingProfileByGroup;
      this.cdr.detectChanges();

    });
  }

  protected readonly Util = Util;
  protected readonly Math = Math;
  protected readonly parseFloat = parseFloat;
}
