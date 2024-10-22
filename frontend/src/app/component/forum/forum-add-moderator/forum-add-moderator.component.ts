import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";

@Component({
  selector: 'dash-forum-add-moderator',
  templateUrl: './forum-add-moderator.component.html',
  styleUrls: ['./forum-add-moderator.component.css', "../../dash-base/dash-base.component.css"],
  changeDetection: ChangeDetectionStrategy.OnPush
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
  suggestMods = true;
  suggestForums = true;

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

  onModInput(value? : string) {
    if (value == null){this.modName = ""; return}
    this.modName = value;
  }
  onForumInput(value? : string) {
    if (value == null){this.forumName = ""; return}
    this.forumName = value;
  }


  onRightsCheckboxChange($event: Event) {
    (this.input_forum as HTMLInputElement).disabled = ($event.target as HTMLInputElement).checked;
    (this.input_forum as HTMLInputElement).required = !($event.target as HTMLInputElement).checked;
  }

  onModKey(value: any){
    if (value.key == "Delete" || value.key == "Backspace"){
      value.preventDefault();
      // @ts-ignore
      this.modName = (this.input_username as HTMLInputElement).value = (this.input_username as HTMLInputElement).value.slice(0, -1);
      this.suggestMods = true;
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
    if (value.key == "Delete" || value.key == "Backspace"){
      value.preventDefault();
      // @ts-ignore
      this.forumName = (this.input_forum as HTMLInputElement).value = (this.input_forum as HTMLInputElement).value.slice(0, -1);
      this.suggestForums = true;
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
    if (this.suggestMods){
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
    return [];
  }

  getForumSuggestions(value: string) {
    if (this.suggestForums){
      if (value == this.lastForumInput){
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
      this.lastForumInput = value;
      let copy = structuredClone(this.forumSuggestions);
      copy.shift();
      return copy;
    }
    return [];
  }

  onModeratorSuggestionClick(mod : string){
    this.suggestMods = !this.suggestMods
    this.modName = mod;
    (this.input_username as HTMLInputElement).value = mod
    this.modSuggestions = [];
  }

  onForumSuggestionClick(forum : string){
    this.suggestForums = !this.suggestForums;
    this.forumName = forum;
    (this.input_forum as HTMLInputElement).value = forum
    this.forumSuggestions = [];
  }

  onMouseOverMod(i: number) {
    this.selectedModIndex = i;
  }

  onMouseOverForum(i: number) {
    this.selectedForumIndex = i;
  }

}
