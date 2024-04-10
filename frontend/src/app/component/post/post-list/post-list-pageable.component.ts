import {AfterViewInit, Component} from '@angular/core';
import {PostListItemComponent} from "./post-list-item/post-list-item.component";
import {Post} from "../Post";
import {SysVars} from "../../../services/sys-vars-service";
import {DashListComponent, DashListPageableComponent} from "../../dash-list/dash-list.component";

/**
 * `PostListComponent` - basic List to display Posts and "Post-like" data
 *
 */
@Component({
  selector: 'dash-post-list',
  templateUrl: './post-list-pageable.component.html',
  styleUrls: ['./post-list-pageable.component.css', "../../dash-base/dash-base.component.css"]
})
export class PostListPageableComponent extends DashListPageableComponent<Post, PostListItemComponent>{
  /**
   * `placeholder` component title text
   */
  placeholder : string = "Post suchen";
  /**
   * `input_search_cb` callback function to get input data
   */
  input_search_cb : any;
  input_all_cb : any;
  input_filter_1_cb : any;
  input_filter_2_cb : any;
  input_filter_3_cb : any;
  input_filter_4_cb : any;
  input_filter_5_cb : any;

  search_text: string = "";
  active_filter: string = " ";

  override ngOnInit(): void {
    this.setToolTip("Auflistung aller Posts, sie können nach den Beitrags-Typen filtern oder nach Schlagwörtern in Titel oder Tags suchen");
    this.load(this.db.getPostsAllPaged(this.pageIndex, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.pageIndex = 0;
      this.search_text = event.target.value;
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    };
    this.input_all_cb = () => {
      this.pageIndex = 0;
      this.active_filter = " ";
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    };
    this.input_filter_1_cb = () => {
      this.pageIndex = 0;
      this.active_filter = "artikel";
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    };
    this.input_filter_2_cb = () => {
      this.pageIndex = 0;
      this.active_filter = "blog";
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_3_cb = () => {
      this.pageIndex = 0;
      this.active_filter = "news";
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_4_cb = () => {
      this.pageIndex = 0;
      this.active_filter = "podcast";
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_5_cb = () => {
      this.pageIndex = 0;
      this.active_filter = "whitepaper";
      this.reload(this.db.getPostsAllPaged(0, this.pageSize, "date", this.active_filter, this.search_text), PostListItemComponent);
    }
  }
}
export class PostListComponent extends DashListComponent<Post, PostListItemComponent>{
  /**
   * `placeholder` component title text
   */
  placeholder : string = "Post suchen";
  /**
   * `input_search_cb` callback function to get input data
   */
  input_search_cb : any;
  input_all_cb : any;
  input_filter_1_cb : any;
  input_filter_2_cb : any;
  input_filter_3_cb : any;
  input_filter_4_cb : any;
  input_filter_5_cb : any;

  search_text: string = "";
  active_filter: string = " ";
}


@Component({
  selector: 'dash-list-podcast',
  templateUrl: './post-list-pageable.component.html',
  styleUrls: ['./post-list-pageable.component.css', "../../dash-base/dash-base.component.css", "./podcast-list.component.css"]
})
export class PodcastListComponent extends PostListPageableComponent{
  override placeholder = "Podcast suchen";

  override ngOnInit() {
    this.setToolTip("Auflistung aller Podcasts, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");
    this.load(this.db.getPodcastsAll(0,this.pageSize), PostListItemComponent);

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
    this.input_filter_5_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }
  override onScrollEnd() {
  }
}

@Component({
  selector: 'dash-list-ratgeber',
  templateUrl: './post-list-pageable.component.html',
  styleUrls: ['./post-list-pageable.component.css', "../../dash-base/dash-base.component.css", "./ratgeber-list.component.css"]
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
    this.input_filter_5_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }
  override onScrollEnd() {
  }

}

@Component({
  selector: 'dash-list-user-post',
  templateUrl: './post-list-pageable.component.html',
  styleUrls: ['./post-list-pageable.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserPostListComponent extends PostListPageableComponent{

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
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_all_cb = () => {
      this.active_filter = " ";
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_5_cb = () => {
      this.active_filter = "whitepaper";
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_3_cb = () => {
      this.active_filter = "news";
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_2_cb = () => {
      this.active_filter = "blog";
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_1_cb = () => {
      this.active_filter = "artikel";
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_4_cb = () => {
      this.active_filter = "podcast";
      this.reload(this.db.getUserPostsPaged(SysVars.USER_ID, 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
  }

}

@Component({
  selector: 'dash-list-user-post',
  templateUrl: './post-list-pageable.component.html',
  styleUrls: ['./post-list-pageable.component.css', "../../dash-base/dash-base.component.css", "./event-list.component.css"]
})
export class EventListComponent extends PostListPageableComponent{
  override placeholder = "Event suchen";

  override ngOnInit() {
    this.setToolTip("Auflistung aller Events, sie können nach Datum oder Clicks sortieren oder nach Schlagwörtern in Titel oder Tags suchen");
    this.load(this.db.getEventsLikePostsPaged( 0, this.pageSize, " ", ""), PostListItemComponent);

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.search_text = event.target.value;
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_all_cb = () => {
      this.active_filter = " ";
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_1_cb = () => {
      this.active_filter = "KG";
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_2_cb = () => {
      this.active_filter = "ME";
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_3_cb = () => {
      this.active_filter = "SE";
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_4_cb = () => {
      this.active_filter = "WS";
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
    this.input_filter_5_cb = () => {
      this.active_filter = "SO";
      this.reload(this.db.getEventsLikePostsPaged( 0, this.pageSize, this.active_filter, this.search_text), PostListItemComponent);
    }
  }
}

@Component({
  selector: 'dash-list-user-post',
  templateUrl: './post-list-pageable.component.html',
  styleUrls: ['./post-list-pageable.component.css', "../../dash-base/dash-base.component.css"]
})
export class UserEventListComponent extends PostListPageableComponent{
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
    this.input_filter_5_cb = () => {
      this.selectorItems.sort((a, b) => Number((b.data as Post).clicks) - Number((a.data as Post).clicks));
      this.selectorItemsLoaded.next(this.selectorItems);
    }
  }
}
