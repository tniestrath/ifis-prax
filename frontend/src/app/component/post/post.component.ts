import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {Post} from "../../Post";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'dash-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css', "../../component/dash-base/dash-base.component.html"]
})
export class PostComponent extends DashBaseComponent implements OnInit{
  post: Post = new Post("Superlanger titel der super lang ist um lange titel zu testen, is aber noch nicht lang genug", "10/10/2010", "article", "1", ["tag1", "tag2"], 2, 0.1);


  removePost() {
  }

  ngOnInit(): void {

    UserService.SELECTED_POST_ID.subscribe( id => {
      Promise.all([this.db.getMaxPerformance(), this.db.getMaxRelevance()]).then(value =>
      {
        this.db.getPostById(id).then(res => {
          switch (res.type) {
            case "artikel": res.type = "Ausgewählter Artikel";
              break;
            case "blog": res.type = "Ausgewählter Blog Eintrag";
              break;
            case "pressemitteilung": res.type = "Ausgewählte Pressemitteilung";
            break;
          }
          this.post = new Post(res.title, res.date, res.type, res.clicks, res.tags, ((res.performance / value[0])*100), (res.relevance / value[1])*100);
        })
      })

    })

  }
}
