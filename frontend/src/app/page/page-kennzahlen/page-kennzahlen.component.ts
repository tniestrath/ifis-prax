import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ChartComponent} from "../../component/chart/chart.component";
import {DbService} from "../../services/db.service";
import {Subject} from "rxjs";

@Component({
  selector: 'dash-page-kennzahlen',
  templateUrl: './page-kennzahlen.component.html',
  styleUrls: ['./page-kennzahlen.component.css']
})
export class PageKennzahlenComponent implements OnInit{

  tagRatingLabel : string[] = [];
  tagRatingData : number[] = [];

  tagRatingLoaded = new Subject<void>();

  constructor(private db : DbService) {
  }

  ngOnInit(): void {
    this.db.getTagRanking().then(res =>
    {
      for (let tr of res) {
        this.tagRatingLabel.push((tr as TagRating).name);
        this.tagRatingData.push((tr as TagRating).count);
      }
    }).finally(() =>
      this.tagRatingLoaded.next());
  }


}
interface TagRating {
  id : number;
  count : number;
  name : string;
}
