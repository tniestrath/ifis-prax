import {Injectable} from '@angular/core';
import {DomSanitizer} from "@angular/platform-browser";
import {Tag, TagRanking, TagStats} from "../component/tag/Tag";
import {DbObject} from "./DbObject";
import {Post} from "../component/post/Post";
import {User} from "../component/user/user";
import {Callup, CategoriesData} from "../component/call-up-chart/call-up-chart.component";
import {SystemUsage} from "../component/system/systemload/systemload.component";
import Util from "../util/Util";
import {Subject} from "rxjs";
import {ProfileState} from "../component/user/profile-completion/profile-completion.component";
import {
  SearchAnbieterItem,
  SearchItem, SearchSS
} from "../component/search/search-no-results-list/search-list-item/search-list-item.component";
import {Newsletter} from "../component/newsletter/Newsletter";
import {BadBot} from "../component/system/black-hole-list/bad-bot-item/bad-bot-item.component";

/**
 * @enum apiUrl
 * backend request urls
 */
export enum apiUrl {
  /**
   * Host and Port url prefixes
   */
  HOST = "http://analyse.it-sicherheit.de/api",
  //HOST = "http://localhost:8080/api", // DEBUG
  PORT = "",

  /**
   * Tags related requests
   */
  GET_TAGS_ALL = "/tags/getPostTagsIdName",
  GET_TAGS_WITH_STATS = "/tags/getTagStatsAll",
  GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL = "/tags/getPostCountAbove?percentage=",
  /**
   * Single Tag
   */
  GET_TAG_POST_COUNT = "/tags/getPostcount?id=",
  GET_TAG_RANKING = "/tags/getTermRanking",
  GET_TAGSTATS_BY_ID = "/tags/getTagStatsDaysBack?tagId=ID&daysBack=DAYS",

  /**
   * User related requests
   */
  GET_USERS_ALL = "/users/getAll?page=PAGE&size=SIZE&search=SEARCH&filterAbo=ACCFILTER&filterTyp=USRFILTER&sorter=SORTER",
  GET_USERS_ACCOUNTTYPES_ALL = "/users/getAccountTypeAll",
  GET_USERS_ACCOUNTTYPES_YESTERDAY = "/users/getAccountTypeAllYesterday",
  GET_USERS_ACCOUNTTYPES_ALL_NEW = "/users/getNewUsersAll",
  GET_USERS_ALL_VIEWS_PER_HOUR = "/users/getAllViewsPerHour",
  GET_USERS_Clicks_AVERAGE_BY_VIEWTYPE = "/users/getUserProfileAndPostViewsAveragesByType",
  GET_USERS_PROFILE_VIEWS_AVERAGE_BY_TYPE_BY_POSTHAVING = "/users/getUserProfileViewsAveragesByTypeAndPosts",
  GET_USERS_TAG_DISTRIBUTION_PRECENTAGE = "/users/getAllUserTagsData",

  /**
   * Single User related requests
   */
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

  GET_POST = "/posts/getPostStatsByIdWithAuthor?id=ID",
  GET_POST_WITH_CONTENT = "/posts/getPostStatsWithContent?id=ID",
  GET_POST_NEWEST = "/posts/getNewestPost",
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
  GET_NEWSLETTERS_ALL_GEO = "/newsletter/getNewsletterGeo",
  GET_NEWSLETTERS_ALL_OPENTIMES = "/newsletter/getGlobalHourly",
  GET_NEWSLETTERS_ALL_OPENRATE = "/newsletter/getGlobalOR",

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

  GET_PODCAST_ALL = "/posts/getAllTypeWithStatsPageable?type=podcast&page=PAGE&size=SIZE",
  GET_RATGEBER_ALL = "/posts/getAllTypeWithStats?type=ratgeber",

  GET_SEARCHES_NO_RESULTS = "/search-stats/getAllUnfixedSearches?page=PAGE&size=SIZE",
  GET_SEARCHES_ANBIETER_NO_RESULT = "/search-stats/getAnbieterNoneFound?page=PAGE&size=SIZE",
  GET_SEARCHES_COOL = "/search-stats/getCoolSearchList?page=PAGE&size=SIZE&sorter=SORTER&dir=DIR",
  SEARCH_IGNORE = "/search-stats/blockSearch?search=SEARCH",
  SEARCH_FLIP = "/search-stats/flipSearch?search=SEARCH",
  SEARCH_USER_IGNORE = "/search-stats/deleteAnbieterSearch?id=ID",
  SEARCH_USER_FLIP = "/search-stats/flipAnbieterSearch?search=SEARCH",

  GET_SYSTEM_USAGE = "/systemLoad/systemLive",
  GET_SYSTEM_USAGE_NOW = "/systemLoad/current",
  GET_SYSTEM_TIME_HOUR = "/systemLoad/getHour",
  GET_SYSTEM_BAD_BOTS = "/diagnosis/getBlackHoleData",

  LOGIN = "/login?user=USERNAME&pass=PASSWORD",
  LOGIN_WITH_BODY = "/login2",
  VALIDATE = "/validate",
  MANUAL_VALIDATE = "/validateCookie?value=VALUE",
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  public static Users : User[] = [];

  public status : Subject<number> = new Subject<number>();
  private requestCount = 0;
  private failedRequestCount = 0;
  private lastFail : string = apiUrl.HOST;
  private static abortController: Map<String,AbortController> = new Map<String, AbortController>();


  private seoToken : string = "";

  constructor(private sanitizer : DomSanitizer) { }

  private static setupRequest( prompt : string){
    return apiUrl.HOST + apiUrl.PORT + prompt;
  }

  private static setupController( prompt : string){
    let controller = new AbortController();
    ApiService.abortController.set(prompt, controller);
    return controller.signal;
  }

  private setStatus(status_code : number){
    this.status.next(status_code);
  }
  private setLoading(){
    this.requestCount++;
    this.setStatus(1);
  }
  private setFinished(html_code : number, url : string){
    console.log("STATUS: " + html_code + " @ " + url + " OPEN: " + this.requestCount);

    if (html_code >= 200 && html_code < 400){
      this.requestCount--;
      console.log("OPEN: " + this.requestCount);
      if (this.lastFail == url){
        this.lastFail = apiUrl.HOST;
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
    this.failedRequestCount = 0;
    this.setStatus(0);
  }

  public cancelAllRequests(){
    console.log("old page requests canceled");
    ApiService.abortController.forEach(value => value.abort());
    ApiService.abortController.clear();
    this.resetStatus();

  }

  async login(username : string, userpass : string) {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.LOGIN).replace("USERNAME", username).replace("PASSWORD", userpass), {credentials: "include"}).then(res => {this.setFinished(res.status, res.url); return res.blob()});
  }
  async loginWithBody(username : string, userpass : string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.LOGIN_WITH_BODY), {method: "POST", credentials: "include", body: "{\"username\":\"" + username + "\",\"password\":\""+ userpass +"\"}" , headers: {"Content-Type" : "application/json"}}).then(res => {this.setFinished(res.status, res.url); return res.blob()});
  }
  async getUserByLogin(login : string) : Promise<User> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_BY_LOGINNAME) + login, {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_BY_LOGINNAME)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserById(id : string) : Promise<User>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_BY_ID) + id, {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_BY_ID)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserAllStatsById(id : string) : Promise<User>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_ALL_STATS_BY_ID) + id, {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_ALL_STATS_BY_ID)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async validate() : Promise<{"user_id":string}>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.VALIDATE), {credentials: "include", signal: ApiService.setupController(apiUrl.VALIDATE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async manualValidate(value : string) : Promise<number>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.MANUAL_VALIDATE).replace("VALUE", value), {credentials: "include", signal: ApiService.setupController(apiUrl.MANUAL_VALIDATE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getAllTagsWithStats() : Promise<TagRanking[]>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_TAGS_WITH_STATS), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_TAGS_WITH_STATS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getAllTagsPostCount(percentage : number) : Promise<Map<string, number>>{
    this.setLoading();
    return await fetch(ApiService.setupRequest((apiUrl.GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL) + percentage) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_TAGS_POST_COUNT_CLAMPED_PERCENTAGE_ALL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getAllUsers(page: number, size: number, search: string, filter: { sort: string, accType: string, usrType: string }, signal: AbortSignal) {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USERS_ALL).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("SEARCH", search).replace("ACCFILTER", filter.accType).replace("USRFILTER", filter.usrType).replace("SORTER", filter.sort), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_ALL)}).then(res =>{this.setFinished(res.status, res.url); return res.json()}).then((res : {users: any[], count : number}) => {
      ApiService.Users = res.users;
      return res;
    }, reason => {
      console.log(reason);
      return {users: [], count: 0}});
  }

  async getUserPostsWithStats(id : string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_PER_USER_WITH_STATS) + id, {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_PER_USER_WITH_STATS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserPostsPaged(id : string, page : number, size : number, filter : string, search : string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_BY_AUTHOR).replace("ID", id).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_BY_AUTHOR)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getPostsByIDs(list : string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_BY_IDS).replace("LIST", list), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_BY_IDS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserImgSrc(id : string){
    this.setLoading();
    const blob = await fetch(ApiService.setupRequest(apiUrl.GET_USER_IMG) + id).then(res => {this.setFinished(res.status, res.url); return res.blob()});
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
    return fetch(ApiService.setupRequest(apiUrl.GET_USER_CLICKS + id) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_CLICKS)}).then(res => {this.setFinished(res.status, res.url); return res.json()}).catch(reason => {return "NO DATA"});
  }

  async getUserBestPost(id: string, type: string){
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_POST_BY_USERS_BEST).replace("ID", id).replace("TYPE", type), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POST_BY_USERS_BEST)}).then(res => {this.setFinished(res.status, res.url); return res.json()}).catch(reason => {return new Post()});
  }

  async getUserNewestPost(id: string): Promise<Post> {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_POSTS_NEWEST_BY_USER_WITH_STATS) + id, {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_NEWEST_BY_USER_WITH_STATS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserAccountTypes() : Promise<Map<string, number>>{
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_USERS_ACCOUNTTYPES_ALL), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_ACCOUNTTYPES_ALL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async hasUserPost(id : number) {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.HAS_USER_POST) + id, {credentials: "include", signal: ApiService.setupController(apiUrl.HAS_USER_POST)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async hasUserPostTypes(id: string) : Promise<{ blog: boolean; news: boolean; artikel: boolean; podcast: boolean; whitepaper: boolean }> {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.HAS_USER_POST_TYPES) + id, {credentials: "include", signal: ApiService.setupController(apiUrl.HAS_USER_POST_TYPES)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserProfileCompletion(id : string): Promise<ProfileState> {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_USER_PROFILE_COMPLETION).replace("USERID", id), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_PROFILE_COMPLETION)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserClicksChartData(id : string, start : string, end : string): Promise<{date: string, profileViews: number, biggestPost: {id: number, title: string, type: string, clicks: number}, posts: {id: number, title: string, type: string, clicks: number}[]}[]> {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_USER_PROFILE_AND_POSTS_BY_DATE).replace("ID", id).replace("START", start).replace("END", end), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_PROFILE_AND_POSTS_BY_DATE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserEventCount(id : string) {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_USER_EVENTCOUNT).replace("ID", id), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_EVENTCOUNT)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserEventCountYesterday(id : string) {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_USER_EVENTCOUNT_CREATED_YESTERDAY).replace("ID", id), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_EVENTCOUNT_CREATED_YESTERDAY)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserPostCountByType(id : string) {
    this.setLoading();
    return fetch(ApiService.setupRequest(apiUrl.GET_USER_POSTCOUNT_BY_TYPE).replace("ID", id), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_POSTCOUNT_BY_TYPE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getMaxPerformance(){
    this.setLoading();
    let max : Promise<number> = await fetch(ApiService.setupRequest(apiUrl.GET_POST_MAX_PERFORMANCE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POST_MAX_PERFORMANCE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
    return max;
  }
  async getMaxRelevance(){
    this.setLoading();
    let max : Promise<number> = await fetch(ApiService.setupRequest(apiUrl.GET_POST_MAX_RELEVANCE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POST_MAX_RELEVANCE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
    return max;
  }
  async getPostViewsByTime(id : string) : Promise<{dates: string[], views: number[]}>{
    this.setLoading();
    return  await fetch(ApiService.setupRequest(apiUrl.GET_POST_VIEWS_BY_TIME).replace("ID", id), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POST_VIEWS_BY_TIME)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
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
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_ALL), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_ALL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getPostsAllPaged(page : number, size : number, sorter : string, filter : string, search : string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_ALL_PAGED).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("SORTER", sorter).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_ALL_PAGED)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getPostByIdWithContent(id: string) : Promise<Post> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POST_WITH_CONTENT).replace("ID", String(id)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POST_WITH_CONTENT)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewestPost() : Promise<Post> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POST_NEWEST), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POST_NEWEST)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getPostsPerType() : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_PER_TYPE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_PER_TYPE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getPostsPerTypeYesterday() : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_PER_TYPE_YESTERDAY), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_PER_TYPE_YESTERDAY)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getOriginMapByUser(id : number, start : string, end : string){
    this.setLoading();
    return await  fetch(ApiService.setupRequest(apiUrl.GET_GEO_USER_BY_DATES).replace("USRID", String(id)).replace("START", start).replace("END", end), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_USER_BY_DATES)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getGeoAll() : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_GERMANY_ALL_TIME)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_GERMANY_ALL_TIME)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByDates(start : string, end : string) : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_GERMANY_BY_DATES).replace("START", start).replace("END", end)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_GERMANY_BY_DATES)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByRegion(region : string) : Promise<Map<string,number>> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION).replace("REGION", region)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByRegionByDates(region : string, start? : string, end? : string) : Promise<Map<string,number>> {
    this.setLoading();
    if (start && end) return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES).replace("REGION", region).replace("START", start).replace("END", end)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
    else return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION).replace("REGION", region)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoByRegionByDatesListed(region : string, start : string, end : string) : Promise<{ data: number[], dates : string[] }> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES_LISTED).replace("REGION", region).replace("START", start).replace("END", end)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_GERMANY_ALL_TIME_BY_REGION_BY_DATES_LISTED)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoStart() : Promise<string> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_FIRST_TIMESTAMP)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_FIRST_TIMESTAMP)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoEnd() : Promise<string> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_LAST_TIMESTAMP)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_LAST_TIMESTAMP)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getGeoTimespan() : Promise<string[]> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_GEO_TIMESPAN)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_GEO_TIMESPAN)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getPodcastsAll(page : number, size : number) : Promise<Post[]> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_PODCAST_ALL).replace("PAGE", String(page)).replace("SIZE", String(size))) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_PODCAST_ALL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getRatgeberAll() : Promise<Post[]> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_RATGEBER_ALL)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_RATGEBER_ALL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getClicksByTime(id : number){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_VIEWS_PER_HOUR)+ id, {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_VIEWS_PER_HOUR)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserRankings(id: string) : Promise<{
    rankingContent: number;
    rankingContentByGroup: number;
    rankingProfile: number;
    rankingProfileByGroup: number
  }>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_RANKINGS).replace("ID", id), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_RANKINGS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getClicksByTimeAll() : Promise<number[]>{
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_USERS_ALL_VIEWS_PER_HOUR)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_ALL_VIEWS_PER_HOUR)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getTagStatsByID(id: number, timeSpan: number) : Promise<TagStats[]> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_TAGSTATS_BY_ID).replace("ID", String(id)).replace("DAYS", String(timeSpan)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_TAGSTATS_BY_ID)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getCallupsByTime(days: number) : Promise<Callup[]> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_CALLUPS_BY_TIME.replace("DAYS", String(days))), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_CALLUPS_BY_TIME)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallupsByCategoriesNewest() : Promise<CategoriesData>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_CALLUP_CATEGORIES_BY_DATE).replace("DATE", Util.getFormattedNow()), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_CALLUP_CATEGORIES_BY_DATE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallupsByCategoriesByDateTime(date : string, hour : number) :Promise<CategoriesData>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_CALLUP_CATEGORIES_BY_DATETIME).replace("DATE", date).replace("HOUR", String(hour)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_CALLUP_CATEGORIES_BY_DATETIME)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallupsByCategoriesByDate(date : string) :Promise<CategoriesData>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_CALLUP_CATEGORIES_BY_DATE).replace("DATE", date), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_CALLUP_CATEGORIES_BY_DATE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getCallpusByCategoriesAllTime() : Promise<CategoriesData> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_CALLUP_CATEGORIES_ALL_TIME), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_CALLUP_CATEGORIES_ALL_TIME)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserAccountTypesYesterday() {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USERS_ACCOUNTTYPES_YESTERDAY), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_ACCOUNTTYPES_YESTERDAY)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserAccountTypesAllNew() : Promise<{ohne: string[], basis: string[], "basis-plus": string[], plus: string[], premium: string[], sponsor: string[]}>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USERS_ACCOUNTTYPES_ALL_NEW), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_ACCOUNTTYPES_ALL_NEW)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getTopPostsBySorterWithType(sorter: string, type: string, limit: number) : Promise<Post[]>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_POSTS_TOP_BY_SORTER).replace("SORTER", sorter).replace("TYPE", type).replace("LIMIT", String(limit)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_POSTS_TOP_BY_SORTER)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getNewsletterSubs(){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER_SUBS), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER_SUBS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterSubsByDateRange(daysBackTo : number, daysBackFrom : number){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER_SUBS_BY_DATERANGE).replace("DAYSBACKTO", String(daysBackTo)).replace("DAYSBACKFROM", String(daysBackFrom)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER_SUBS_BY_DATERANGE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterSubsYesterday(){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER_SUBS_YESTERDAY), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER_SUBS_YESTERDAY)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterSubsAsMailByStatus(c : string) : Promise<string[]>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER_SUBS_AS_MAIL_BY_STATUS).replace("STATUS", c), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER_SUBS_AS_MAIL_BY_STATUS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletters(page : number, size : number) : Promise<Newsletter[]>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTERS_ALL).replace("PAGE", String(page)).replace("SIZE", String(size)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTERS_ALL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewslettersGeo() : Promise<Map<string,number>>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTERS_ALL_GEO), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTERS_ALL_GEO)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletter(id : number) : Promise<Newsletter>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER).replace("ID", String(id)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getLatestNewsletter() : Promise<Newsletter>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER_LATEST), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER_LATEST)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewsletterGeo(id : number) : Promise<Map<string,number>>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTER_GEO).replace("ID", String(id)), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTER_GEO)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewslettersOpenTimes() : Promise<number[]>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTERS_ALL_OPENTIMES), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTERS_ALL_OPENTIMES)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getNewslettersOR() : Promise<number>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_NEWSLETTERS_ALL_OPENRATE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_NEWSLETTERS_ALL_OPENRATE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getSystemTimeHour() : Promise<number>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_SYSTEM_TIME_HOUR), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SYSTEM_TIME_HOUR)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSystemUsage() : Promise<SystemUsage>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_SYSTEM_USAGE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SYSTEM_USAGE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSystemUsageNow() : Promise<{cpu : number, memory : number, networkSent : number, networkRecv : number}>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_SYSTEM_USAGE_NOW), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SYSTEM_USAGE_NOW)}).then(res => {
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
  async getBlackHoleData() : Promise<BadBot[]>{
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_SYSTEM_BAD_BOTS), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SYSTEM_BAD_BOTS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getEvents() : Promise<string[]> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_EVENTS), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_EVENTS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getEventsYesterday() : Promise<string[]> {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_EVENTS_YESTERDAY), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_EVENTS_YESTERDAY)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getEventsLikePostsPaged(page : number, size : number,filter : string, search: string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_EVENTS_LIKE_POSTS).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_EVENTS_LIKE_POSTS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserEventsLikePostsPaged(id : string, page : number, size : number,filter : string, search: string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_EVENTS_LIKE_POSTS).replace("ID", id).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("FILTER", filter).replace("SEARCH", search), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_EVENTS_LIKE_POSTS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserProfileViewsAverageByType() {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USERS_PROFILE_VIEWS_AVERAGE_BY_TYPE_BY_POSTHAVING), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_PROFILE_VIEWS_AVERAGE_BY_TYPE_BY_POSTHAVING)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserTagsDistributionPercentage() {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USERS_TAG_DISTRIBUTION_PRECENTAGE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_TAG_DISTRIBUTION_PRECENTAGE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getUserTagsRanking(id : string, sorter: string){
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USER_TAG_DISTRIBUTION_PRECENTAGE).replace("ID", id).replace("SORTER", sorter), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USER_TAG_DISTRIBUTION_PRECENTAGE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async getUserClicksAverageByViewType() {
    this.setLoading();
    return await fetch(ApiService.setupRequest(apiUrl.GET_USERS_Clicks_AVERAGE_BY_VIEWTYPE), {credentials: "include", signal: ApiService.setupController(apiUrl.GET_USERS_Clicks_AVERAGE_BY_VIEWTYPE)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async loginSeo() {
    if (this.seoToken == "") {
      this.setLoading();
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
        "credentials": "omit",
        signal : ApiService.setupController("SEO_LOGIN")
      }).then(res => {this.setFinished(res.status, res.url); return res.json()}).then((value : {token : string} ) => {this.seoToken = value.token; return false})
    }
    else return true;
  }

  async getSeoIndexOverTime(isMobile : string) : Promise<{id : any, sichtbarkeitsindex : number, date : number}[]> {
    this.setLoading();
    return this.loginSeo().then(value => {
      return fetch("https://seo.internet-sicherheit.de/api/sistrix/domain/sichtbarkeitsindexHistory/it-sicherheit.de?isMobile=" + isMobile, {
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
        "credentials": "omit",
        signal: ApiService.setupController("SEO_INDEX" + isMobile)
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
        "credentials": "omit",
        signal : ApiService.setupController("SEO_CTR")
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
        "credentials": "omit",
        signal : ApiService.setupController("SEO_KEYWORDS")
    }).then(res => {
      this.setFinished(res.status, res.url);
      return res.json();
    });
    });
  }

  async getSearchesWithoutResults(page : number, size : number) : Promise<SearchItem[]> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_SEARCHES_NO_RESULTS).replace("PAGE", String(page)).replace("SIZE", String(size))) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SEARCHES_NO_RESULTS)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSearchesAnbieterWithoutResults(page : number, size : number) : Promise<SearchAnbieterItem[]> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_SEARCHES_ANBIETER_NO_RESULT).replace("PAGE", String(page)).replace("SIZE", String(size))) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SEARCHES_ANBIETER_NO_RESULT)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async getSearchesCool(page : number, size : number, sorter : string, dir : string) : Promise<SearchSS[]> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.GET_SEARCHES_COOL).replace("PAGE", String(page)).replace("SIZE", String(size)).replace("SORTER", sorter).replace("DIR", dir)) , {credentials: "include", signal: ApiService.setupController(apiUrl.GET_SEARCHES_COOL)}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }

  async ignoreSearch(search : string) : Promise<boolean> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.SEARCH_IGNORE).replace("SEARCH", search)) , {credentials: "include", method: "post"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async flipSearch(id : string) : Promise<string> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.SEARCH_FLIP).replace("SEARCH", id)) , {credentials: "include", method: "get"}).then(res => {this.setFinished(res.status, res.url); return res.text()});
  }
  async ignoreAnbieterSearch(id : string) : Promise<boolean> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.SEARCH_USER_IGNORE).replace("ID", id)) , {credentials: "include", method: "post"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }
  async flipAnbieterSearch(id : string) : Promise<{ city: string, query : string }> {
    this.setLoading();
    return await fetch((ApiService.setupRequest(apiUrl.SEARCH_USER_FLIP).replace("SEARCH", id)) , {credentials: "include", method: "get"}).then(res => {this.setFinished(res.status, res.url); return res.json()});
  }



}