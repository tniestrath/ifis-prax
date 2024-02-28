import {Injectable} from '@angular/core';
import {DomSanitizer} from "@angular/platform-browser";
import {Tag, TagStats} from "../component/tag/Tag";
import {DbObject} from "./DbObject";
import {Post} from "../component/post/Post";
import {User} from "../component/user/user";
import {Callup, CategoriesData} from "../component/call-up-chart/call-up-chart.component";
import {SystemUsage} from "../component/system/systemload/systemload.component";
import Util from "../util/Util";
import {Subject} from "rxjs";
import {ProfileState} from "../component/user/profile-completion/profile-completion.component";
import {
  SearchItem, SearchRank, SearchSS
} from "../component/search/search-no-results-list/search-list-item/search-list-item.component";
import {Newsletter} from "../component/newsletter/Newsletter";

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
  GET_USER_ALL_STATS_BY_ID = "/users/getAllSingleUser?id=",
  GET_USER_VIEWS_PER_HOUR = "/users/getViewsPerHour?id=",
  GET_USER_RANKINGS = "/users/getRankings?id=ID",
  HAS_USER_POST = "/users/hasPost?id=",
  HAS_USER_POST_TYPES = "/users/hasPostByType?id=ID",
  GET_USER_PROFILE_COMPLETION = "/users/getPotentialById?userId=USERID",
  GET_USER_PROFILE_AND_POSTS_BY_DATE = "/users/getUserClicksChartData?id=ID&start=START&end=END",
  GET_USER_POSTCOUNT_BY_TYPE = "/users/getPostCountByType?id=ID",
  GET_USER_EVENTCOUNT = "/users/getAmountOfEvents?id=ID",
  GET_USER_EVENTCOUNT_CREATED_YESTERDAY = "/users/getAmountOfEventsCreatedYesterday?id=ID",
  GET_USER_EVENTS_LIKE_POSTS = "/users/getEventsWithStatsAndId?&page=PAGE&size=SIZE&filter=FILTER&search=SEARCH&id=ID",
  GET_USER_TAG_DISTRIBUTION_PRECENTAGE = "/users/getSingleUserTagsData?id=ID&sorter=SORTER",

  GET_USERS_ALL = "/users/getAll?page=PAGE&size=SIZE&search=SEARCH&filterAbo=ACCFILTER&filterTyp=USRFILTER&sorter=SORTER",
  GET_USERS_ACCOUNTTYPES_ALL = "/users/getAccountTypeAll",
  GET_USERS_ACCOUNTTYPES_YESTERDAY = "/users/getAccountTypeAllYesterday",
  GET_USERS_ACCOUNTTYPES_ALL_NEW = "/users/getNewUsersAll",
  GET_USERS_ALL_VIEWS_PER_HOUR = "/users/getAllViewsPerHour",
  GET_USERS_Clicks_AVERAGE_BY_VIEWTYPE = "/users/getUserProfileAndPostViewsAveragesByType",
  GET_USERS_PROFILE_VIEWS_AVERAGE_BY_TYPE_BY_POSTHAVING = "/users/getUserProfileViewsAveragesByTypeAndPosts",
  GET_USERS_TAG_DISTRIBUTION_PRECENTAGE = "/users/getAllUserTagsData",


  GET_POST = "/posts/getPostStatsByIdWithAuthor?id=ID",
  GET_POST_BY_USERS_BEST = "/posts/bestPost?id=ID&type=TYPE",
  GET_POST_PERFORMANCE = "/posts/getPerformanceByArtId?id=",
  GET_POST_MAX_PERFORMANCE = "/posts/maxPerformance",
  GET_POST_MAX_RELEVANCE = "/posts/maxRelevance",
  GET_POST_VIEWS_BY_TIME = "/posts/getPostViewsByTime?id=ID",

  GET_POSTS_ALL = "/posts/getAllPostsWithStats",
  GET_POSTS_ALL_PAGED = "/posts/pageByTitle?page=PAGE&size=SIZE&sortBy=SORTER&filter=FILTER&search=SEARCH",
  GET_POSTS_PER_USER_PER_DAY = "/posts/getPostsByAuthorLine?id=",
  GET_POSTS_PER_USER_WITH_STATS = "/posts/getPostsByAuthorLine2?id=",
  GET_POSTS_BY_AUTHOR = "/posts/getPostsByAuthor?authorId=ID&page=PAGE&size=SIZE&filter=FILTER&search=SEARCH",
  GET_POSTS_BY_IDS = "/posts/getPostStatsForList?list=LIST",
  GET_POSTS_PER_TYPE = "/bericht/getPostsByType",
  GET_POSTS_PER_TYPE_YESTERDAY = "/bericht/getPostsByTypeYesterday",
  GET_POSTS_NEWEST_BY_USER_WITH_STATS = "/posts/getNewestPostWithStatsByAuthor?id=",
  GET_POSTS_TOP_BY_SORTER = "/posts/getTopWithType?sorter=SORTER&type=TYPE&limit=LIMIT",

  GET_CALLUPS_BY_TIME = "/bericht/callups?days=DAYS",
  GET_CALLUP_CATEGORIES_BY_DATE = "/bericht/getCallupByCategoryDate?date=DATE",
  GET_CALLUP_CATEGORIES_BY_DATETIME = "/bericht/getCallupByCategoryDateAndHour?date=DATE&hour=HOUR",
  GET_CALLUP_CATEGORIES_ALL_TIME = "/bericht/getCallupByCategoryAllTime",

  GET_NEWSLETTER = "/newsletter/getNewsletterCallup?emailId=ID",
  GET_NEWSLETTER_LATEST = "/newsletter/getLatestNewsletterCallup",
  GET_NEWSLETTER_SUBS = "/newsletter/getStatusAll",
  GET_NEWSLETTER_SUBS_YESTERDAY = "/newsletter/getAmountOfSubsYesterday",
  GET_NEWSLETTER_SUBS_BY_DATERANGE = "/newsletter/getAmountOfSubsByDateRange?daysBackTo=DAYSBACKTO&daysBackFrom=DAYSBACKFROM",
  GET_NEWSLETTER_SUBS_AS_MAIL_BY_STATUS = "/newsletter/getMailByStatus?c=STATUS",
  GET_NEWSLETTER_GEO = "/newsletter/getNewsletterGeoSingle?emailId=ID",

  GET_NEWSLETTERS_ALL = "/newsletter/getAll?page=PAGE&size=SIZE",
  GET_NEWSLETTERS_GEO = "/newsletter/getNewsletterGeo",

  GET_EVENTS = "/events/getAmountOfEvents",
  GET_EVENTS_YESTERDAY = "/events/getAmountOfEventsCreatedYesterday",
  GET_EVENTS_LIKE_POSTS = "/posts/getEventsWithStats?&page=PAGE&size=SIZE&filter=FILTER&search=SEARCH",

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

  GET_SEARCHES_NO_RESULTS = "/search-stats/getAllUnfixedSearches",
  GET_SEARCHES_TOP_N = "/search-stats/getTopNSearchQueries?number=NUMBER",
  GET_SEARCHES_TOP_N_BY_SS = "/search-stats/getTopNSearchQueriesBySS?number=NUMBER",
  POST_SEARCH_IGNORE = "/search-stats/blockSearch?id=SEARCH",

  GET_SYSTEM_USAGE = "/systemLoad/systemLive",
  GET_SYSTEM_USAGE_NOW = "/systemLoad/current",
  GET_SYSTEM_TIME_HOUR = "/systemLoad/getHour",

  LOGIN = "/login?user=USERNAME&pass=PASSWORD",
  LOGIN_WITH_BODY = "/login2",
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


  private seoToken : string = "";

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
    console.log("STATUS: " + html_code + " @ " + url);

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
  async loginWithBody(username : string, userpass : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.LOGIN_WITH_BODY), {method: "POST", credentials: "include", body: "{\"username\":\"" + username + "\",\"password\":\" "+ userpass +" \"}"}).then(res => {this.setFinished(res.status, res.url); return res.blob()});
  }
  async getUserByLogin(login : string) : Promise<User> {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_BY_LOGINNAME) + login, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserById(id : string) : Promise<User>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_BY_ID) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserAllStatsById(id : string) : Promise<User>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_ALL_STATS_BY_ID) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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

  async getAllUsers(page: number, size: number, search: string, filter: { sort: string, accType: string, usrType: string }, signal: AbortSignal) {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_ALL).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("SEARCH", search).replace("ACCFILTER", filter.accType).replace("USRFILTER", filter.usrType).replace("SORTER", filter.sort), {credentials: "include", signal: signal}).then(res =>{this.setFinished(res.status, res.url); return res.json()}).then((res : {users: any[], count : number}) => {
      DbService.Users = res.users;
      return res;
    }, reason => {
      console.log(reason);
      return {users: [], count: 0}});
  }

  async getUserPostsDay(id : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_USER_PER_DAY) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserPostsWithStats(id : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_PER_USER_WITH_STATS) + id, {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserPostsPaged(id : string, page : number, size : number, filter : string, search : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_BY_AUTHOR).replace("ID", id).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getPostsByIDs(list : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_BY_IDS).replace("LIST", list), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
  async hasUserPostTypes(id: string) : Promise<{ blog: boolean; news: boolean; artikel: boolean; podcast: boolean; whitepaper: boolean }> {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.HAS_USER_POST_TYPES) + id).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserProfileCompletion(id : string): Promise<ProfileState> {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USER_PROFILE_COMPLETION).replace("USERID", id)).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserClicksChartData(id : string, start : string, end : string): Promise<{date: string, profileViews: number, biggestPost: {id: number, title: string, type: string, clicks: number}, posts: {id: number, title: string, type: string, clicks: number}[]}[]> {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USER_PROFILE_AND_POSTS_BY_DATE).replace("ID", id).replace("START", start).replace("END", end)).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserEventCount(id : string) {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USER_EVENTCOUNT).replace("ID", id)).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserEventCountYesterday(id : string) {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USER_EVENTCOUNT_CREATED_YESTERDAY).replace("ID", id)).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserPostCountByType(id : string) {
    this.setLoading();
    return fetch(DbService.getUrl(dbUrl.GET_USER_POSTCOUNT_BY_TYPE).replace("ID", id)).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
  async getPostViewsByTime(id : string) : Promise<{dates: string[], views: number[]}>{
    this.setLoading();
    return  await fetch(DbService.getUrl(dbUrl.GET_POST_VIEWS_BY_TIME).replace("ID", id), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
  async getPostsAllPaged(page : number, size : number, sorter : string, filter : string, search : string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_POSTS_ALL_PAGED).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("SORTER", sorter).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
  async getUserRankings(id: string) : Promise<{
    rankingContent: number;
    rankingContentByGroup: number;
    rankingProfile: number;
    rankingProfileByGroup: number
  }>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_RANKINGS).replace("ID", id), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
  async getNewsletters(page : number, size : number) : Promise<Newsletter[]>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTERS_ALL).replace("PAGE", String(page)).replace("SIZE", String(size)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewslettersGeo() : Promise<Map<string,number>>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTERS_GEO), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletter(id : number) : Promise<Newsletter>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER).replace("ID", String(id)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getLatestNewsletter() : Promise<Newsletter>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER_LATEST), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterGeo(id : number) : Promise<Map<string,number>>{
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_NEWSLETTER_GEO).replace("ID", String(id)), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
  async getEventsLikePostsPaged(page : number, size : number,filter : string, search: string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_EVENTS_LIKE_POSTS).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserEventsLikePostsPaged(id : string, page : number, size : number,filter : string, search: string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_EVENTS_LIKE_POSTS).replace("ID", id).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserProfileViewsAverageByType() {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_PROFILE_VIEWS_AVERAGE_BY_TYPE_BY_POSTHAVING), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserTagsDistributionPercentage() {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_TAG_DISTRIBUTION_PRECENTAGE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserTagsRanking(id : string, sorter: string){
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USER_TAG_DISTRIBUTION_PRECENTAGE).replace("ID", id).replace("SORTER", sorter), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserClicksAverageByViewType() {
    this.setLoading();
    return await fetch(DbService.getUrl(dbUrl.GET_USERS_Clicks_AVERAGE_BY_VIEWTYPE), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async loginSeo() {
    this.setLoading();
    if (this.seoToken == "") {
    return await fetch("https://seo.internet-sicherheit.de/api/auth/login", {
      "headers": {
        "accept": "application/json, text/plain, */*",
        "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7",
        "cache-control": "no-cache",
        "content-type": "application/json",
        "pragma": "no-cache",
        "sec-ch-ua": "\"\"",
        "sec-ch-ua-mobile": "?0",
        "sec-ch-ua-platform": "\"\"",
        "sec-fetch-dest": "empty",
        "sec-fetch-mode": "cors",
        "sec-fetch-site": "same-origin",
        "origin": "https://seo.internet-sicherheit.de/"
      },
      "referrer": "https://seo.internet-sicherheit.de/",
      "referrerPolicy": "strict-origin-when-cross-origin",
      "body": "{\"username\":\"AnalyseITSicherheit\",\"password\":\"EnTUFpSbRt83EM3\"}",
      "method": "POST",
      "mode": "cors",
      "credentials": "omit"
    }).then(res => {this.setFinished(res.status, res.url); return res.json()}).then((value : {token : string} ) => {this.seoToken = value.token})
    }
  }

  async getSeoIndexOverTime(isMobile : string) : Promise<{id : any, sichtbarkeitsindex : number, date : number}[]> {
    this.setLoading();
    return this.loginSeo().then(value => {
      return fetch("https://seo.internet-sicherheit.de/api/sistrix/domain/sichtbarkeitsindexHistory/it-sicherheit.de?isMobile="+isMobile, {
        "headers": {
          "accept": "application/json, text/plain, */*",
          "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7",
          "authorization": "Bearer " + this.seoToken,
          "cache-control": "no-cache",
          "content-type": "application/json",
          "pragma": "no-cache",
          "sec-ch-ua": "\"\"",
          "sec-ch-ua-mobile": "?0",
          "sec-ch-ua-platform": "\"\"",
          "sec-fetch-dest": "empty",
          "sec-fetch-mode": "cors",
          "sec-fetch-site": "same-origin"
        },
        "referrer": "https://seo.internet-sicherheit.de/",
        "referrerPolicy": "strict-origin-when-cross-origin",
        "body": null,
        "method": "GET",
        "mode": "cors",
        "credentials": "omit"
    }).then(res => {
      this.setFinished(res.status, res.url);
      return res.json();
    });
    });
  }

  async getSeoStatsNow() : Promise<{id : any, sichtbarkeitsindex : number, date : number}[]> {
    this.setLoading();
    return this.loginSeo().then(value => {
      return fetch("https://seo.internet-sicherheit.de/api/sistrix/domain/sichtbarkeitsindexHistory/it-sicherheit.de?isMobile=false", {
        "headers": {
          "accept": "application/json, text/plain, */*",
          "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7",
          "authorization": "Bearer " + this.seoToken,
          "cache-control": "no-cache",
          "content-type": "application/json",
          "pragma": "no-cache",
          "sec-ch-ua": "\"\"",
          "sec-ch-ua-mobile": "?0",
          "sec-ch-ua-platform": "\"\"",
          "sec-fetch-dest": "empty",
          "sec-fetch-mode": "cors",
          "sec-fetch-site": "same-origin"
        },
        "referrer": "https://seo.internet-sicherheit.de/",
        "referrerPolicy": "strict-origin-when-cross-origin",
        "body": null,
        "method": "GET",
        "mode": "cors",
        "credentials": "omit"
    }).then(res => {
      this.setFinished(res.status, res.url);
      return res.json();
    });
    });
  }

  async getSeoImpCtrNow() : Promise<{ "clicks": number, "ctr": number, "impressions": number, "keys": string[]}[]> {
    this.setLoading();
    return this.loginSeo().then(value => {
      return fetch("https://seo.internet-sicherheit.de/api/googleSearchConsole/executeQuery?domain=sc-domain%3Ait-sicherheit.de&method=chart&startDate=" + Util.getFormattedNow(-30) + "&endDate=" + Util.getFormattedNow(), {
        "headers": {
          "accept": "application/json, text/plain, */*",
          "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7",
          "authorization": "Bearer " + this.seoToken,
          "cache-control": "no-cache",
          "content-type": "application/json",
          "pragma": "no-cache",
          "sec-ch-ua": "\"\"",
          "sec-ch-ua-mobile": "?0",
          "sec-ch-ua-platform": "\"\"",
          "sec-fetch-dest": "empty",
          "sec-fetch-mode": "cors",
          "sec-fetch-site": "same-origin"
        },
        "referrer": "https://seo.internet-sicherheit.de/",
        "referrerPolicy": "strict-origin-when-cross-origin",
        "body": null,
        "method": "GET",
        "mode": "cors",
        "credentials": "omit"
    }).then(res => {
      this.setFinished(res.status, res.url);
      return res.json();
    });
    });
  }

  async getSeoKeywordsNow() : Promise<{ "clicks": number, "ctr": number, "impressions": number, "keys": string[]}[]> {
    this.setLoading();
    return this.loginSeo().then(value => {
      return fetch("https://seo.internet-sicherheit.de/api/googleSearchConsole/executeQuery?domain=sc-domain%3Ait-sicherheit.de&method=keyword&startDate=" + Util.getFormattedNow(-30) + "&endDate=" + Util.getFormattedNow(), {
        "headers": {
          "accept": "application/json, text/plain, */*",
          "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7",
          "authorization": "Bearer " + this.seoToken,
          "cache-control": "no-cache",
          "content-type": "application/json",
          "pragma": "no-cache",
          "sec-ch-ua": "\"\"",
          "sec-ch-ua-mobile": "?0",
          "sec-ch-ua-platform": "\"\"",
          "sec-fetch-dest": "empty",
          "sec-fetch-mode": "cors",
          "sec-fetch-site": "same-origin"
        },
        "referrer": "https://seo.internet-sicherheit.de/",
        "referrerPolicy": "strict-origin-when-cross-origin",
        "body": null,
        "method": "GET",
        "mode": "cors",
        "credentials": "omit"
    }).then(res => {
      this.setFinished(res.status, res.url);
      return res.json();
    });
    });
  }

  async getSearchesWithoutResults() : Promise<SearchItem[]> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_SEARCHES_NO_RESULTS)) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSearchesTopN(num : number) : Promise<SearchRank[]> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_SEARCHES_TOP_N).replace("NUMBER", String(num))) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSearchesTopNBySS(num : number) : Promise<SearchSS[]> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.GET_SEARCHES_TOP_N_BY_SS).replace("NUMBER", String(num))) , {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async ignoreSearch(searchID : string) : Promise<boolean> {
    this.setLoading();
    return await fetch((DbService.getUrl(dbUrl.POST_SEARCH_IGNORE).replace("SEARCH", searchID)) , {credentials: "include", method: "post"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

}
