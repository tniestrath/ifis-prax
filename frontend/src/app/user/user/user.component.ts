import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SelectableComponent} from "../selector/selectable.component";
import {DbObject} from "../../services/DbObject";

export class User implements DbObject{
  constructor(public id : string, public email : string, public displayName : string, public img_src : string) {}
  name: string = this.displayName;

}

@Component({
  selector: 'dash-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements SelectableComponent{
  @Input() data: User = new User("", "", "", "");

  @Input() clicked : EventEmitter<User> = new EventEmitter<User>();

  onClick(): void {
    this.clicked?.emit(this.data);
  }


}
