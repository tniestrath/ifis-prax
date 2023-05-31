import { Component } from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "../../Post";

@Component({
  selector: 'dash-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css', "../../component/dash-base/dash-base.component.html"]
})
export class PostComponent extends DashBaseComponent{
  post: Post = new Post("Superlanger titel der super lang ist um lange titel zu testen, is aber noch nicht lang genug", "10/10/2010", "article", "1", ["tag1", "tag2"], 2, 0.1);

  removePost() {
    // @ts-ignore
    this.grid_reference?.removeCard(this.grid_index);
  }
}
