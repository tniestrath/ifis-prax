import {Injectable} from '@angular/core';
import {User} from "../page/page-einzel/user/user.component";
import {DomSanitizer} from "@angular/platform-browser";
import {Tag} from "../page/tag/Tag";
import {DbObject} from "./DbObject";
import {Post} from "../Post";

export enum dbUrl {
  HOST = "http://localhost",
  PORT = ":8080",
  GET_ALL_TAGS = "/terms/getPostTagsIdName",
  GET_ALL_TAGS_WITH_COUNT_AND_RELEVANCE = "/stats/allTermsRelevanceAndCount",
  GET_TAG_POST_COUNT = "/terms/getPostcount?id=",
  GET_TAG_RANKING = "/terms/getTermRanking",
  GET_ALL_USERS = "/users/getAllNew",
  GET_USER_POST_PER_DAY = "/getPostsByAuthorLine?id=",
  GET_USER_POSTS_WITH_STATS = "/getPostsByAuthorLine2?id=",
  GET_USER_NEWEST_POST_WITH_STATS = "/getNewestPostWithStatsByAuthor?id=",
  GET_USER_IMG = "/users/profilePic?id=",
  GET_USER_BY_LOGIN = "/users?login=",
  GET_USER_BY_EMAIL = "/users?email=",
  GET_USER_CLICKS = "/stats/getViewsBrokenDown?id=",
  GET_USER_BEST_POST = "/stats/bestPost?id=",
  GET_POST = "/getPostWithStatsById?id=",
  GET_POST_PERFORMANCE = "/stats/getPerformanceByArtId?id=",
  GET_POST_MAX_PERFORMANCE = "/stats/maxPerformance",
  GET_POST_MAX_RELEVANCE = "/stats/maxRelevance",
  LOGIN = "http://test.it-sicherheit.de/anmelden"
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

  async login(){
    return await fetch('http://test.it-sicherheit.de/anmelden', {
      method: 'POST',
      headers: {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Content-Type': 'application/x-www-form-urlencoded',
        'Cookie': '_gid=GA1.2.623550920.1687944953; _ga=GA1.1.2145831199.1680522460; _ga_GL2G070SB4=GS1.1.1687944953.3.1.1687944997.0.0.0',

      },
      body: "username-1384=philipAlbers&user_password-1384=KL22n2aKldGMKIxTU3Hg%241Qz&form_id=1384&um_request=&_wpnonce=b01c06ce2a&_wp_http_referer=%2Fanmelden%2F&rememberme=1"
    })
      .then(response => response.json())
      .then(response => console.log(JSON.stringify(response)))
  }

  private static getUrl( prompt : string){
    return DbService.host + DbService.port + prompt;
  }

  async loadAllTags(){
    if (DbService.Tags.length > 0){
      return;
    }
    await fetch(DbService.getUrl(dbUrl.GET_ALL_TAGS)).then(res => res.json()).then(res => {
      for (let tag of res) {
        DbService.Tags.push(tag);
      }
    });
  }
  async getAllTagsWithCountAndRelevance(){
    return await fetch(DbService.getUrl(dbUrl.GET_ALL_TAGS_WITH_COUNT_AND_RELEVANCE)).then(res => res.json());
  }

  async getTagPostCount(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_TAG_POST_COUNT) + id).then(res => res.json());
  }

  async getTagRanking() {
    return await fetch(DbService.getUrl(dbUrl.GET_TAG_RANKING)).then(res => res.json());
  }

  async loadAllUsers() {
    if (DbService.Users.length > 0){
      return;
    }
    await fetch(DbService.getUrl(dbUrl.GET_ALL_USERS)).then(res => res.json()).then(res => {
      for (let user of res) {
        DbService.Users.push(user);
      }
    });
  }

  async getUserPostsDay(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_USER_POST_PER_DAY) + id).then(res => res.json());
  }
  async getUserPostsWithStats(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_USER_POSTS_WITH_STATS) + id).then(res => res.json());
  }

  async getUserImgSrc(id : string){
    const blob = await fetch(DbService.getUrl(dbUrl.GET_USER_IMG) + id).then(res => res.blob());
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
    return fetch(DbService.getUrl(dbUrl.GET_USER_BY_LOGIN + login)).then(res => res.json());
  }
  async getUserByEmail(email : string){
    return fetch(DbService.getUrl(dbUrl.GET_USER_BY_EMAIL + email)).then(res => res.json());
  }

  async getUserClicks(id : string){
    return fetch(DbService.getUrl(dbUrl.GET_USER_CLICKS + id)).then(res => res.json());
  }

  async getUserBestPost(id: string, type: string){
    return fetch(DbService.getUrl(dbUrl.GET_USER_BEST_POST) + id + "&type=" + type).then(res => res.json()).catch(reason => {return new Post()});
  }

  async getUserNewestPost(id: string): Promise<Post> {
    return fetch(DbService.getUrl(dbUrl.GET_USER_NEWEST_POST_WITH_STATS) + id).then(res => res.json());
  }

  async getMaxPerformance(){
    let max : Promise<number> = await fetch(DbService.getUrl(dbUrl.GET_POST_MAX_PERFORMANCE)).then(res => res.json());
    return max;
  }
  async getMaxRelevance(){
    let max : Promise<number> = await fetch(DbService.getUrl(dbUrl.GET_POST_MAX_RELEVANCE)).then(res => res.json());
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
    return await fetch(DbService.getUrl(dbUrl.GET_POST) + id).then(res => res.json());
  }
}
