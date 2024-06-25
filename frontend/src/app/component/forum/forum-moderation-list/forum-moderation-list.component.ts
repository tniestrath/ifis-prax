import { Component } from '@angular/core';
import {DashListComponent} from "../../dash-list/dash-list.component";
import {ForumModerationListItemComponent} from "./forum-moderation-list-item/forum-moderation-list-item.component";
import {ForumPost} from "./ForumPost";
import {SysVars} from "../../../services/sys-vars-service";
import {SelectorItem} from "../../../page/selector/selector.component";


@Component({
  selector: 'dash-forum-moderation-list',
  templateUrl: './forum-moderation-list.component.html',
  styleUrls: ['./forum-moderation-list.component.css', "/../../dash-base/dash-base.component.css"]
})
export class ForumModerationListComponent extends DashListComponent<ForumPost, ForumModerationListItemComponent>{

  public g_cdr = this.cdr;
  override ngOnInit() {
    this.setToolTip("Liste mit unmoderierten Beiträgen", 2);

    let moderatedCheckbox = document.getElementById("moderated-checkbox");
    let titleText = document.getElementById("forum-post-list-box-title-text");

    // @ts-ignore
    (moderatedCheckbox as HTMLInputElement).onchange = () => {
      if ((moderatedCheckbox as HTMLInputElement).checked){
        // @ts-ignore
        titleText.innerText = "Moderierte Beiträge";
      } else {
        // @ts-ignore
        titleText.innerText = "Unmoderierte Beiträge";
      }
      this.onModeratedCheckboxChange((moderatedCheckbox as HTMLInputElement).checked);
    }
  }
  override onScrollEnd() {
  }

  onModeratedCheckboxChange = (checked : boolean) : void => {};


  public isModeratedChecked(){
    return (document.getElementById("moderated-checkbox") as HTMLInputElement).checked;
  }
}
