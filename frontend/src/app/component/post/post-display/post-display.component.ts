import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Post} from "../Post";

@Component({
  selector: 'dash-post-display',
  templateUrl: './post-display.component.html',
  styleUrls: ['./post-display.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostDisplayComponent extends DashBaseComponent implements OnInit{
  post = new Post();

  ngOnInit(): void {
    this.db.getPostByIdWithContent(35154).then(res => {
      this.post = res;
    })
  }

}
