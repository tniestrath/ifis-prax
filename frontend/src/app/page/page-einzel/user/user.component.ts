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
  @Input() data: User = new User("", "", "", 0, 0, 0, 50, "", 0, "");

  user_img: SafeUrl = "";
  bgColor: string = "0";
  performance_image_id: string = "33";

  constructor(private db: DbService) {
  }

  onClick(): void {
    SysVars.SELECTED_USER_ID.emit(Number(this.data.id));
  }

  ngOnInit(): void {
    if (this.data.performance <= 33) this.performance_image_id = "33";
    else if (this.data.performance > 33 && this.data.performance <= 66) this.performance_image_id = "66";
    else if (this.data.performance > 66) this.performance_image_id = "100";

    this.db.getUserImgSrc(this.data.id).then(dataUrl => {
      this.user_img = dataUrl;
    });
  }

  getUserImg() {
    return this.user_img;
  }

  protected readonly Util = Util;
}
