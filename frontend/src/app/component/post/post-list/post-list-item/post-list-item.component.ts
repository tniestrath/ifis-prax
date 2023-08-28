import { Component } from '@angular/core';
import {Post} from "../../Post";

@Component({
  selector: 'dash-post-list-item',
  templateUrl: './post-list-item.component.html',
  styleUrls: ['./post-list-item.component.css']
})
export class PostListItemComponent {
  data: Post = new Post();
  formattedDate = new Date(this.data.date).toLocaleDateString();
  formattedTags = this.data.tags?.toString().replace("[", "").replace("]", "");
  /*formattedPerformance = (this.data.performance / maxPerf) * 100;
  formattedRelevanz = (this.data.relevance / maxRel) * 100;*/
  // @ts-ignore
  formattedSSR = this.data.searchSuccessRate * 100;

  formattedPerformance = 0;
  formattedRelevanz = 0;
}
