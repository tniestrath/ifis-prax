import {EventEmitter, Injectable} from '@angular/core';

import {User} from "../component/user/user";
import {Post} from "../component/post/Post";
import {DbObject} from "./DbObject";
import {Newsletter} from "../component/newsletter/Newsletter";
import {Tag} from "../component/tag/Tag";
import {ForumPost} from "../component/forum/forum-moderation-list/ForumPost";
import {Dialog} from "../util/Dialog";
import {ForumStat} from "../component/forum/forum-stats/forum-stats.component";
import {Subject} from "rxjs";

export class PAGE {
  id: number = 0;
  en: string = "Login";
  de: string = "Login";

  constructor(id: number, en: string, de: string) {
    this.id = id;
    this.en = en;
    this.de = de;
  }
}

@Injectable({
  providedIn: 'root'
})
export class SysVars {

  public static USER_ID : string = "0";
  public static ADMIN : boolean = false;
  public static WELCOME : boolean = true;
  public static login : EventEmitter<User> = new EventEmitter<User>();
  public static ACCOUNT : User;
  public static CURRENT_PAGE : string = "landing";
  public static Cookie : string = "";

  public static UPDATING : boolean = false;

  public static SELECTED_PAGE : EventEmitter<string> = new EventEmitter<string>();

  public static SELECTED_POST_ID : EventEmitter<number> = new EventEmitter<number>();
  public static SELECTED_POST : EventEmitter<Post> = new EventEmitter<Post>();
  public static SELECTED_POST_IDS : EventEmitter<string> = new EventEmitter<string>();

  public static SELECTED_TAG : EventEmitter<Tag> = new EventEmitter<Tag>();
  public static CURRENT_TAG : Tag = new Tag("378", "IT-Sicherheit");

  public static SELECTED_USER_ID : EventEmitter<number> = new EventEmitter<number>();

  public static SELECTED_NEWSLETTER : EventEmitter<Newsletter> = new EventEmitter<Newsletter>();

  public static SEO_DATA : EventEmitter<{desktop: {now: number, last: number}, mobile: {now: number, last: number}}> = new EventEmitter<{desktop: {now: number; last: number}; mobile: {now: number; last: number}}>();

  public static SELECTED_SEARCH : EventEmitter<{item: DbObject, operation: string}> = new EventEmitter<{item: DbObject; operation: string}>();

  public static SELECTED_FORUM_POST : Subject<ForumPost> = new Subject<ForumPost>();
  public static SELECTED_FORUM_FILTER : Subject<ForumStat> = new Subject<ForumStat>();
  public static FORUM_UPDATE_STATS : Subject<void> = new Subject<void>();

  public static IS_POPUP : boolean = false;

  public static CREATE_DIALOG : (name : string) => Dialog;
  public static REMOVE_DIALOG : () => void;

  public static PAGES = [
    //new PAGE(0, "Login", "Login");
    new PAGE(1, "Overview", "Übersicht"),
    new PAGE(2, "Posts", "Beiträge"),
    new PAGE(3, "Tags", "Themen"),
    new PAGE(4, "Users", "Anbieter"),
    new PAGE(5, "Content", "Inhalte"),
  ]

  constructor() {}
}
