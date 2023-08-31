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
  formattedDate = new Date(this.data.date).toLocaleDateString();
  formattedTags = this.data.tags?.toString().replace("[", "").replace("]", "");
  typeColor = "rgb(148,28,62)";
  bgColor: string = "#FFFFFF";

  ngOnInit(): void {
    switch (this.data.type) {
      case "article": {
        this.typeColor = DashColors.Red;
        break;
      }
      case "blog": {
        this.typeColor = DashColors.Blue;
        break;
      }
      case "news": {
        this.typeColor = DashColors.Black;
        break;
      }
    }
    this.formattedDate = new Date(this.data.date).toLocaleDateString();
    this.formattedTags = this.data.tags?.toString().replace("[", "").replace("]", "");
  }
}
