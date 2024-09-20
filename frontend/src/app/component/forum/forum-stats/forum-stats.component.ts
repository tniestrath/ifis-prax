import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";
import {SysVars} from "../../../services/sys-vars-service";

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
  styleUrls: ['./forum-stats.component.css', '../../dash-base/dash-base.component.css'],
  changeDetection : ChangeDetectionStrategy.OnPush
})
export class ForumStatsComponent extends DashBaseComponent implements OnInit{
  data : ForumStats = new ForumStats();

  ngOnInit(): void {
    this.setToolTip("Hier können die unmoderierten Beiträge nach Teilbereich eingesehen und ausgewählt werden. <br>" +
      "Der Effekt der Auswauhl ist in der Liste der Beiträge rechts zu sehen. <br><br>" +
      "Eine Auswahl wird mittels eines Klicks getroffen. Teilbereiche ohne unmoderierte Beiträge stehen nicht zur Auswahl.");
    this.api.getForumStats().then(res => {
      this.data = res;
      this.cdr.detectChanges();
    });

    SysVars.FORUM_UPDATE_STATS.subscribe(() =>{
      this.api.getForumStats().then(res => {
        this.data = res;
        this.cdr.detectChanges();
      });
    });
  }

  selectStat(stat : ForumStat){
    if (stat.count > 0){
      SysVars.SELECTED_FORUM_FILTER.next(stat);
    }
  }

  isHover(stat : ForumStat){
    if (stat.count <= 0){
      return "noHover";
    } return "";
  }

  protected readonly Util = Util;
  protected readonly DashColors = DashColors;
}
