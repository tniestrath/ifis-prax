import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "./Post";
import {SysVars} from "../../services/sys-vars-service";

@Component({
  selector: 'dash-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css', "../../component/dash-base/dash-base.component.html"]
})
export class PostComponent extends DashBaseComponent implements OnInit{
  post: Post = new Post();
  formattedDate : string = "";
  formattedTags : string = "";
  formattedPerformance: number = 0;
  formattedRelevanz: number = 0;
  formattedSSR: number = 0;

  ngOnInit(): void {
    this.setToolTip("Hier werden Ihnen Einzelheiten zu einem Post angezeigt. " +
      "Diesen können Sie links im Graphen anclicken um hier Details anzeigen zu lassen.");
    this.db.getUserNewestPost(SysVars.USER_ID).then(res => {
      Promise.all([this.db.getMaxPerformance(), this.db.getMaxRelevance()]).then(value => {
        this.formatPost(res, value[0], value[1], false)
      })
    });

    SysVars.SELECTED_POST_ID.subscribe(id => {
      Promise.all([this.db.getMaxPerformance(), this.db.getMaxRelevance()]).then(value =>
      {
        this.db.getPostById(id).then(res => {
          this.formatPost(res, value[0], value[1], true)})
      })

    });
  }

  formatPost(res : Post, maxPerf : number, maxRel : number, isSelected : boolean){
    if (isSelected){
      switch (res.type) {
        case "artikel": res.type = "Ausgewählter Artikel";
          break;
        case "blog": res.type = "Ausgewählter Blog Eintrag";
          break;
        case "news": res.type = "Ausgewählte News";
          break;
      }
    } else {
      switch (res.type) {
        case "artikel": res.type = "Ihr aktuellster Artikel";
          break;
        case "blog": res.type = "Ihr aktuellster Blog Eintrag";
          break;
        case "news": res.type = "Ihre aktuellster News Beitrag";
          break;
      }
    }
    this.formattedDate = new Date(res.date).toLocaleDateString();
    this.formattedTags = res.tags?.toString().replace("[", "").replace("]", "");
    this.formattedPerformance = (res.performance / maxPerf) * 100;
    this.formattedRelevanz = (res.relevance / maxRel) * 100;
    // @ts-ignore
    this.formattedSSR = res.searchSuccessRate * 100;

    // @ts-ignore
    this.post = new Post(res.title, res.date, res.type, res.clicks, res.tags, res.performance, res.relevance, res.searchSuccesses, res.searchSuccessRate, res.referrings, res.articleReferringRate, res.lettercount);
  }
}
