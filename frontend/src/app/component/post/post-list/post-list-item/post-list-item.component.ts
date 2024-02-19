import {Component, OnInit} from '@angular/core';
import {Post} from "../../Post";
import Util, {DashColors} from "../../../../util/Util";
import {SysVars} from "../../../../services/sys-vars-service";

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
    this.typeColor = Util.getColor("post", this.data.type);
    this.formattedDate = new Date(this.data.date).toLocaleDateString();
    if (this.data.tags != null && this.data.tags.length > 0){
      this.formattedTags = this.data.tags?.toString().replace("[", "").replace("]", "");
    }
  }


  onClick(data: Post) {
    SysVars.SELECTED_POST.next(data);
  }
}
