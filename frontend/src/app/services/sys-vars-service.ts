import {EventEmitter, Injectable} from '@angular/core';

import {User} from "../component/user/user";
import {Post} from "../component/post/Post";
import {
  SearchItem
} from "../component/search/search-no-results-list/search-list-item/search-list-item.component";
import {DbObject} from "./DbObject";
import {Newsletter} from "../component/newsletter/Newsletter";
import {Tag} from "../component/tag/Tag";
import {ForumPost} from "../component/forum/forum-moderation-list/ForumPost";

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
  public static CURRENT_PAGE : string = "landing";

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

  public static SELECTED_FORUM_POST : EventEmitter<ForumPost> = new EventEmitter<ForumPost>();

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
