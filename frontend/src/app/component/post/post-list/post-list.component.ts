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
  placeholder : string = "Post suchen";
  input_search_cb : any;
  input_all_cb : any;
  input_article_cb : any;
  input_blog_cb : any;
  input_news_cb : any;
  input_whitepaper_cb : any;


  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  ngOnInit(): void {
    this.setToolTip("Auflistung aller Posts, sie können nach den Beitrags-Typen filtern oder nach Schlagwörtern in Titel oder Tags suchen");
    this.db.getPostsAll().then( (value : Post[]) => {
      for (const valueElement of value) {
        this.selectorItems.push(new SelectorItem(PostListItemComponent, valueElement));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });

    this.input_search_cb = (event: { target: { value: string; }; }) => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return (item.data as Post).title.toUpperCase().includes(event.target.value.toUpperCase());
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    };
    this.input_all_cb = () => {
      this.selectorItems = this.selectorItemsBackup;
      this.selectorItemsLoaded.next(this.selectorItems);
    };
    this.input_article_cb = () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "artikel" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_blog_cb = () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "blog" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_news_cb = () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "news" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
    }
    this.input_whitepaper_cb = () => {
      this.selectorItems = this.selectorItemsBackup.filter((item) => {
        return ( (item.data as Post).type == "whitepaper" );
      });
      this.selectorItemsLoaded.next(this.selectorItems);
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

}
