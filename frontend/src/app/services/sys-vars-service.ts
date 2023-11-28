import {EventEmitter, Injectable} from '@angular/core';

import {User} from "../page/page-einzel/user/user";
import {TagRanking} from "../component/tag/Tag";
import {PAGE_DOWN} from "@angular/cdk/keycodes";

export class PAGE {
  id: number = 0;
  eng: string = "Login";
  de: string = "Login";

  constructor(id: number, eng: string, de: string) {
    this.id = id;
    this.eng = eng;
    this.de = de;
  }
}

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

  public static PAGES = [
    //new PAGE(0, "Login", "Login");
    new PAGE(1, "Overview", "Übersicht"),
    new PAGE(2, "Posts", "Beiträge"),
    new PAGE(3, "Tags", "Themen"),
    new PAGE(4, "Users", "Anbieter"),
    new PAGE(5, "Inhalte", "Content"),
  ]

  constructor() {}
}
