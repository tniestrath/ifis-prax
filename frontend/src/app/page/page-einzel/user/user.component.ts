import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SelectableComponent} from "../../selector/selectable.component";
import {DbObject} from "../../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";
import {DbService} from "../../../services/db.service";

export class User extends DbObject{
  constructor(public override id : string,
              public email : string,
              public displayName : string,
              public accountType: string,
              public postCount: number,
              public potential : number,
              public performance: number,
              public postViews: number,
              public img : SafeUrl) {
    super(id, displayName);
  }
}

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
    if (this.user_img){
      return this.user_img;
    } else {
      return "../../assets/user_img/404_img.png";
    }
  }

}
