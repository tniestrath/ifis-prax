import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {DashColors} from "../../util/Util";
import {SysVars} from "../../services/sys-vars-service";

export class ProfileState {
  profilePicture : number = 0;
  titlePicture : number = 0;
  bio: number = 0;
  slogan: number = 0;
  tagsCount: number = 0;
  tagsMax: number = 0;
  contactPublic: number = 0;
  contactPublicMax : number = 0;
  contactIntern: number = 0;
  companyDetails: number = 0;
  solutions: number = 0;
  solutionsMax: number = 0;
}
@Component({
  selector: 'dash-polar-chart',
  templateUrl: './profile-completion.component.html',
  styleUrls: ['./profile-completion.component.css', "../dash-base/dash-base.component.css"]
})
export class ProfileCompletionComponent extends DashBaseComponent implements OnInit{

  status : ProfileState = new ProfileState();

  ngOnInit(): void {
    this.setToolTip("", SysVars.CURRENT_PAGE != "PRINT")
    this.db.getUserProfileCompletion(SysVars.USER_ID).then(res => {
      this.status = res;
    });
  }

  protected readonly DashColors = DashColors;

  getColor(item: number, itemMax: number): DashColors {
    if (item >= itemMax) return DashColors.BLUE;
    else return DashColors.RED;
  }

  isComplete(item: number, itemMax: number) {
    if (item >= itemMax) return 1;
    else return 0;
  }
}


