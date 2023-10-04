import {AfterViewInit, Component, OnInit} from '@angular/core';
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
    this.getData();
    this.setToolTip("Hier werden die Top 5 Posts der jeweiligen Kategorie und nach der ausgewählten Statistik gewertet angezeigt.");
  }

  protected getData(event?: Event){
    if (event !== undefined) {
      this.sorter = (event?.target as HTMLInputElement).value;
    }
    this.db.getTopPostsBySorterWithType(this.sorter.toLowerCase(), this.postType.toLowerCase(), 5).then(res => {
      this.dataArray = this.addTypeColors(res);
      this.cdr.detectChanges();
    });
    if (this.sorter.indexOf("p") == 0) this.sorter = this.sorter.replace("p", "P");
    if (this.sorter.indexOf("r") == 0) this.sorter = this.sorter.replace("r", "R").slice(0, 7) + "z";
    if (this.sorter.indexOf("c") == 0) this.sorter = this.sorter.replace("c", "C");
  }
  protected getTypeString(){
    if (this.postType.toLowerCase() == "blog"){
      return "Blogs";
    } else {
      return this.postType;
    }
  }

  protected getTitleSliced(title : string){
    if (title.length > 50){
      return title.slice(0, Math.min(title.lastIndexOf(" "), 50));
    }
    return title;
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
  protected readonly event = event;
}
@Component({
  selector: 'dash-top5-article',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5ArticleComponent extends Top5PostsComponent{
  override postType = "Artikel";
  override sorter = "Performance";

}

@Component({
  selector: 'dash-top5-blog',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5BlogComponent extends Top5PostsComponent{
  override postType = "Blog";
  override sorter = "Performance";

}

@Component({
  selector: 'dash-top5-news',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5NewsComponent extends Top5PostsComponent{
  override postType = "News";
  override sorter = "Performance";

}

@Component({
  selector: 'dash-top5-whitepaper',
  templateUrl: './top5-posts.component.html',
  styleUrls: ['./top5-posts.component.css', "../../dash-base/dash-base.component.css"]
})
export class Top5WhitepaperComponent extends Top5PostsComponent{
  override postType = "Whitepaper";
  override sorter = "Performance";

}
