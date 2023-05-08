import {Component, OnInit} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {SelectorItem} from "../../user/selector/selector.component";
import {DbService} from "../../services/db.service";
import {User, UserComponent} from "../../user/user/user.component";
import {Subject} from "rxjs";
import {Post} from "../../Post";
import {ChartElements} from "../../component/chart/chart.component";

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
  postPerDayLoaded = new Subject<ChartElements>();

  constructor(private cookieService : CookieService, private db : DbService) {
  }

  onSelected(id: string, name: string){
    if (id != "0"){
      this.displayContent = "grid";
    } else {
      this.displayContent = "none";
    }
    this.db.getUserPostsDay(id).then(res => {
      this.postPerDayLabel = [];
      this.postsPerDayData = [];
      for (let post of res) {
        this.postPerDayLabel.push((post as Post).date);
        this.postsPerDayData.push(Number((post as Post).count));
      }
    }).finally(() =>
      this.postPerDayLoaded.next(new ChartElements(this.postPerDayLabel, this.postsPerDayData)));
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
        this.selectorItems.push(new SelectorItem(UserComponent, new User(u.id, u.email, u.displayName, this.db.getUserImgSrc(u.id))));
      }
    }).then(() => {
      this.selectorItems = this.selectorItems.filter(item => item.data.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
                                                    (item.data as User).email.toUpperCase().includes(this.searchValue.toUpperCase()))
    }).finally(() =>
      this.selectorItemsLoaded.next(this.selectorItems));
  }
}
