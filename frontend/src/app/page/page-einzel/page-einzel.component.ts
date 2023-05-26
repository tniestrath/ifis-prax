import {Component, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {SelectorItem} from "../selector/selector.component";
import {DbService} from "../../services/db.service";
import {User, UserComponent} from "./user/user.component";
import {Subject} from "rxjs";
import {Post} from "../../Post";
import {ChartElements} from "../../component/chart/chart.component";
import {ClicksComponent} from "../../component/clicks/clicks.component";
import {PostChartComponent} from "../../component/post-chart/post-chart.component";
import {PerformanceComponent} from "../../component/performance/performance.component";
import {TagListComponent} from "../../component/tag-list/tag-list.component";
import {GridCard} from "../../grid/GridCard";

@Component({
  selector: 'dash-page-einzel',
  templateUrl: './page-einzel.component.html',
  styleUrls: ['./page-einzel.component.css']
})
export class PageEinzelComponent implements OnInit {
  displayContent: string = "none";

  selectorItems : SelectorItem[] = [];
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  searchValue = "";

  postPerDayLabel : string[] = []
  postsPerDayData : number[]  = [];
  postsPerDayTitle : string[] = [];
  postPerDayLoaded = new Subject<ChartElements>();
  cardsLoaded = new Subject<GridCard[]>();
  cards : GridCard[];

  constructor(private cookieService : CookieService, private db : DbService) {
    this.cards = [
      {type: ClicksComponent, row: 1, col: 1, height: 2, width: 2},
      //@ts-ignore
      {type: PostChartComponent, row: 1, col: 3, height: 1, width: 3},
      //@ts-ignore
      {type: PerformanceComponent, row: 2, col: 3, height: 1, width: 1},
      {type: PerformanceComponent, row: 2, col: 4, height: 1, width: 1},
      //@ts-ignore
      {type: TagListComponent, row: 1, col: 6, height: 1, width: 1}
    ];
  }

  onSelected(id: string, name: string){
    if (id != "0"){
      this.displayContent = "grid";
      this.cardsLoaded.next(this.cards);
    } else {
      this.displayContent = "none";
    }
    this.db.getUserPostsDay(id).then(res => {
      this.postPerDayLabel = [];
      this.postsPerDayData = [];
      for (let post of res) {
        this.postPerDayLabel.push((post as Post).date);
        this.postsPerDayData.push(Number((post as Post).count));
        this.postsPerDayTitle.push((post as Post).title);
      }
    }).finally(() =>
      this.postPerDayLoaded.next(new ChartElements(this.postPerDayLabel, this.postsPerDayData, this.postsPerDayTitle)));
  }

  onSearchInput(value : string){
    this.searchValue = value;
    this.loadSelector();
  }

  ngOnInit(): void {
    this.loadSelector();
  }

  loadSelector(){
    this.db.loadAllUsers().then(() => {
      this.selectorItems = [];
      for (let u of DbService.Users) {
        this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, u.img)));
      }
    }).then(() => {
      this.selectorItems = this.selectorItems.filter(item => item.data.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
                                                    (item.data as User).email.toUpperCase().includes(this.searchValue.toUpperCase()))
    }).finally(() =>
      this.selectorItemsLoaded.next(this.selectorItems));
  }
}
