import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "./Post";
import {SysVars} from "../../services/sys-vars-service";

@Component({
  selector: 'dash-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css', "../dash-base/dash-base.component.css"]
})
export class PostComponent extends DashBaseComponent implements OnInit{
  post: Post = new Post();
  formattedDate : string = "";
  formattedTags : string = "";
  formattedPerformance: number = 0;
  formattedRelevanz: number = 0;
  formattedSSR: number = 0;

  ngOnInit(): void {
    this.setToolTip("Hier werden Ihnen Einzelheiten zu einem Beitrag angezeigt.<br><br>" +
      "Sie können links in der Liste einen Beitrag auswählen, um hier Details anzeigen zu lassen.", 2);
    this.db.getNewestPost().then(res => {
        this.formatPost(res, false);
    });

    SysVars.SELECTED_POST.subscribe(post => {
          this.formatPost(post, true);
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
        case "podcast": res.type = "Ausgewählter Podcast";
          break;
        case "whitepaper": res.type = "Ausgewähltes Whitepaper";
          break;
      }
    } else {
      switch (res.type) {
        default: res.type = "Der neueste Beitrag auf dem Marktplatz";
          break;
        /*case "blog": res.type = "Der aktuellste Blog Eintrag";
          break;
        case "news": res.type = "Der aktuellste News Beitrag";
          break;
        case "podcast": res.type = "Der aktuellste Podcast";
          break;
        case "whitepaper": res.type = "Das aktuellste Whitepaper";
          break;*/
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
