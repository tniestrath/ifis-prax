import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";

export class ForumStats{
  public forums : ForumStat[];
  public topics : ForumStat[];
  public cats : ForumStat[];


  constructor(forums: ForumStat[] = [], topics: ForumStat[] = [], cats: ForumStat[] = []) {
    this.forums = forums;
    this.topics = topics;
    this.cats = cats;
  }
}
export class ForumStat {
  public name : string;
  public forumId : number;
  public topicId : number;
  public catId : number;
  public count : number;


  constructor(name: string, forumId: number, topicId: number, catId: number, count: number) {
    this.name = name;
    this.forumId = forumId;
    this.topicId = topicId;
    this.catId = catId;
    this.count = count;
  }
}

@Component({
  selector: 'dash-forum-stats',
  templateUrl: './forum-stats.component.html',
  styleUrls: ['./forum-stats.component.css', '../../dash-base/dash-base.component.css']
})
export class ForumStatsComponent extends DashBaseComponent implements OnInit{
  data : ForumStats = new ForumStats();

  ngOnInit(): void {
    this.api.getForumStats().then(res => {
      this.data = res;
      this.cdr.detectChanges();
    });
  }

  protected readonly Util = Util;
  protected readonly DashColors = DashColors;
}
