import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SelectableComponent} from "../../selector/selectable.component";
import {DbObject} from "../../../services/DbObject";
import {SafeUrl} from "@angular/platform-browser";
import {DbService} from "../../../services/db.service";

export class User extends DbObject{
  constructor(public override id : string, public email : string, public displayName : string, public accountType: string, public postCount: number, public potential : number, public img : SafeUrl) {
    super(id, displayName);
  }
}

@Component({
  selector: 'dash-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements SelectableComponent, OnInit {
  @Input() data: User = new User("", "", "", "", 0, 50, "");

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
      return "C:/Users/Robin/IdeaProjects/ifis-prax/frontend/src/assets/ifismpdashboard.png";
    }
  }

}
