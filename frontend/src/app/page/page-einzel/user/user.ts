import {DbObject} from "../../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";

export class User extends DbObject {
  constructor(public override id: string,
              public email: string,
              public displayName: string,
              public accountType: string, // basic plus premium admin ?customer?
              public postCount: number,
              public potential: number,
              public performance: number,
              public postViews: number,
              public img: SafeUrl) {
    super(id, displayName);
  }
}
