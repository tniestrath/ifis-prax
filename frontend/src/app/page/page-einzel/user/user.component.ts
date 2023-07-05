import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {SelectableComponent} from "../../selector/selectable.component";
import {SafeUrl} from "@angular/platform-browser";
import {DbService} from "../../../services/db.service";
import {User} from "./user";

@Component({
  selector: 'dash-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements SelectableComponent, OnInit {
  @Input() data: User = new User("", "", "", "", 0, 50, 66, 0, "");

  @Input() clicked: EventEmitter<User> = new EventEmitter<User>();

  user_img: SafeUrl = "";

  constructor(private db: DbService) {
  }

  onClick(): void {
    this.clicked?.emit(this.data);
  }

  ngOnInit(): void {
    this.db.getUserImgSrc(this.data.id).then(dataUrl => {
      this.user_img = dataUrl;
    });
  }

  getUserImg() {
    return this.user_img;
  }

}
