import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ChartComponent, ChartElements} from "../../component/chart/chart.component";
import {DbService} from "../../services/db.service";
import {Subject} from "rxjs";
import {CookieService} from "ngx-cookie-service";
import {UserService} from "../../services/user.service";
import {ClicksComponent} from "../../component/clicks/clicks.component";
import {PostChartComponent} from "../../component/post-chart/post-chart.component";
import {GaugeComponent} from "../../component/gauge/gauge.component";
import {RelevanceComponent} from "../../component/gauge/relevance/relevance.component";
import {PostComponent} from "../../component/post/post.component";
import {PotentialComponent} from "../../component/potential/potential.component";
import {GridCard} from "../../grid/GridCard";

@Component({
  selector: 'dash-page-kennzahlen',
  templateUrl: './page-kennzahlen.component.html',
  styleUrls: ['./page-kennzahlen.component.css']
})
export class PageKennzahlenComponent implements OnInit{

  cardsLoaded = new Subject<GridCard[]>();
  cards : GridCard[];

  constructor() {
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

  ngOnInit(): void {
    this.cardsLoaded.next(this.cards);
  }

}
