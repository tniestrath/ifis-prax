import {Component, Directive, OnDestroy, OnInit, ViewChild, ViewContainerRef} from "@angular/core";
import {Subject} from "rxjs";
import {ApiService} from "../services/api.service";
import Util from "./Util";

@Directive({
  selector: '[dialogDirective]'
})
export class DialogDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
@Directive({
  selector: '[filterOptionsDirective]'
})
export class FilterOptionsDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
@Component({
  selector: "dialog",
  template: ""
})
export class Dialog{
  protected answer : Subject<boolean> = new Subject<boolean>();

  constructor(protected api : ApiService) {
  }
  public awaitAnswer(){
    return this.answer;
  }
}

@Component({
  selector: "dialog-sure",
  template: "<div class='dialog-sure-content'><p>Sind sie sich sicher?</p><input type='button' value='JA' name='sure' (click)='answer.next(true)'><input type='button' value='NEIN' name='notsure' (click)='answer.next(false)'></div>",
  styles: [".dialog-sure-content{position: fixed; top: calc(50% - 100px); left: calc(50% - 250px); width: 500px; height: 200px; display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; background-color: #FFFFFF; border: 1px solid black; border-radius: 5px; z-index: 99999999}",
    ".dialog-sure-content p{grid-row: 1; grid-column: 1/2;}.dialog-sure-content input{grid-row: 2;}"]
})
export class AreYouSureDialog extends Dialog implements OnDestroy, OnInit{

  private evListener = (ev: KeyboardEvent) => {
    if(ev.key == "Enter") {
      this.answer.next(true);
    } else if (ev.key == "Escape"){
      this.answer.next(false);
    }
  }
  ngOnInit() {
    document.addEventListener("keydown", this.evListener);
  }

  ngOnDestroy(): void {
    document.removeEventListener("keydown", this.evListener);
  }
}
@Component({
  selector: "dialog-filter",
  template: "<form class='dialog-content' action=''>" +
    "<p>Filter anlegen</p>" +
    "<label for='selectForum'>Forum:</label><select name='selectForum' id='selectForum'></select>" +
    "<input type='submit' value='Absenden' name='ok'><input type='button' value='Abbrechen' name='notOk' (click)='answer.next(false)'>" +
    "</form>",
  styles: [".dialog-content{position: fixed; top: calc(50% - 100px); left: calc(50% - 250px); width: 500px; height: 200px; display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 100px 1fr 1fr; background-color: #FFFFFF; border: 1px solid black; border-radius: 5px; z-index: 99999999}",
    ".dialog-content p{grid-row: 1; grid-column: 1/3;}.dialog-content select{grid-row: 2;grid-column: 2}.dialog-content input{grid-row: 3;}"]
})
export class FilterDialog extends Dialog implements OnDestroy, OnInit{
  @ViewChild(FilterOptionsDirective, {static : true}) filterOptions!: FilterOptionsDirective;

  private evListener = (ev: KeyboardEvent) => {
    if(ev.key == "Enter") {
      this.answer.next(true);
    } else if (ev.key == "Escape"){
      this.answer.next(false);
    }
  }
  ngOnInit(): void {
    const selectForum = document.getElementById("selectForum");
    const forums = ["datenschutz", "smartenschutz", "tomatenschutz"];
    if (selectForum){
      for (let forum of forums) {
        selectForum.innerHTML += "<option value='" + forum + "'>" + Util.firstToUpperCase(forum) + "</option>";
      }
    }

  }

  ngOnDestroy(): void {
    document.removeEventListener("keydown", this.evListener);
  }


}
