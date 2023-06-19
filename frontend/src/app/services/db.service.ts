import {Injectable} from '@angular/core';
import {User} from "../page/page-einzel/user/user.component";
import {DomSanitizer} from "@angular/platform-browser";
import {Tag} from "../page/tag/Tag";
import {DbObject} from "./DbObject";
import {Post} from "../Post";

export enum dbUrl {
  HOST = "http://localhost",
  PORT = ":8080",
  GET_ALL_TAGS = "http://localhost:8080/terms/getPostTagsIdName",
  GET_ALL_TAGS_WITH_COUNT_AND_RELEVANCE = "http://localhost:8080/stats/allTermsRelevanceAndCount",
  GET_TAG_POST_COUNT = "http://localhost:8080/terms/getPostcount?id=",
  GET_TAG_RANKING = "http://localhost:8080/terms/getTermRanking",
  GET_ALL_USERS = "http://localhost:8080/users/getAllNew",
  GET_USER_POST_PER_DAY = "http://localhost:8080/getPostsByAuthorLine?id=",
  GET_USER_POSTS_WITH_STATS = "http://localhost:8080/getPostsByAuthorLine2?id=",
  GET_USER_NEWEST_POST_WITH_STATS = "http://localhost:8080/getNewestPostWithStatsByAuthor?id=",
  GET_USER_IMG = "http://localhost:8080/users/profilePic?id=",
  GET_USER_BY_LOGIN = "http://localhost:8080/users?login=",
  GET_USER_BY_EMAIL = "http://localhost:8080/users?email=",
  GET_USER_CLICKS = "http://localhost:8080/stats/getViewsBrokenDown?id=",
  GET_USER_BEST_POST = "http://localhost:8080/stats/bestPost?id=",
  GET_POST = "http://localhost:8080/getPostWithStatsById?id=",
  GET_POST_PERFORMANCE = "http://localhost:8080/stats/getPerformanceByArtId?id=",
  GET_POST_MAX_PERFORMANCE = "http://localhost:8080/stats/maxPerformance",
  GET_POST_MAX_RELEVANCE = "http://localhost:8080/stats/maxRelevance"
}

@Injectable({
  providedIn: 'root'
})
export class DbService {

  private static host = dbUrl.HOST;
  private static port = dbUrl.PORT;

  public static Tags : Tag[] = [];
  public static Users : User[] = [];

  constructor(private sanitizer : DomSanitizer) { }

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
  async getAllTagsWithCountAndRelevance(){
    return await fetch(dbUrl.GET_ALL_TAGS_WITH_COUNT_AND_RELEVANCE).then(res => res.json());
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
  async getUserPostsWithStats(id : string){
    return await fetch(dbUrl.GET_USER_POSTS_WITH_STATS + id).then(res => res.json());
  }

  async getUserImgSrc(id : string){
    const blob = await fetch(dbUrl.GET_USER_IMG + id).then(res => res.blob());
    if (blob.size == 0){
      return "../../assets/user_img/404_img.jpg";
    }
    const reader = new FileReader();
    reader.readAsDataURL(blob);
    return new Promise<string>((resolve, reject) => {
      reader.onloadend = () => {
        if (typeof reader.result === 'string') {
          resolve(reader.result);
        } else {
          reject(new Error('Failed to create data URL from blob'));
        }
      };
      reader.onerror = () => {
        reject(new Error('Failed to read blob data'));
      };
    }).then(dataUrl => this.sanitizer.bypassSecurityTrustUrl(dataUrl));
  }

  async getUserByLogin(login : string){
    return fetch(dbUrl.GET_USER_BY_LOGIN + login).then(res => res.json());
  }
  async getUserByEmail(email : string){
    return fetch(dbUrl.GET_USER_BY_EMAIL + email).then(res => res.json());
  }

  async getUserClicks(id : string){
    return fetch(dbUrl.GET_USER_CLICKS + id).then(res => res.json());
  }

  async getUserBestPost(id: string, type: string){
    return fetch(dbUrl.GET_USER_BEST_POST + id + "&type=" + type).then(res => res.json()).catch(reason => {return new Post()});
  }

  async getUserNewestPost(id: string): Promise<Post> {
    return fetch(dbUrl.GET_USER_NEWEST_POST_WITH_STATS + id).then(res => res.json());
  }

  async getMaxPerformance(){
    let max : Promise<number> = await fetch(dbUrl.GET_POST_MAX_PERFORMANCE).then(res => res.json());
    return max;
  }
  async getMaxRelevance(){
    let max : Promise<number> = await fetch(dbUrl.GET_POST_MAX_RELEVANCE).then(res => res.json());
    return max;
  }


  static sortAlphanumeric(input : DbObject[]){
    input.sort((a, b) => {
      return a.name.toLowerCase()
        .replace("ü", "ue")
        .replace("ä", "ae")
        .replace("ö", "oe")
        .replace(/[\W_]+/g,"")
        .localeCompare(b.name.toLowerCase());
    });
  }

  async getPostById(id: number) : Promise<Post> {
    return await fetch(dbUrl.GET_POST + id).then(res => res.json());
  }
}
