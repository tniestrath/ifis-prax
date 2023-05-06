import {Host, Injectable} from '@angular/core';
import {Tag, WPTerm} from "../tag/Tag";
import {Company} from "../company/Company";
import {User} from "../user/user/user.component";

@Injectable({
  providedIn: 'root'
})
export class DbService {

  private static host = "http://localhost"
  private static port = ":8080";


  private static getAllTags  = "/terms/getPostTagsIdName";
  private static getTagPostCount = "/terms/getPostcount?id="
  private static getTagRanking = "/terms/getTermRanking";
  private static getAllUsers = "/users/getAll";


  public static Tags : Tag[] = [];
  public static Users : User[] = [];
  public static Companies : Company[] = [];

  constructor() { }

  private static getUrl( prompt : string){
    return DbService.host + DbService.port + prompt;
  }

  async loadAllTags(){
    if (DbService.Tags.length > 0){
      return;
    }
    await fetch(DbService.getUrl(DbService.getAllTags)).then(res => res.json()).then(res => {
      for (let tag of res) {
        DbService.Tags.push(tag);
      }
    });
  }

  async getTagPostCount(id : string){
    return await fetch(DbService.getUrl(DbService.getTagPostCount + id)).then(res => res.json());
  }

  async getTagRanking() {
    return await fetch(DbService.getUrl(DbService.getTagRanking)).then(res => res.json());
  }

  async loadAllUsers() {
    if (DbService.Users.length > 0){
      return;
    }
    await fetch(DbService.getUrl(DbService.getAllUsers)).then(res => res.json()).then(res => {
      for (let user of res) {
        DbService.Users.push(user);
      }
    });
  }
}
