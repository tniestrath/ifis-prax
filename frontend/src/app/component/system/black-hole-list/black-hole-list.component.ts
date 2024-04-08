import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SelectorItem} from "../../../page/selector/selector.component";
import {Subject, Subscription} from "rxjs";
import {BadBotItemComponent} from "./bad-bot-item/bad-bot-item.component";

@Component({
  selector: 'dash-black-hole-list',
  templateUrl: './black-hole-list.component.html',
  styleUrls: ['./black-hole-list.component.css', "../../dash-base/dash-base.component.css"]
})
export class BlackHoleListComponent extends DashBaseComponent implements OnInit{
  selectorItems : SelectorItem[] = [];
  selectorItemsBackup = this.selectorItems;
  selectorItemsLoaded = new Subject<SelectorItem[]>();

  title : string = "Geblockte Bots";

  ngOnInit() {
    this.selectorItems = [];
    this.db.getBlackHoleData().then(res => {
      for (var badBot of res) {
        this.selectorItems.push(new SelectorItem(BadBotItemComponent, badBot));
      }
      this.selectorItemsLoaded.next(this.selectorItems);
    });

  }
}
