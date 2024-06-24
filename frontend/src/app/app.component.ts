import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {Chart} from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";
import ChartAnnotation from "chartjs-plugin-annotation";
import Util from "./util/Util";
import {SysVars} from "./services/sys-vars-service";
import {AreYouSureDialog, Dialog, DialogDirective, FilterDialog} from "./util/Dialog";




@Component({
  selector: 'dash-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements AfterViewInit{
  title = 'Dashboard';
  tag : string = "";

  @ViewChild(DialogDirective, {static : true}) dialogDirective!: DialogDirective;
  dialog : Dialog | undefined;

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
  createDialog(name : string){
    switch (name) {
      case "sure":
        console.log("createSureDialog")
        this.dialog = this.dialogDirective.viewContainerRef.createComponent(AreYouSureDialog).instance;
        this.dialog.awaitAnswer().subscribe(value => this.dialogDirective.viewContainerRef.clear())
        // @ts-ignore
        SysVars.SELECTED_PAGE.subscribe(() => this.dialog.awaitAnswer().next(false))

        return this.dialog;
      case "filter":
        console.log("createFilterDialog")
        this.dialog = this.dialogDirective.viewContainerRef.createComponent(FilterDialog).instance;
        this.dialog.awaitAnswer().subscribe(value => this.dialogDirective.viewContainerRef.clear())
        // @ts-ignore
        SysVars.SELECTED_PAGE.subscribe(() => this.dialog.awaitAnswer().next(false))

        return this.dialog;
      default:
        return undefined;
    }
  }

  removeDialog(){
    if (this.dialog) this.dialog.awaitAnswer().next(false);
  }

  protected readonly SysVars = SysVars;

  ngAfterViewInit(): void {
    SysVars.CREATE_DIALOG = (name : string) : Dialog => <Dialog>this.createDialog(name);
    SysVars.REMOVE_DIALOG = () : void => this.removeDialog();
  }
}


