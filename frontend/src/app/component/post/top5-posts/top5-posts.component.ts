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
    /*this.db.getTopPostsBySorterWithLimit("performance", 5).then(res => {
      this.dataArray = res;
    })*/

    this.dataArray = [
      {id: 0, title: "awfaegae aw daw da", date: "11.09.2000", clicks: 123, lettercount: 123, authors: "a,b,c", tags: "d,e,f", type: "news", typColor: "#0f0"},
      {id: 0, title: "awfaegae aw daw da", date: "11.09.2000", clicks: 123, lettercount: 123, authors: "a,b,c", tags: "d,e,f", type: "post", typColor: "#f00"}
    ]
  }

}
