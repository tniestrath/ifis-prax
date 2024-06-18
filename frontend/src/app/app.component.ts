import {AfterViewInit, Component, Directive, OnDestroy, ViewChild, ViewContainerRef} from '@angular/core';
import {Subject} from "rxjs";
import {Chart} from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";
import ChartAnnotation from "chartjs-plugin-annotation";
import Util from "./util/Util";
import {SysVars} from "./services/sys-vars-service";

@Directive({
  selector: '[dialogDirective]'
})
export class DialogDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}

@Component({
  selector: "dialog-sure",
  template: "<div class='dialog-sure-content'><p>Sind sie sich sicher?</p><input type='button' value='JA' name='sure' (click)='answer.next(true)'><input type='button' value='NEIN' name='notsure' (click)='answer.next(false)'></div>",
  styles: [".dialog-sure-content{position: fixed; top: calc(50% - 100px); left: calc(50% - 250px); width: 500px; height: 200px; display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; background-color: #FFFFFF; border: 1px solid black; border-radius: 5px; z-index: 99999999}",
    ".dialog-sure-content p{grid-row: 1; grid-column: 1/2;}.dialog-sure-content input{grid-row: 2;}"]
})
export class AreYouSureDialog implements OnDestroy{
  protected answer : Subject<boolean> = new Subject<boolean>();

  private evListener = (ev: KeyboardEvent) => {
    if(ev.key == "Enter") {
      this.answer.next(true);
    } else if (ev.key == "Escape"){
      this.answer.next(false);
    }
  }
  constructor() {
    document.addEventListener("keydown", this.evListener);
  }

  public awaitAnswer(){
    return this.answer;
  }

  ngOnDestroy(): void {
    document.removeEventListener("keydown", this.evListener);
  }
}


@Component({
  selector: 'dash-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements AfterViewInit{
  title = 'Dashboard';
  tag : string = "";

  @ViewChild(DialogDirective, {static : true}) dialogDirective!: DialogDirective;
  dialog : AreYouSureDialog | undefined;

  constructor() {
    Chart.register(ChartDataLabels);
    Chart.register(ChartAnnotation);
    Chart.defaults.set('plugins.datalabels', {
      color: '#fff',
      formatter: (value: number, context: { dataIndex: string; }) => {
        return value == 0 ? "" : Util.formatNumbers(value);
      }
    });
    // @ts-ignore
    Chart.defaults.animation.duration = 2000;
  }

  select(selection : string) {
    SysVars.SELECTED_PAGE.next(selection);
  }

  createAreYouSureDialog(){
    console.log("createDialog")
    this.dialog = this.dialogDirective.viewContainerRef.createComponent(AreYouSureDialog).instance;
    this.dialog.awaitAnswer().subscribe(value => this.dialogDirective.viewContainerRef.clear())
    // @ts-ignore
    SysVars.SELECTED_PAGE.subscribe(() => this.dialog.awaitAnswer().next(false))

    return this.dialog;
  }

  removeAreYouSureDialog(){
    if (this.dialog) this.dialog.awaitAnswer().next(false);
  }

  protected readonly SysVars = SysVars;

  ngAfterViewInit(): void {
    SysVars.CREATE_DIALOG = () : AreYouSureDialog => this.createAreYouSureDialog();
    SysVars.REMOVE_DIALOG = () : void => this.removeAreYouSureDialog();
  }
}


