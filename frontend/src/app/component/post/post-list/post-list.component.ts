import {AfterViewInit, Component} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {PostListItemComponent} from "./post-list-item/post-list-item.component";
import {Post} from "../Post";
import {Top5PostsComponent} from "../top5-posts/top5-posts.component";

@Component({
  selector: 'dash-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostListComponent extends DashBaseComponent{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();
  search_input : any;
  type_input_all : any;
  type_input_article : any;
  type_input_blog : any;
  type_input_news : any;
  type_input_whitepaper : any;

  ngOnInit(): void {
    this.setToolTip("Auflistung aller Posts, sie können nach den Beitrags-Typen filtern oder nach Schlagwörtern in Titel oder Tags suchen");
    //@ts-ignore
    this.db.getPostsAll().then( (value : Post[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.search_input = document.getElementById("post-search");
    this.search_input.addEventListener("input", (event : any) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.type_input_all = document.getElementById("post-type-all");
    this.type_input_all.addEventListener("change", () => {
      this.selectorItems = this.selectorItemsBackup;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.type_input_article = document.getElementById("post-type-article");
    this.type_input_article.addEventListener("change", () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "artikel" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.type_input_blog = document.getElementById("post-type-blog");
    this.type_input_blog.addEventListener("change", () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "blog" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.type_input_news = document.getElementById("post-type-news");
    this.type_input_news.addEventListener("change", () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "news" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.type_input_whitepaper = document.getElementById("post-type-whitepaper");
    this.type_input_whitepaper.addEventListener("change", () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "whitepaper" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
}

@Component({
  selector: 'dash-list-podcast',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css", "./podcast-list.component.css"]
})
export class PodcastListComponent extends PostListComponent{

  override ngOnInit() {
    // @ts-ignore
    (document.getElementById("post-search") as HTMLInputElement).placeholder = "Podcast suchen";

    this.db.getPodcastsAll().then( (res: Post[])  => {
      for (const valueElement of res) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        this.selectorItems.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });


    this.search_input = document.getElementById("post-search");
    this.search_input.addEventListener("input", (event : any) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.type_input_all = document.getElementById("post-type-all");
    this.type_input_all.addEventListener("change", () => {
      this.selectorItems = this.selectorItemsBackup.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.type_input_whitepaper = document.getElementById("post-type-whitepaper");
    this.type_input_whitepaper.addEventListener("change", () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }
}
