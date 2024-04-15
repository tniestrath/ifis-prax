import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "./Post";
import {SysVars} from "../../services/sys-vars-service";
import Util from "../../util/Util";

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
  identifier: string = "";

  ngOnInit(): void {
    this.setToolTip("Hier werden Ihnen Einzelheiten zu einem Beitrag angezeigt.<br><br>" +
      "Sie können links in der Liste einen Beitrag auswählen, um hier Details anzeigen zu lassen.", 2);
    this.api.getNewestPost().then(res => {
        this.formatPost(res, false);
    });

    SysVars.SELECTED_POST.subscribe(post => {
          this.formatPost(post, true);
      });
  }

  formatPost(res : Post, isSelected : boolean){
    if (isSelected){
      switch (res.type) {
        case "artikel": this.identifier = "Ausgewählter Artikel";
          break;
        case "blog": this.identifier = "Ausgewählter Blog Eintrag";
          break;
        case "news": this.identifier = "Ausgewählter News Beitrag";
          break;
        case "podcast": this.identifier = "Ausgewählter Podcast";
          break;
        case "whitepaper": this.identifier = "Ausgewähltes Whitepaper";
          break;
      }
    } else {
      switch (res.type) {
        default: this.identifier = "Der neueste Beitrag auf dem Marktplatz";
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
    this.post = res;
  }

  protected readonly Util = Util;
}
