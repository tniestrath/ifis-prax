import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Post} from "../Post";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-post-display',
  templateUrl: './post-display.component.html',
  styleUrls: ['./post-display.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostDisplayComponent extends DashBaseComponent implements OnInit{

  ngOnInit(): void {
    var display = document.getElementById("post-display-box");

    this.db.getNewestPost().then(res => {
      this.formatPost(res, display);
    });
    SysVars.SELECTED_POST.subscribe(post => {
      this.db.getPostByIdWithContent(post.id).then(res => {
        this.formatPost(res, display);
      });
    })


  }

  private formatPost(post: Post, display: HTMLElement | null) {
    var content = post.content.substring(0, post.content.indexOf("<h2>"));
    if (!content.startsWith("<strong>")) {
      content = "<strong>" + post.title + "</strong>" + content;
    }
    // @ts-ignore
    display.innerHTML = content.replace("</strong>", "</strong><br>").replace("</em>", "</em><br><br>");
  }
}
