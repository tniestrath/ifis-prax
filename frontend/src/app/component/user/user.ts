import {DbObject} from "../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";
import {Component, Input, OnInit} from "@angular/core";
import {SelectableComponent} from "../../page/selector/selectable.component";
import Util from "../../util/Util";

export class User extends DbObject {
  constructor(public override id: string = "",
              public email : string = "undefined",
              public displayName : string = "undefined",
              public niceName : string = "undefined",
              public profileViews : number = 0,
              public postViews : number = 0,
              public postCount : number = 0,
              public tendency : boolean | null = null,
              public viewsPerDay : number = 0,
              public performance : number = 0,
              public accountType : string = "undefined", // basic plus premium admin ?customer?
              public potential : number = 0,
              public TeleDE : boolean = false,
              public TeleEU : boolean = false,
              public category : string = "none",
              public employees : string = "",
              public redirects : number = 0,
              public rankingContent : number = -1,
              public rankingContentByGroup : number = -1,
              public rankingProfile : number = -1,
              public rankingProfileByGroup : number = -1,
              public slogan : string = " - ",
              public service: string = "",
              public tel: string = "",
              public creationDate: string = "",
              public img : string = "") {
    super(id, displayName);
  }
}

@Component({
  selector: 'chip-user-plan',
  template: '<div [style.background-color]="Util.getPlanColor(plan)" class="chip-user-plan-box">{{user || Util.firstToUpperCase(plan)}}</div>',
  styles: ["chip-user-plan { padding: 5px}",".chip-user-plan-box { padding: 2px; border: 1px solid #00000000;color: #FFFFFF; width: 100%; text-align: center; border-radius: 5px; font-size: 100%}"]
})
export class UserPlanChip extends Component {
  @Input() plan : string = "none";
  @Input() user : string | undefined;

  protected readonly Util = Util;
}
