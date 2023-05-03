import { Injectable } from '@angular/core';
import {Tag} from "../tag/Tag";

@Injectable({
  providedIn: 'root'
})
export class DbService {

  private static getAllTagsUrl = "http://localhost:8080/terms/getPostTags";

  constructor() { }

  async getAllTags() : Promise<Tag[]> {
    return await fetch(DbService.getAllTagsUrl).then(res => res.json());
  }


}
