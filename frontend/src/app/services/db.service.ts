import {Injectable} from '@angular/core';
import {Tag} from "../tag/Tag";
import {User} from "../user/user/user.component";

export enum dbUrl {
  HOST = "http://localhost",
  PORT = ":8080",
  GET_ALL_TAGS = "http://localhost:8080/terms/getPostTagsIdName",
  GET_TAG_POST_COUNT = "http://localhost:8080/terms/getPostcount?id=",
  GET_TAG_RANKING = "http://localhost:8080/terms/getTermRanking",
  GET_ALL_USERS = "http://localhost:8080/users/getAll",
  GET_USER_POST_PER_DAY = "http://localhost:8080/getPostsByAuthorLine?id="
}

@Injectable({
  providedIn: 'root'
})
export class DbService {

  private static host = dbUrl.HOST;
  private static port = dbUrl.PORT;

  public static Tags : Tag[] = [];
  public static Users : User[] = [];

  constructor() { }

  private static getUrl( prompt : string){
    return DbService.host + DbService.port + prompt;
  }

  async loadAllTags(){
    if (DbService.Tags.length > 0){
      return;
    }
    await fetch(dbUrl.GET_ALL_TAGS).then(res => res.json()).then(res => {
      for (let tag of res) {
        DbService.Tags.push(tag);
      }
    });
  }

  async getTagPostCount(id : string){
    return await fetch(dbUrl.GET_TAG_POST_COUNT + id).then(res => res.json());
  }

  async getTagRanking() {
    return await fetch(dbUrl.GET_TAG_RANKING).then(res => res.json());
  }

  async loadAllUsers() {
    if (DbService.Users.length > 0){
      return;
    }
    await fetch(dbUrl.GET_ALL_USERS).then(res => res.json()).then(res => {
      for (let user of res) {
        DbService.Users.push(user);
      }
    });
  }

  async getUserPostsDay(id : string){
    return await fetch(dbUrl.GET_USER_POST_PER_DAY + id).then(res => res.json());
  }
}
