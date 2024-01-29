import {AfterViewInit, Component} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {PostListItemComponent} from "./post-list-item/post-list-item.component";
import {Post} from "../Post";
import {Top5PostsComponent} from "../top5-posts/top5-posts.component";
import _default from "chart.js/dist/core/core.interaction";
import index = _default.modes.index;
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostListComponent extends DashBaseComponent{
  placeholder : string = "Post suchen";
  input_search_cb : any;
  input_all_cb : any;
  input_article_cb : any;
  input_blog_cb : any;
  input_news_cb : any;
  input_podcast_cb : any;
  input_whitepaper_cb : any;

  lastScroll = 0;
  pageIndex = 0;
  pageSize = 20;
  pagesComplete = false;
  search_text: string = "";
  filter: string = " ";

  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {
    this.setToolTip("Auflistung aller Posts, sie können nach den Beitrags-Typen filtern oder nach Schlagwörtern in Titel oder Tags suchen");
    this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
      for (const valueElement of value.posts) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.pageIndex = 0;
      this.search_text = event.target.value;
      this.selectorItems = [];

      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    };
    this.input_all_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = " ";
      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    };
    this.input_article_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "artikel";
      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    };
    this.input_blog_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "blog";
      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    }
    this.input_news_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "news";
      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    }
    this.input_podcast_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "podcast";
      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "whitepaper";
      this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts: Post[], count : number}) => {
        for (const valueElement of value.posts) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex++;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    }
  }

  onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text).then((value : {posts:  Post[], count : number}) => {
          for (const valueElement of value.posts) {
            this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
          }
          if (value.count <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }


}

@Component({
  selector: 'dash-list-podcast',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css", "./podcast-list.component.css"]
})
export class PodcastListComponent extends PostListComponent{
  override placeholder = "Podcast suchen";

  override ngOnInit() {
    this.setToolTip("Auflistung aller Podcasts, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");
    this.db.getPodcastsAll().then( (res: Post[])  => {
      for (const valueElement of res) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        this.selectorItems.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_all_cb = () => {
      this.selectorItems = this.selectorItemsBackup.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }
  override onScrollEnd() {
  }

}

@Component({
  selector: 'dash-list-ratgeber',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css", "./ratgeber-list.component.css"]
})
export class RatgeberListComponent extends PostListComponent{
  override placeholder = "Ratgeber suchen";

  override ngOnInit() {
    this.setToolTip("Auflistung aller Ratgeber-Inhalte, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");
    this.db.getRatgeberAll().then((res: Post[]) => {
      for (const valueElement of res) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        this.selectorItems.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_all_cb = () => {
      this.selectorItems = this.selectorItemsBackup.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }

  override onScrollEnd() {
  }

}

@Component({
  selector: 'dash-list-user-post',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserPostListComponent extends PostListComponent{

  override ngOnInit() {
    this.setToolTip("Auflistung aller Inhalte dieses Nutzers, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");

    SysVars.SELECTED_POST_IDS.subscribe(list => {
      this.db.getPostsByIDs(list).then((res : Post[]) => {
        this.selectorItems = [];
        for (const valueElement of res) {
          this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        }
        this.pageIndex = 0;
        this.pagesComplete = true;
        this.selectorItemsLoaded.next(this.selectorItems);
      });
    });

    this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, " ", "").then((value : {posts:  Post[], count : number}) => {
      this.selectorItems = [];
      this.pagesComplete = false;
      for (const valueElement of value.posts) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        this.selectorItems.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.search_text = event.target.value;
      this.loadItems();
    }
    this.input_all_cb = () => {
      this.filter = " ";
      this.loadItems();
    }
    this.input_whitepaper_cb = () => {
      this.filter = "whitepaper";
      this.loadItems();
    }
    this.input_news_cb = () => {
      this.filter = "news";
      this.loadItems()
    }
    this.input_blog_cb = () => {
      this.filter = "blog";
      this.loadItems();
    }
    this.input_article_cb = () => {
      this.filter = "artikel";
      this.loadItems();
    }
    this.input_podcast_cb = () => {
      this.filter = "podcast";
      this.loadItems();
    }
  }

  loadItems(){
    this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text).then((value : {posts:  Post[], count : number}) => {
      this.selectorItems = [];
      this.pagesComplete = false;
      for (const valueElement of value.posts) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getUserPostsPaged(SysVars.USER_ID, this.pageIndex, this.pageSize, this.filter, this.search_text).then((value : {posts:  Post[], count : number}) => {
          for (const valueElement of value.posts) {
            this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
          }
          if (value.count <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }

}

@Component({
  selector: 'dash-list-user-post',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class EventListComponent extends PostListComponent{
  override placeholder = "Event suchen";

  override ngOnInit() {
    this.setToolTip("Auflistung aller Events, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");
    this.db.getEventsLikePostsPaged( 0, 20, " ", "").then((value : {posts:  Post[], count : number}) => {
      this.selectorItems = [];
      for (const valueElement of value.posts) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        this.selectorItems.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_all_cb = () => {
      this.selectorItems = this.selectorItemsBackup.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getEventsLikePostsPaged(this.pageIndex, this.pageSize, this.filter, this.search_text).then((value : {posts:  Post[], count : number}) => {
          for (const valueElement of value.posts) {
            this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
          }
          if (value.count <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }

}

@Component({
  selector: 'dash-list-user-post',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserEventListComponent extends PostListComponent{
  override placeholder = "Event suchen";

  override ngOnInit() {
    this.setToolTip("Auflistung aller Events, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");
    this.db.getUserEventsLikePostsPaged(SysVars.USER_ID, 0, 20, " ", "").then((value : {posts:  Post[], count : number}) => {
      this.selectorItems = [];
      for (const valueElement of value.posts) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
        this.selectorItems.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      }
      this.pageIndex++;
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_all_cb = () => {
      this.selectorItems = this.selectorItemsBackup.sort((a, b) => Number(b.data.id) - Number(a.data.id));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }

  override onScrollEnd() {
    if (!this.pagesComplete){
      let scroll = Date.now();
      if (scroll >= (this.lastScroll + 100)){
        console.log(this.pageIndex)
        this.db.getUserEventsLikePostsPaged(SysVars.USER_ID, this.pageIndex, this.pageSize, this.filter, this.search_text).then((value : {posts:  Post[], count : number}) => {
          for (const valueElement of value.posts) {
            this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
          }
          if (value.count <= 0){
            this.pagesComplete = true;
          }
          this.selectorItemsLoaded.next(this.selectorItems);
        });
        this.pageIndex++;
      }
      else {}
      this.lastScroll = scroll;
    }
  }

}
