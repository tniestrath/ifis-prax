import {Component, OnInit, Pipe, PipeTransform, ViewChild} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {ForumPost} from "../forum-moderation-list/ForumPost";
import {DashColors} from "../../../util/Util";
import {DomSanitizer} from "@angular/platform-browser";

@Pipe({ name: 'unsafeHtml'})
export class SafeHtmlPipe implements PipeTransform {
  constructor(private sanitized: DomSanitizer) {
  }

  transform(value: string) {
    return this.sanitized.bypassSecurityTrustHtml(value);
  }
}

@Component({
  selector: 'dash-forum-moderation-display',
  templateUrl: './forum-moderation-display.component.html',
  styleUrls: ['./forum-moderation-display.component.css', "/../../dash-base/dash-base.component.css"]
})
export class ForumModerationDisplayComponent extends DashBaseComponent implements OnInit{
  data : ForumPost = new ForumPost();
  isEdited : boolean = false;

  postLinks : string[] = [];
  ngOnInit(): void {
    var style = "width: 15%; display: inline-flex; align-items: center; justify-content: center; border-radius: 5px; background-color: ";
    this.setToolTip(
 "<span>Hier können Beiträge aus dem Forum Moderiert werden</span>" +
      "<br><br>" +
      "<div style=\"" + style + DashColors.RED  + "\"><span>Löschen</span></div><span> (STRG + ↓ / ENTF) : Der Beitrag wird aus der Liste entfernt und nicht im Forum angezeigt</span><br><br>" +
      "<div style=\"" + style + DashColors.GREEN  + "\"><span>Freigeben</span></div><span> (STRG + ↑ / EINFG) : Der Beitrag wird aus der Liste entfernt und im Forum angezeigt</span><br><br>" +
      "<div style=\""+ style + DashColors.DARK_GREY + "\"><span>→</span></div><span> (STRG + → / ENDE) : Der Beitrag wird zurück in die Liste geschoben </span>"
      , 3)
  }

  onDeleteClick = () : void => {};
  onAcceptClick = () : void => {};
  onStackClick = () : void => {};

  onEdited($event : any, field : string) {
    if($event.target.checked){
      this.isEdited = true;
      $event.target.disabled = true;
      switch (field){
        case "name":
          // @ts-ignore
          document.getElementById("editorName").contentEditable = "true";
          break;
        case "email":
          // @ts-ignore
          document.getElementById("editorEmail").contentEditable = "true";
          break;
        case "body":
          // @ts-ignore
          document.getElementById("editorField").contentEditable = "true";
          break;
      }
    }
  }

  public isEditedWhere(){
    let ans : string[] = [];
    if ((document.getElementById("edit-body") as HTMLInputElement).checked){
      ans.push("body");
    }
    if ((document.getElementById("edit-email") as HTMLInputElement).checked){
      ans.push("email");
    }
    if ((document.getElementById("edit-name") as HTMLInputElement).checked){
      ans.push("name");
    }
    return ans;
  }

  protected readonly DashColors = DashColors;

  getRatingColor(preRating: string, category: string) {
    if (preRating != "good" && preRating.includes(category)){
        return DashColors.RED;
    } else return "#808080";
  }

  public getEditorField(){
    return document.getElementById("editorField");
  }

  public getEmailField(){
    return document.getElementById("editorEmail");
  }

  public getNameField(){
    return document.getElementById("editorName");
  }
  public setPostLinks(value: string[]) {
    this.postLinks = value;
  }
  public resetEditButton(){
    (document.getElementById("edit-body") as HTMLInputElement).disabled = false;
    (document.getElementById("edit-body") as HTMLInputElement).checked = false;
    (document.getElementById("edit-email") as HTMLInputElement).disabled = false;
    (document.getElementById("edit-email") as HTMLInputElement).checked = false;
    (document.getElementById("edit-name") as HTMLInputElement).disabled = false;
    (document.getElementById("edit-name") as HTMLInputElement).checked = false;
    this.isEdited = false;
    // @ts-ignore
    document.getElementById("editorField").contentEditable = "false";
    // @ts-ignore
    document.getElementById("editorEmail").contentEditable = "false";
    // @ts-ignore
    document.getElementById("editorName").contentEditable = "false";
  }
}
