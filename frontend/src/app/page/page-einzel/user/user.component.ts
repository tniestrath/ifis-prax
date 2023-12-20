import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {SelectableComponent} from "../../selector/selectable.component";
import {SafeUrl} from "@angular/platform-browser";
import {DbService} from "../../../services/db.service";
import {User} from "./user";
import Util from "../../../util/Util";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements SelectableComponent, OnInit {
  @Input() data: User = new User("", "", "", 0, 0, 0, null, 0, 50, "", 0, "");

  user_img: SafeUrl = "";
  bgColor: string = "0";

  tendency: string = "stagniert";

  constructor(private db: DbService) {
  }

  onClick(): void {
    SysVars.SELECTED_USER_ID.emit(Number(this.data.id));
  }

  ngOnInit(): void {
    if (this.data.tendency == null) this.tendency = "stagniert"
    else if (!this.data.tendency) this.tendency = "fallend"
    else if (this.data.tendency) this.tendency = "steigend";

    this.db.getUserImgSrc(this.data.id).then(dataUrl => {
      this.user_img = dataUrl;
    });
  }

  getUserImg() {
    return this.user_img;
  }

  protected readonly Util = Util;
  protected readonly parseFloat = parseFloat;
}
