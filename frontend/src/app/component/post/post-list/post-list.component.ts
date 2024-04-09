import {AfterViewInit, Component} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {PostListItemComponent} from "./post-list-item/post-list-item.component";
import {Post} from "../Post";
import {SysVars} from "../../../services/sys-vars-service";
import {DashListComponent, DashListPageableComponent} from "../../dash-list/dash-list.component";

@Component({
  selector: 'dash-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostListComponent extends DashListPageableComponent<Post, PostListItemComponent>{
  placeholder : string = "Post suchen";
  input_search_cb : any;
  input_all_cb : any;
  input_article_cb : any;
  input_blog_cb : any;
  input_news_cb : any;
  input_podcast_cb : any;
  input_whitepaper_cb : any;

  search_text: string = "";
  filter: string = " ";

  override ngOnInit(): void {
    this.setToolTip("Auflistung aller Posts, sie können nach den Beitrags-Typen filtern oder nach Schlagwörtern in Titel oder Tags suchen");
    this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.pageIndex = 0;
      this.search_text = event.target.value;
      this.selectorItems = [];
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
    };
    this.input_all_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = " ";
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
    };
    this.input_article_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "artikel";
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
    };
    this.input_blog_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "blog";
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
    }
    this.input_news_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "news";
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
    }
    this.input_podcast_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "podcast";
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems = [];
      this.pageIndex = 0;
      this.filter = "whitepaper";
      this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.filter, this.search_text), PostListItemComponent);
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
    this.load(this.db.getRatgeberAll(), PostListItemComponent);

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
    this.selectorItems = [];
    this.pagesComplete = false;

    SysVars.SELECTED_POST_IDS.subscribe(list => {
      this.load(this.db.getPostsByIDs(list), PostListItemComponent);
    });
    this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, " ", ""), PostListItemComponent);

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.search_text = event.target.value;
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
    }
    this.input_all_cb = () => {
      this.filter = " ";
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
    }
    this.input_whitepaper_cb = () => {
      this.filter = "whitepaper";
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
    }
    this.input_news_cb = () => {
      this.filter = "news";
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
    }
    this.input_blog_cb = () => {
      this.filter = "blog";
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
    }
    this.input_article_cb = () => {
      this.filter = "artikel";
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
    }
    this.input_podcast_cb = () => {
      this.filter = "podcast";
      this.load(this.db.getUserPostsPaged(SysVars.USER_ID, 0, 20, this.filter, this.search_text), PostListItemComponent);
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
    this.load(this.db.getEventsLikePostsPaged( 0, 20, " ", ""), PostListItemComponent);

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
    this.load(this.db.getUserEventsLikePostsPaged(SysVars.USER_ID, 0, 20, " ", ""), PostListItemComponent)

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
}
