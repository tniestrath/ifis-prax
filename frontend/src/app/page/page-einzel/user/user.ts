import {DbObject} from "../../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";

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
              public img : string = "") {
    super(id, displayName);
  }
}
