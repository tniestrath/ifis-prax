import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";
import {DbObject} from "../../../services/DbObject";

export class ForumStats extends DbObject{
  public forums : number;
  public topics : number;
  public topicsClosed : number;
  public topicsAnswered : number;
  public questions : number;
  public answers : number;


  constructor(forums : number, topics : number, topicsClosed : number, topicsAnswered : number, questions : number, answers : number) {
    super("0", "name");
    this.topics = topics;
    this.forums = forums;
    this.topicsClosed = topicsClosed;
    this.topicsAnswered = topicsAnswered;
    this.questions = questions;
    this.answers = answers;
  }
}

@Component({
  selector: 'dash-forum-stats',
  templateUrl: './forum-stats.component.html',
  styleUrls: ['./forum-stats.component.css', '../../dash-base/dash-base.component.css']
})
export class ForumStatsComponent extends DashBaseComponent implements OnInit{
  data : ForumStats = new ForumStats(0,0,0,0,0,0);

  ngOnInit(): void {
    this.api.getForumStats().then(res => {
      this.data = res;
      this.cdr.detectChanges();
    });
  }

  protected readonly Util = Util;
  protected readonly DashColors = DashColors;
}
