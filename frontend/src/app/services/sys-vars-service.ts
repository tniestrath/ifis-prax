import {EventEmitter, Injectable} from '@angular/core';

import {User} from "../page/page-einzel/user/user";
import {TagRanking} from "../component/tag/Tag";

@Injectable({
  providedIn: 'root'
})
export class SysVars {

  public static USER_ID : string = "0";
  public static ADMIN : boolean = false;
  public static SELECTED_POST_ID : EventEmitter<number> = new EventEmitter<number>();
  public static login : EventEmitter<User> = new EventEmitter<User>();
  public static CURRENT_PAGE : string = "landing";
  public static SELECTED_TAG : EventEmitter<number> = new EventEmitter<number>();
  public static WELCOME : boolean = true;
  constructor() {}
}
