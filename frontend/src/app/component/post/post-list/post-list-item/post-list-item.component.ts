import {Component, OnInit} from '@angular/core';
import {Post} from "../../Post";
import Util, {DashColors} from "../../../../util/Util";

@Component({
  selector: 'dash-post-list-item',
  templateUrl: './post-list-item.component.html',
  styleUrls: ['./post-list-item.component.css']
})
export class PostListItemComponent implements OnInit{
  data: Post = new Post();
  formattedDate = "DATUM FEHLT";
  formattedTags = "KEINE TAGS";
  typeColor = DashColors.GREY;
  bgColor: string = "#FFFFFF";

  ngOnInit(): void {
    switch (this.data.type) {
      case "artikel": {
        this.typeColor = DashColors.ARTICLE;
        break;
      }
      case "blog": {
        this.typeColor = DashColors.BLOG;
        break;
      }
      case "news": {
        this.typeColor = DashColors.NEWS;
        break;
      }
      case "whitepaper": {
        this.typeColor = DashColors.WHITEPAPER;
        break;
      }
    }
    this.formattedDate = new Date(this.data.date).toLocaleDateString();
    if (this.data.tags != null && this.data.tags.length > 0){
      this.formattedTags = this.data.tags?.toString().replace("[", "").replace("]", "");
    }
  }
}
