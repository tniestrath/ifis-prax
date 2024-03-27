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
  postImg: string = "";

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
    var imgBox = document.getElementById("post-display-img") as HTMLImageElement;

    var content = post.content.substring(0, post.content.indexOf("</em>"));
    if (!content.startsWith("<strong>")) {
      content = "<strong style= 'color: "+ Util.getColor('post', post.type) + " ' >" + post.title + "" + content;
    }
    // @ts-ignore
    contentBox.innerHTML = content.replace("<strong>", "<strong style= 'color: "+ Util.getColor('post', post.type) + " ' >").replace("</strong>", "</strong><br><br>").replace("</em>", "</em><br><br>");
    // @ts-ignore
    authorBox.innerText = post.authors[0].substring(0, post.authors[0].lastIndexOf(",") > 0 ? post.authors[0].lastIndexOf(",") : 25);
    // @ts-ignore
    dateBox.innerText = post.date;
    // @ts-ignore
    tagBox.innerText = Util.formatArray(post.tags);
    // @ts-ignore
    tagBox.style.backgroundColor = Util.getColor("post", post.type);
    // @ts-ignore
    this.postImg = post.img;
  }

}
