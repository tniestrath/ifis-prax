import {DbObject} from "../../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";

export class User extends DbObject {
  constructor(public override id: string = "",
              public email: string = "",
              public displayName: string = "",
              public profileViews: number = 0,
              public postViews: number = 0,
              public postCount: number = 0,
              public tendency: boolean | null = null,
              public viewsPerDay: number = 0,
              public performance: number = 0,
              public accountType: string = "", // basic plus premium admin ?customer?
              public potential: number = 0,
              public TeleDE: boolean = false,
              public TeleEU: boolean = false,
              public category: string = "none",
              public employees: string = "",
              public redirects: number = 0,
              public img: string = "") {
    super(id, displayName);
  }
}
