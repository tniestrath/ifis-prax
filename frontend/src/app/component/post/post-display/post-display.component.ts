import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Post} from "../Post";
import {SysVars} from "../../../services/sys-vars-service";
import Util from "../../../util/Util";

@Component({
  selector: 'dash-post-display',
  templateUrl: './post-display.component.html',
  styleUrls: ['./post-display.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostDisplayComponent extends DashBaseComponent implements OnInit{

  ngOnInit(): void {
    this.setToolTip("", 1,false);
    this.db.getNewestPost().then(res => {
      this.formatPost(res);
    });
    SysVars.SELECTED_POST.subscribe(post => {
      this.db.getPostByIdWithContent(post.id).then(res => {
        this.formatPost(res);
      });
    })


  }

  private formatPost(post: Post) {
    var contentBox = document.getElementById("post-display-content");
    var authorBox = document.getElementById("post-display-author");
    var dateBox = document.getElementById("post-display-date");
    var tagBox = document.getElementById("post-display-tags");
    var imgBox = document.getElementById("post-display-img");

    var content = post.content.substring(0, post.content.indexOf("<h2>"));
    if (!content.startsWith("<strong>")) {
      content = "<strong>" + post.title + "</strong>" + content;
    }
    // @ts-ignore
    contentBox.innerHTML = content.replace("</strong>", "</strong><br>").replace("</em>", "</em><br><br>");
    // @ts-ignore
    authorBox.innerText = post.authors;
    // @ts-ignore
    dateBox.innerText = post.date;
    // @ts-ignore
    tagBox.innerText = post.tags;
    // @ts-ignore
    tagBox.style.backgroundColor = Util.getColor("post", post.type);
    // @ts-ignore
    imgBox.style.background = "url('" + post.img +"')";
  }

}
