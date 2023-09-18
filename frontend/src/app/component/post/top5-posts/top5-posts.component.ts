import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";

@Component({
  selector: 'dash-top5-posts',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5PostsComponent extends DashBaseComponent implements OnInit{
  dataArray: {
    id: number;
    title: string;
    date: string;
    clicks: number;
    lettercount: number;
    authors: string;
    tags: string;
    type: string;
    typColor: string;
  }[] | undefined;

  ngOnInit(): void {
    this.db.getTopPostsBySorterWithType("performance", "artikel").then(res => {
      this.dataArray = res;
    });
  }

}
