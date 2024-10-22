import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-forum-add-moderator',
  templateUrl: './forum-add-moderator.component.html',
  styleUrls: ['./forum-add-moderator.component.css', "../../dash-base/dash-base.component.css"]
})
export class ForumAddModeratorComponent extends DashBaseComponent implements OnInit{

  input_username: HTMLElement | undefined | null ;
  input_forum: HTMLElement | undefined | null ;
  input_rights_checkbox: HTMLElement | undefined | null;

  modSuggestions : string[] = [];
  lastModInput : string = "";

  forumSuggestions : string[] = [];
  lastForumInput : string = "";

  modName = "";
  forumName = "";
  selectedModIndex = 0;
  selectedForumIndex = 0;

  ngOnInit(): void {
    this.input_username = document.getElementById("moderator-name-input");
    this.input_forum = document.getElementById("moderator-forum-input");
    this.input_rights_checkbox = document.getElementById("moderator-copy-rights-input");

    this.modName = "";
    this.forumName = "";
  }

  onSubmit() {
    let use_own_rights = (this.input_rights_checkbox as HTMLInputElement).checked;

    if (use_own_rights) {
      this.forumName = "-1"
    }
    this.api.addForumModerator(this.modName, this.forumName).then(value => {
      console.log("addModerator: " + value);
    });
  }

  onRightsCheckboxChange($event: Event) {
    (this.input_forum as HTMLInputElement).disabled = ($event.target as HTMLInputElement).checked;
    (this.input_forum as HTMLInputElement).required = !($event.target as HTMLInputElement).checked;
  }

  onModKey(value: any){
    if (value.key == "Enter"){
      if (this.selectedModIndex == 0){
        this.modName = (this.input_username as HTMLInputElement).value;
      }
      else {
        this.modName = this.modSuggestions[this.selectedModIndex+1];
        (this.input_username as HTMLInputElement).value = this.modSuggestions[this.selectedModIndex+1];
        this.selectedModIndex = 0;
      }
      this.modSuggestions = [];
      this.cdr.detectChanges();
    }
    if (value.key == "Delete" || value.key == "Backspace"){
      value.preventDefault();
      // @ts-ignore
      this.modName = (this.input_username as HTMLInputElement).value = (this.input_username as HTMLInputElement).value.slice(0, -1);
      this.cdr.detectChanges();
    }
    if (value.key == "ArrowUp" && this.selectedModIndex > 0){
      value.preventDefault();
      this.selectedModIndex--;
    } else if (value.key == "ArrowDown" && this.selectedModIndex < (this.modSuggestions.length -2)){
      value.preventDefault();
      this.selectedModIndex++;
    }
  }

  onForumKey(value: any){
    if (value.key == "Enter"){
      if (this.selectedForumIndex == 0){
        this.forumName = (this.input_forum as HTMLInputElement).value;
      }
      else {
        this.forumName = this.forumSuggestions[this.selectedForumIndex+1];
        (this.input_forum as HTMLInputElement).value = this.forumSuggestions[this.selectedForumIndex+1];
        this.selectedForumIndex = 0;
      }
      this.forumSuggestions = [];
      this.cdr.detectChanges();
      value.preventDefault();
    }
    if (value.key == "Delete" || value.key == "Backspace"){
      value.preventDefault();
      // @ts-ignore
      this.forumName = (this.input_forum as HTMLInputElement).value = (this.input_forum as HTMLInputElement).value.slice(0, -1);
      this.cdr.detectChanges();
    }
    if (value.key == "ArrowUp" && this.selectedForumIndex > 0){
      value.preventDefault();
      this.selectedForumIndex--;
    } else if (value.key == "ArrowDown" && this.selectedForumIndex < (this.forumSuggestions.length -2)){
      value.preventDefault();
      this.selectedForumIndex++;
    }
  }

  getModeratorSuggestions(value: string) {
    if (value == this.lastModInput){
      let copy = structuredClone(this.modSuggestions);
      copy.shift();
      return copy;
    }
    if (value == ""){this.modSuggestions = []; return [];}
    this.api.getModeratorSuggestions(value).then(res => {
      res.unshift("");
      this.modSuggestions =  res;
      this.cdr.detectChanges();
    });
    this.lastModInput = value;
    let copy = structuredClone(this.modSuggestions);
    copy.shift();
    return copy;
  }

  getForumSuggestions(value: string) {
    if (value == this.lastModInput){
      let copy = structuredClone(this.forumSuggestions);
      copy.shift();
      return copy;
    }
    if (value == ""){this.forumSuggestions = []; return [];}
    this.api.getForumSuggestions(value).then(res => {
      res.unshift("");
      this.forumSuggestions =  res;
      this.cdr.detectChanges();
    });
    this.lastModInput = value;
    let copy = structuredClone(this.forumSuggestions);
    copy.shift();
    return copy;
  }

  onModeratorSuggestionClick(mod : string){
    this.modName = mod;
    (this.input_username as HTMLInputElement).value = this.modSuggestions[this.selectedModIndex+1];
    this.modSuggestions = [];
  }

  onForumSuggestionClick(forum : string){
    this.forumName = forum;
    (this.input_forum as HTMLInputElement).value = this.forumSuggestions[this.selectedForumIndex+1];
    this.forumSuggestions = [];
  }

  onMouseOverMod(i: number) {
    this.selectedModIndex = i;
  }

  onMouseOverForum(i: number) {
    this.selectedForumIndex = i;
  }

}
