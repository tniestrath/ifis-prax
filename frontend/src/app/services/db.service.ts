import {Injectable} from '@angular/core';
import {DomSanitizer} from "@angular/platform-browser";
import {Tag, TagRanking, TagStats} from "../component/tag/Tag";
import {DbObject} from "./DbObject";
import {Post} from "../component/post/Post";
import {User} from "../page/page-einzel/user/user";
import {Callup, CategoriesData} from "../component/call-up-chart/call-up-chart.component";
import {SystemUsage} from "../component/system/systemload/systemload.component";
import Util from "../util/Util";
import {Subject, Subscription} from "rxjs";

export enum dbUrl {
  HOST = "http://analyse.it-sicherheit.de/api",
  //HOST = "http://localhost:8080/api", // DEBUG
  PORT = "",

  GET_TAGS_ALL = "/tags/getPostTagsIdName",
  GET_TAGS_WITH_RELEVANCE_AND_VIEWS_ALL = "/tags/allTermsRelevanceAndViews",
  GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL = "/tags/getPostCountAbove?percentage=",

  GET_TAG_POST_COUNT = "/tags/getPostcount?id=",
  GET_TAG_RANKING = "/tags/getTermRanking",
  GET_TAGSTATS_BY_ID = "/tags/getTagStats?tagId=ID&limitDaysBack=DAYS&dataType=TYPE",

  GET_USER_IMG = "/users/profilePic?id=",
  GET_USER_CLICKS = "/users/getViewsBrokenDown?id=",
  GET_USER_BY_LOGINNAME = "/users/getByLogin?u=",
  GET_USER_BY_ID = "/users/getById?id=",
  GET_USER_VIEWS_PER_HOUR = "/users/getViewsPerHour?id=",
  HAS_USER_POST = "/users/hasPost?id=",

  GET_USERS_ALL = "/users/getAllNew",
  GET_USERS_ACCOUNTTYPES_ALL = "/users/getAccountTypeAll",
  GET_USERS_ACCOUNTTYPES_YESTERDAY = "/users/getAccountTypeAllYesterday",
  GET_USERS_ACCOUNTTYPES_ALL_NEW = "/users/getNewUsersAll",
  GET_USERS_ALL_VIEWS_PER_HOUR = "/users/getAllViewsPerHour",

  GET_POST = "/posts/getPostStatsByIdWithAuthor?id=ID",
  GET_POST_BY_USERS_BEST = "/posts/bestPost?id=ID&type=TYPE",
  GET_POST_PERFORMANCE = "/posts/getPerformanceByArtId?id=",
  GET_POST_MAX_PERFORMANCE = "/posts/maxPerformance",
  GET_POST_MAX_RELEVANCE = "/posts/maxRelevance",

  GET_POSTS_ALL = "/posts/getAllPostsWithStats",
  GET_POSTS_PER_USER_PER_DAY = "/posts/getPostsByAuthorLine?id=",
  GET_POSTS_PER_USER_WITH_STATS = "/posts/getPostsByAuthorLine2?id=",
  GET_POSTS_PER_TYPE = "/bericht/getPostsByType",
  GET_POSTS_PER_TYPE_YESTERDAY = "/bericht/getPostsByTypeYesterday",
  GET_POSTS_NEWEST_BY_USER_WITH_STATS = "/posts/getNewestPostWithStatsByAuthor?id=",
  GET_POSTS_TOP_BY_SORTER = "/posts/getTopWithType?sorter=SORTER&type=TYPE&limit=LIMIT",

  GET_CALLUPS_BY_TIME = "/bericht/callups?days=DAYS",
  GET_CALLUP_CATEGORIES_BY_DATE = "/bericht/getCallupByCategoryDate?date=DATE",
  GET_CALLUP_CATEGORIES_BY_DATETIME = "/bericht/getCallupByCategoryDateAndHour?date=DATE&hour=HOUR",
  GET_CALLUP_CATEGORIES_ALL_TIME = "/bericht/getCallupByCategoryAllTime",


  GET_NEWSLETTER_SUBS = "/newsletter/getStatusAll",
  GET_NEWSLETTER_SUBS_YESTERDAY = "/newsletter/getAmountOfSubsYesterday",
  GET_NEWSLETTER_SUBS_BY_DATERANGE = "/newsletter/getAmountOfSubsByDateRange?daysBackTo=DAYSBACKTO&daysBackFrom=DAYSBACKFROM",
  GET_NEWSLETTER_SUBS_AS_MAIL_BY_STATUS = "/newsletter/getMailByStatus?c=STATUS",

  GET_EVENTS = "/events/getAmountOfEvents",
  GET_EVENTS_YESTERDAY = "/events/getAmountOfEventsCreatedYesterday",

  GET_GEO_GERMANY_ALL_TIME = "/geo/getTotalGermanGeoAllTime",
  GET_GEO_GERMANY_BY_DATES = "/geo/getTotalGermanGeoByDay?start=START&end=END",
  GET_GEO_GERMANY_ALL_TIME_BY_REGION = "/geo/getRegionGermanGeoAllTime?region=REGION",
  GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES = "/geo/getRegionGermanGeoByDate?region=REGION&start=START&end=END",
  GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES_LISTED = "/geo/getRegionGermanGeoByDateAsList?region=REGION&start=START&end=END",
  GET_GEO_USER_BY_DATES = "/geo/getUserGeoByIdAndDay?id=USRID&start=START&end=END",
  GET_GEO_LAST_TIMESTAMP = "/geo/lastEntry",
  GET_GEO_FIRST_TIMESTAMP = "/geo/firstEntry",
  GET_GEO_TIMESPAN = "/geo/geoRange",

  GET_PODCAST_ALL = "/posts/getAllPodcastsWithStats",
  GET_RATGEBER_ALL = "/posts/getAllRatgeberWithStats",

  GET_SYSTEM_USAGE = "/systemLoad/systemLive",
  GET_SYSTEM_USAGE_NOW = "/systemLoad/current",
  GET_SYSTEM_TIME_HOUR = "/systemLoad/getHour",

  LOGIN = "/login?user=USERNAME&pass=PASSWORD",
  VALIDATE = "/validate",
  MANUAL_VALIDATE = "/validateCookie?value=VALUE",
}

@Injectable({
  providedIn: 'root'
})
export class DbService {

  public static Tags : Tag[] = [];
  public static Users : User[] = [];

  public status : Subject<number> = new Subject<number>();
  private requestCount = 0;
  private failedRequestCount = 0;
  private lastFail : string = dbUrl.HOST;

  constructor(private sanitizer : DomSanitizer) { }

  private static getUrl( prompt : string){
    return dbUrl.HOST + dbUrl.PORT + prompt;
  }

  private setStatus(status_code : number){
    this.status.next(status_code);
  }
  private setLoading(){
    this.requestCount++;
    this.setStatus(1);
  }
  private setFinished(html_code : number, url : string){
    //console.log("STATUS: " + html_code + " @ " + url);

    if (html_code >= 200 && html_code < 400){
      this.requestCount--;
      if (this.lastFail == url){
        this.lastFail = dbUrl.HOST;
        this.failedRequestCount--;
        this.setStatus(0);
      }
      else if (this.requestCount <= 0){
        this.setStatus(0);
      }
    } else {
      this.lastFail = url;
      this.failedRequestCount++;
      this.setStatus(html_code);
    }
  }
  public resetStatus(){
    this.requestCount = 0;
    this.setStatus(0);
  }

  async login(username : string, userpass : string) {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.LOGIN).replace("USERNAME", username).replace("PASSWORD", userpass), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.blob()});
  }
  async getUserByLogin(login : string) : Promise<User> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_BY_LOGINNAME) + login, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserById(id : string) : Promise<User>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_BY_ID) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async validate() : Promise<{"user_id":string}>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.VALIDATE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async manualValidate(value : string) : Promise<number>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.MANUAL_VALIDATE).replace("VALUE", value), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async loadAllTags(){
    this.setLoading();
    if (DbService.Tags.length > 0){
      return;
    }
    await fetch(DbService.getUrl(dbUrl.GET_TAGS_ALL)).then(res => res.json()).then(res => {
      for (let tag of res) {
        DbService.Tags.push(tag);
      }
    this.setFinished(res.status, res.url);
    });
  }
  async getAllTagsWithRelevanceAndViews(){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_TAGS_WITH_RELEVANCE_AND_VIEWS_ALL), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getTagPostCount(id : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_TAG_POST_COUNT) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getAllTagsPostCount(percentage : number) : Promise<Map<string, number>>{
    this.setLoading();
    return await fetch(DbService.getUrl((dbUrl.GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL) + percentage) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getTagRanking() {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_TAG_RANKING), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async loadAllUsers() {
    this.setLoading();
    if (DbService.Users.length > 0){
      return;
    }
    await fetch(DbService.getUrl(dbUrl.GET_USERS_ALL), {credentials: "include"}).then(res =>{this.setFinished(res.status, res.url); return res.json()}).then(res => {
      DbService.Users = [];
      for (let user of res) {
        DbService.Users.push(user);
      }
    });
  }

  async getUserPostsDay(id : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_USER_PER_DAY) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserPostsWithStats(id : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_USER_WITH_STATS) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserImgSrc(id : string){
    this.setLoading();
    const blob = await fetch(DbService.getUrl(dbUrl.GET_USER_IMG) + id).then(res => {this.setFinished(res.status, res.url); return res.blob()});
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
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USER_CLICKS + id) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()}).catch(reason => {return "NO DATA"});
  }

  async getUserBestPost(id: string, type: string){
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_POST_BY_USERS_BEST).replace("ID", id).replace("TYPE", type), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()}).catch(reason => {return new Post()});
  }

  async getUserNewestPost(id: string): Promise<Post> {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_POSTS_NEWEST_BY_USER_WITH_STATS) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserAccountTypes() : Promise<Map<string, number>>{
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USERS_ACCOUNTTYPES_ALL)).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async hasUserPost(id : number) {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.HAS_USER_POST) + id).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getMaxPerformance(){
    this.setLoading();
    let max : Promise<number> = await fetch(DbService.getUrl(dbUrl.GET_POST_MAX_PERFORMANCE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
    return max;
  }
  async getMaxRelevance(){
    this.setLoading();
    let max : Promise<number> = await fetch(DbService.getUrl(dbUrl.GET_POST_MAX_RELEVANCE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_ALL), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getPostById(id: number) : Promise<Post> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POST).replace("ID", String(id)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getPostsPerType() : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_TYPE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getPostsPerTypeYesterday() : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_TYPE_YESTERDAY), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getOriginMapByUser(id : number, start : string, end : string){
    this.setLoading();
    return await  fetch(DbService.getUrl(dbUrl.GET_GEO_USER_BY_DATES).replace("USRID", String(id)).replace("START", start).replace("END", end), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getGeoAll() : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_GERMANY_ALL_TIME)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByDates(start : string, end : string) : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_GERMANY_BY_DATES).replace("START", start).replace("END", end)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByRegion(region : string) : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION).replace("REGION", region)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByRegionByDates(region : string, start? : string, end? : string) : Promise<Map<string,number>> {
    this.setLoading();
    if (start && end) return await fetch((DbService.getUrl(dbUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES).replace("REGION", region).replace("START", start).replace("END", end)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
    else return await fetch((DbService.getUrl(dbUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION).replace("REGION", region)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByRegionByDatesListed(region : string, start : string, end : string) : Promise<{ data: number[], dates : string[] }> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES_LISTED).replace("REGION", region).replace("START", start).replace("END", end)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoStart() : Promise<string> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_FIRST_TIMESTAMP)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoEnd() : Promise<string> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_LAST_TIMESTAMP)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoTimespan() : Promise<string[]> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_GEO_TIMESPAN)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getPodcastsAll() : Promise<Post[]> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_PODCAST_ALL)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getRatgeberAll() : Promise<Post[]> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_RATGEBER_ALL)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getClicksByTime(id : number){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_VIEWS_PER_HOUR)+ id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getClicksByTimeAll() : Promise<number[]>{
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_USERS_ALL_VIEWS_PER_HOUR)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getTagStatsByID(id: number, timeSpan: number, dataType: string) : Promise<TagStats[]> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_TAGSTATS_BY_ID).replace("ID", String(id)).replace("DAYS", String(timeSpan)).replace("TYPE", dataType), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getCallupsByTime(days: number) : Promise<Callup[]> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_CALLUPS_BY_TIME.replace("DAYS", String(days))), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallupsByCategoriesNewest() : Promise<CategoriesData>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_CALLUP_CATEGORIES_BY_DATE).replace("DATE", Util.getFormattedNow()), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallupsByCategoriesByDateTime(date : string, hour : number) :Promise<CategoriesData>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_CALLUP_CATEGORIES_BY_DATETIME).replace("DATE", date).replace("HOUR", String(hour)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallupsByCategoriesByDate(date : string) :Promise<CategoriesData>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_CALLUP_CATEGORIES_BY_DATE).replace("DATE", date), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallpusByCategoriesAllTime() : Promise<CategoriesData> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_CALLUP_CATEGORIES_ALL_TIME), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserAccountTypesYesterday() {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_ACCOUNTTYPES_YESTERDAY), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserAccountTypesAllNew() : Promise<{ohne: string[], basis: string[], "basis-plus": string[], plus: string[], premium: string[], sponsor: string[]}>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_ACCOUNTTYPES_ALL_NEW), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getTopPostsBySorterWithType(sorter: string, type: string, limit: number) : Promise<Post[]>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_TOP_BY_SORTER).replace("SORTER", sorter).replace("TYPE", type).replace("LIMIT", String(limit)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getNewsletterSubs(){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER_SUBS), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterSubsByDateRange(daysBackTo : number, daysBackFrom : number){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER_SUBS_BY_DATERANGE).replace("DAYSBACKTO", String(daysBackTo)).replace("DAYSBACKFROM", String(daysBackFrom)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterSubsYesterday(){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER_SUBS_YESTERDAY), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterSubsAsMailByStatus(c : string) : Promise<string[]>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER_SUBS_AS_MAIL_BY_STATUS).replace("STATUS", c), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getSystemTimeHour() : Promise<number>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_SYSTEM_TIME_HOUR), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSystemUsage() : Promise<SystemUsage>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_SYSTEM_USAGE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSystemUsageNow() : Promise<{cpu : number, memory : number, networkSent : number, networkRecv : number}>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_SYSTEM_USAGE_NOW), {credentials: "include"}).then(res => {
      this.setFinished(res.status, res.url);

      if (res.status < 200 || res.status >= 400){
        this.failedRequestCount++;
        if (this.failedRequestCount >= 5) {
          this.failedRequestCount = 0;
          location.reload();
        }
      }

      return res.json()
    });
  }

  async getEvents() : Promise<string[]> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_EVENTS), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getEventsYesterday() : Promise<string[]> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_EVENTS_YESTERDAY), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
}
