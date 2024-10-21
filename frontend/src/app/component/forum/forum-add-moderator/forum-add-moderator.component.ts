import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";

@Component({
  selector: 'dash-forum-add-moderator',
  templateUrl: './forum-add-moderator.component.html',
  styleUrls: ['./forum-add-moderator.component.css', "../../dash-base/dash-base.component.css"]
})
export class ForumAddModeratorComponent extends DashBaseComponent implements OnInit{

  input_username: HTMLElement | undefined | null ;
  input_forum: HTMLElement | undefined | null ;
  input_rights_checkbox: HTMLElement | undefined | null;

  ngOnInit(): void {
    this.input_username = document.getElementById("moderator-name-input");
    this.input_forum = document.getElementById("moderator-forum-input");
    this.input_rights_checkbox = document.getElementById("moderator-copy-rights-input");
  }

  onSubmit() {
    let modId = "";
    let forumId = "";
    let use_own_rights = (this.input_rights_checkbox as HTMLInputElement).checked;
    /*this.api.addForumModerator(modId, forumId).then(value => {

    });*/
  }

  onRightsCheckboxChange($event: Event) {
    (this.input_forum as HTMLInputElement).disabled = ($event.target as HTMLInputElement).checked;
  }
}
