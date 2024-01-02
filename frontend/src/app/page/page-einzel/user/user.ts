import {DbObject} from "../../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";

export class User extends DbObject {
  constructor(public override id: string,
              public email: string,
              public displayName: string,
              public profileViews: number,
              public postViews: number,
              public postCount: number,
              public tendency: boolean | null,
              public viewsPerDay: number,
              public performance: number,
              public accountType: string, // basic plus premium admin ?customer?
              public potential: number,

              public img: string) {
    super(id, displayName);
  }
}
