import {Host, Injectable} from '@angular/core';
import {Tag, WPTerm} from "../tag/Tag";
import {Company} from "../company/Company";

@Injectable({
  providedIn: 'root'
})
export class DbService {

  private static host = "http://localhost"
  private static port = ":8080";


  private static getAllTags  = "/terms/getPostTagsIdName";


  public static Tags : Tag[] = [];
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

}
