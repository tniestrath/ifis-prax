import {Injectable} from '@angular/core';
import {DomSanitizer} from "@angular/platform-browser";
import {Tag, TagRanking, TagStats} from "../component/tag/Tag";
import {DbObject} from "./DbObject";
import {Post} from "../component/post/Post";
import {User} from "../page/page-einzel/user/user";
import {Callup} from "../component/call-up-chart/call-up-chart.component";

export enum dbUrl {
  HOST = "http://analyse.it-sicherheit.de/api",
  //HOST = "http://localhost:8080/api", DEBUG LINE
  PORT = "",
  GET_TAGS_ALL = "/tags/getPostTagsIdName",
  GET_TAGS_WITH_RELEVANCE_AND_VIEWS_ALL = "/tags/allTermsRelevanceAndViews",
  GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL = "/tags/getPostCountAbove?percentage=",

  GET_TAG_POST_COUNT = "/tags/getPostcount?id=",
  GET_TAG_RANKING = "/tags/getTermRanking",
  GET_TAGSTATS_BY_ID = "/tags/getTagStats?tagId=ID&limitDaysBack=DAYS",

  GET_USER_IMG = "/users/profilePic?id=",
  GET_USER_CLICKS = "/users/getViewsBrokenDown?id=",
  GET_USER_BY_LOGINNAME = "/users/getByLogin?u=",
  GET_USER_BY_ID = "/users/getById?id=",
  GET_USER_ORIGIN_MAP = "/users/getViewsByLocation?id=",
  GET_USER_VIEWS_PER_HOUR = "/users/getViewsPerHour?id=",
  HAS_USER_POST = "/users/hasPost?id=",

  GET_USERS_ALL = "/users/getAllNew",
  GET_USERS_ACCOUNTTYPES_ALL = "/users/getAccountTypeAll",
  GET_USERS_ALL_ORIGIN_MAP = "/users/getAllViewsByLocation",
  GET_USERS_ALL_VIEWS_PER_HOUR = "/users/getAllViewsPerHour",

  GET_POST = "/posts/getPostStatsByIdWithAuthor?id=",
  GET_POST_BY_USERS_BEST = "/posts/bestPost?id=",
  GET_POST_PERFORMANCE = "/posts/getPerformanceByArtId?id=",
  GET_POST_MAX_PERFORMANCE = "/posts/maxPerformance",
  GET_POST_MAX_RELEVANCE = "/posts/maxRelevance",

  GET_POSTS_ALL = "/posts/getAllPostsWithStats",
  GET_POSTS_PER_USER_PER_DAY = "/posts/getPostsByAuthorLine?id=",
  GET_POSTS_PER_USER_WITH_STATS = "/posts/getPostsByAuthorLine2?id=",
  GET_POSTS_NEWEST_BY_USER_WITH_STATS = "/posts/getNewestPostWithStatsByAuthor?id=",

  GET_CALLUPS_BY_TIME = "/bericht/callups?days=DAYS",
  GET_USERS_ACCOUNTTYPES_YESTERDAY = "/bericht/getAccountTypeAllYesterday",

  LOGIN = "/login",
  VALIDATE = "/validate",
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

  async login(username : string, userpass : string) {
    return await fetch(DbService.getUrl(dbUrl.LOGIN) + "?user=" + username + "&pass=" + userpass, {credentials: "include"}).then(res => res.blob());
  }
  async getUserByLogin(login : string) : Promise<User> {
    return await fetch(DbService.getUrl(dbUrl.GET_USER_BY_LOGINNAME) + login, {credentials: "include"}).then(res => res.json());
  }
  async getUserById(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_USER_BY_ID) + id, {credentials: "include"}).then(res => res.json());
  }

  async validate() : Promise<{"user_id":string}>{
    return await fetch(DbService.getUrl(dbUrl.VALIDATE), {credentials: "include"}).then(res => res.json());
  }

  async loadAllTags(){
    if (DbService.Tags.length > 0){
      return;
    }
    await fetch(DbService.getUrl(dbUrl.GET_TAGS_ALL)).then(res => res.json()).then(res => {
      for (let tag of res) {
        DbService.Tags.push(tag);
      }
    });
  }
  async getAllTagsWithRelevanceAndViews(){
    return await fetch(DbService.getUrl(dbUrl.GET_TAGS_WITH_RELEVANCE_AND_VIEWS_ALL), {credentials: "include"}).then(res => res.json());
  }

  async getTagPostCount(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_TAG_POST_COUNT) + id, {credentials: "include"}).then(res => res.json());
  }
  async getAllTagsPostCount(percantage : number) : Promise<Map<string, number>>{
    return await fetch(DbService.getUrl((dbUrl.GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL) + percantage) , {credentials: "include"}).then(res => res.json());
  }

  async getTagRanking() {
    return await fetch(DbService.getUrl(dbUrl.GET_TAG_RANKING), {credentials: "include"}).then(res => res.json());
  }

  async loadAllUsers() {
    if (DbService.Users.length > 0){
      return;
    }
    await fetch(DbService.getUrl(dbUrl.GET_USERS_ALL), {credentials: "include"}).then(res => res.json()).then(res => {
      for (let user of res) {
        DbService.Users.push(user);
      }
    });
  }

  async getUserPostsDay(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_USER_PER_DAY) + id, {credentials: "include"}).then(res => res.json());
  }
  async getUserPostsWithStats(id : string){
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_USER_WITH_STATS) + id, {credentials: "include"}).then(res => res.json());
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

  async getUserClicks(id : string){
    return fetch(DbService.getUrl(dbUrl.GET_USER_CLICKS + id) , {credentials: "include"}).then(res => res.json()).catch(reason => {return "NO DATA"});
  }

  async getUserBestPost(id: string, type: string){
    return fetch(DbService.getUrl(dbUrl.GET_POST_BY_USERS_BEST) + id + "&type=" + type, {credentials: "include"}).then(res => res.json()).catch(reason => {return new Post()});
  }

  async getUserNewestPost(id: string): Promise<Post> {
    return fetch(DbService.getUrl(dbUrl.GET_POSTS_NEWEST_BY_USER_WITH_STATS) + id, {credentials: "include"}).then(res => res.json());
  }

  async getUserAccountTypes() : Promise<Map<string, number>>{
    return fetch(DbService.getUrl(dbUrl.GET_USERS_ACCOUNTTYPES_ALL)).then(res => res.json());
  }

  async hasUserPost(id : number) {
    return fetch(DbService.getUrl(dbUrl.HAS_USER_POST) + id).then(res => res.json());
  }

  async getMaxPerformance(){
    let max : Promise<number> = await fetch(DbService.getUrl(dbUrl.GET_POST_MAX_PERFORMANCE), {credentials: "include"}).then(res => res.json());
    return max;
  }
  async getMaxRelevance(){
    let max : Promise<number> = await fetch(DbService.getUrl(dbUrl.GET_POST_MAX_RELEVANCE), {credentials: "include"}).then(res => res.json());
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

  async getPostsAll(){
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_ALL), {credentials: "include"}).then(res => res.json());
  }

  async getPostById(id: number) : Promise<Post> {
    return await fetch(DbService.getUrl(dbUrl.GET_POST) + id, {credentials: "include"}).then(res => res.json());
  }

  async getOriginMapByUser(id : number){
    return await  fetch(DbService.getUrl(dbUrl.GET_USER_ORIGIN_MAP) + id, {credentials: "include"}).then(res => res.json());
  }

  async getOriginMapAll() {
    return await fetch((DbService.getUrl(dbUrl.GET_USERS_ALL_ORIGIN_MAP)) , {credentials: "include"}).then(res => res.json());
  }

  async getClicksByTime(id : number){
    return await fetch(DbService.getUrl(dbUrl.GET_USER_VIEWS_PER_HOUR)+ id, {credentials: "include"}).then(res => {
      return res.json();
    });
  }
  async getClicksByTimeAll() : Promise<number[]>{
    return await fetch((DbService.getUrl(dbUrl.GET_USERS_ALL_VIEWS_PER_HOUR)), {credentials: "include"}).then(res => res.json());
  }

  async getTagStatsByID(id: number, timeSpan: number) : Promise<TagStats[]> {
    return await fetch(DbService.getUrl(dbUrl.GET_TAGSTATS_BY_ID).replace("ID", String(id)).replace("DAYS", String(timeSpan)), {credentials: "include"}).then(res => res.json());
  }

  async getCallupsByTime(days: number) : Promise<Callup[]> {
    return await fetch(DbService.getUrl(dbUrl.GET_CALLUPS_BY_TIME.replace("DAYS", String(days))), {credentials: "include"}).then(res => res.json());
  }

  async getUserAccountTypesYesterday() {
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_ACCOUNTTYPES_YESTERDAY), {credentials: "include"}).then(res => res.json());
  }
}
