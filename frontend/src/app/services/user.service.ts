import {EventEmitter, Injectable} from '@angular/core';

import {User} from "../page/page-einzel/user/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public static USER_ID : string = "0";
  public static ADMIN : boolean = false;
  public static SELECTED_POST_ID : EventEmitter<number> = new EventEmitter<number>();
  public static login : EventEmitter<User> = new EventEmitter<User>();
  constructor() {}
}
