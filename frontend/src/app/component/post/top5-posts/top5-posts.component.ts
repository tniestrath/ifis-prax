import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";
import {Post, PostWithTypeColor} from "../Post";

@Component({
  selector: 'dash-top5-posts',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5PostsComponent extends DashBaseComponent implements OnInit{
  dataArray: PostWithTypeColor[] = [];

  postType: string = "Beitrag";
  sorter: string = "Performance";

  ngOnInit(): void {
  }
  protected addTypeColors(data: Post[]) : PostWithTypeColor[]{
    var dataWithColors: PostWithTypeColor[] = [];
    data.forEach((post : Post) => {
      var typeColor : string;
      switch (post.type) {
        case "artikel":
          typeColor = DashColors.ARTICLE;
          break;
        case "blog":
          typeColor = DashColors.BLOG;
          break;
        case "news":
          typeColor = DashColors.NEWS;
          break;
        case "whitepaper":
          typeColor = DashColors.WHITEPAPER;
          break;
        case "podcast":
          typeColor = DashColors.GREY;
          break;
        default:
          typeColor = DashColors.GREY;
      }
      dataWithColors.push(new PostWithTypeColor(post, typeColor));
    })
    return dataWithColors;
  }

  protected readonly Util = Util;
  protected readonly String = String;
}
@Component({
  selector: 'dash-top5-article',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5ArticleComponent extends Top5PostsComponent{
  override postType = "Artikel";
  override sorter = "Performance";
  override ngOnInit() {
    this.db.getTopPostsBySorterWithType("performance", "artikel", 5).then(res => {
      this.dataArray = this.addTypeColors(res);
    });
  }
}

@Component({
  selector: 'dash-top5-blog',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5BlogComponent extends Top5PostsComponent{
  override postType = "Blogs";
  override sorter = "Performance";
  override ngOnInit() {
    this.db.getTopPostsBySorterWithType("performance", "blog", 5).then(res => {
      this.dataArray = this.addTypeColors(res);
    });
  }
}

@Component({
  selector: 'dash-top5-news',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5NewsComponent extends Top5PostsComponent{
  override postType = "News";
  override sorter = "Performance";
  override ngOnInit() {
    this.db.getTopPostsBySorterWithType("performance", "news", 5).then(res => {
      this.dataArray = this.addTypeColors(res);
    });
  }
}

@Component({
  selector: 'dash-top5-whitepaper',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5WhitepaperComponent extends Top5PostsComponent{
  override postType = "Whitepaper";
  override sorter = "Performance";
  override ngOnInit() {
    this.db.getTopPostsBySorterWithType("performance", "whitepaper", 5).then(res => {
      this.dataArray = this.addTypeColors(res);
    });
  }
}
