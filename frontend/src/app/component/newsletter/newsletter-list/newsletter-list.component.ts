import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject} from "rxjs";
import {NewsletterListItemComponent} from "./newsletter-list-item/newsletter-list-item.component";
import {Newsletter} from "../Newsletter";

@Component({
  selector: 'dash-newsletter-list',
  templateUrl: './newsletter-list.component.html',
  styleUrls: ['./newsletter-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class NewsletterListComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  title : string = "Alle Newsletter";
  ngOnInit(): void {
    this.setToolTip("Hier sehen sie alle bisherigen Newsletter");
    this.selectorItems = [];
    this.db.getNewsletters(0, 20).then((res : Newsletter[]) => {
      for (var newsletter of res) {
        this.selectorItems.push(new SelectorItem(NewsletterListItemComponent, newsletter));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });
  }

}
