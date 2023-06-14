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
import {GaugeComponent} from "../../component/gauge/gauge.component";
import {TagListComponent} from "../../component/tag-list/tag-list.component";
import {GridCard} from "../../grid/GridCard";
import {RelevanceComponent} from "../../component/gauge/relevance/relevance.component";
import {PostComponent} from "../../component/post/post.component";
import {UserService} from "../../services/user.service";
import {PotentialComponent} from "../../component/potential/potential.component";

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

  constructor(private cookieService : CookieService, private db : DbService, private us : UserService) {
    this.cards = [
      {type: ClicksComponent, row: 1, col: 1, height: 4, width: 1},
      //@ts-ignore
      {type: PostChartComponent, row: 1, col: 2, height: 2, width: 4},
      //@ts-ignore
      {type: GaugeComponent, row: 4, col: 6, height: 1, width: 1},
      {type: RelevanceComponent, row: 3, col: 6, height: 1, width: 1},
      //@ts-ignore
      {type: PostComponent, row: 1, col: 6, height: 2, width: 1},
      {type: PotentialComponent, row: 3, col: 2, height: 2, width: 4}
    ];
  }

  onSelected(id: string, name: string){
    if (id != "0"){
      this.displayContent = "grid";
      this.cardsLoaded.next(this.cards);
    } else {
      this.displayContent = "none";
    }
    UserService.USER_ID = id;
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
        this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, "Premium", 0, 50, u.img)));
      }
    }).then(() => {
      this.selectorItems = this.selectorItems.filter(item => item.data.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
                                                    (item.data as User).email.toUpperCase().includes(this.searchValue.toUpperCase()))
    }).finally(() =>
      this.selectorItemsLoaded.next(this.selectorItems));
  }
}
