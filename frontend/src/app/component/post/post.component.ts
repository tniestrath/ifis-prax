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
        this.formatPost(res, false)
    });

    SysVars.SELECTED_POST_ID.subscribe(id => {
      this.db.getPostById(id).then(res => {
          this.formatPost(res, true)})
      });
  }

  formatPost(res : Post, isSelected : boolean){
    if (isSelected){
      switch (res.type) {
        case "artikel": res.type = "Ausgewählter Artikel";
          break;
        case "blog": res.type = "Ausgewählter Blog Eintrag";
          break;
        case "news": res.type = "Ausgewählter News Beitrag";
          break;
      }
    } else {
      switch (res.type) {
        case "artikel": res.type = "Ihr aktuellster Artikel";
          break;
        case "blog": res.type = "Ihr aktuellster Blog Eintrag";
          break;
        case "news": res.type = "Ihr aktuellster News Beitrag";
          break;
      }
    }
    this.formattedDate = new Date(res.date).toLocaleDateString();
    this.formattedTags = res.tags?.toString().replace("[", "").replace("]", "");
    this.formattedPerformance = res.performance * 100;
    this.formattedRelevanz = res.relevance * 100;
    // @ts-ignore
    this.formattedSSR = res.searchSuccessRate * 100;

    // @ts-ignore
    this.post = new Post(res.title, res.date, res.type, res.clicks, res.tags, res.performance, res.relevance, res.searchSuccesses, res.searchSuccessRate, res.referrings, res.articleReferringRate, res.lettercount, res.authors);
  }
}
