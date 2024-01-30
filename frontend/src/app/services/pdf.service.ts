import {Injectable} from '@angular/core';
import {jsPDF} from "jspdf";
import html2canvas from "html2canvas";
import {Chart} from "chart.js/auto";
import {style} from "@angular/animations";
import {overflow} from "html2canvas/dist/types/css/property-descriptors/overflow";
@Injectable({
  providedIn: 'root'
})
export class PdfService {

  private oldElementStyle : string = "";
  private oldChildStyle : string = "";

  constructor() {
  }

  public bringInFormat(element : HTMLElement) {
    let child = element.children[0] as HTMLElement;
    // @ts-ignore
    this.oldElementStyle = element.getAttribute("style");
    // @ts-ignore
    this.oldChildStyle = child.getAttribute("style");

    element.style.display = "flex";
    element.style.flexDirection = "row";
    element.style.justifyContent = "center";

    child.style.display = "block";
    child.style.width = "21cm";
    child.style.height = "29.7cm";
    child.style.margin = "0";
    child.style.padding = "0";
    child.style.backgroundColor = "white";
    child.style.overflow = "visible";
    child.style.setProperty("box-shadow", "none", "important");

    Array.from(element.querySelectorAll(".component-box")).forEach(element => {
      let htmlTag = element as HTMLElement;
      console.log(htmlTag)
      htmlTag.style.setProperty("box-shadow", "none");
    })
  }
  public restoreStyle(element : HTMLElement){
    if (this.oldElementStyle != "") {
      element.setAttribute("style", this.oldElementStyle);
      let child = element.children[0] as HTMLElement;
      child.setAttribute("style", this.oldChildStyle);
      this.oldElementStyle = "";
      this.oldChildStyle = "";
    }
  }
}
