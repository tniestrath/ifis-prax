import {Injectable} from '@angular/core';
@Injectable({
  providedIn: 'root'
})
export class PdfService {

  private oldElementStyle : string = "";
  private oldChildStyle : string = "";
  private oldGridStyle : string = "";

  constructor() {
  }

  public bringInFormat(element : HTMLElement) {
    let child = element.firstElementChild as HTMLElement;
    // @ts-ignore
    let grid = element.firstElementChild.firstElementChild as HTMLElement;
    // @ts-ignore
    this.oldElementStyle = element.getAttribute("style");
    // @ts-ignore
    this.oldChildStyle = child.getAttribute("style");
    // @ts-ignore
    this.oldGridStyle = child.getAttribute("style");

    element.style.display = "flex";
    element.style.flexDirection = "row";
    element.style.justifyContent = "center";

    child.style.display = "block";
    child.style.width = "21cm";
    child.style.height = "29.7cm";
    child.style.margin = "0";
    child.style.padding = "0";
    child.style.backgroundColor = "white";
    child.style.overflow = "clip";

    grid.style.gridTemplateRows = "repeat(8, calc(calc(100% - 10px) / 6))"
  }
  public restoreStyle(element : HTMLElement){
    if (this.oldElementStyle != "") {
      element.setAttribute("style", this.oldElementStyle);
      let child = element.firstElementChild as HTMLElement;
      child.setAttribute("style", this.oldChildStyle);
      // @ts-ignore
      let grid = element.firstElementChild.firstElementChild as HTMLElement;
      grid.setAttribute("style", this.oldGridStyle);
      this.oldElementStyle = "";
      this.oldChildStyle = "";
      this.oldGridStyle = "";
    }
  }
}
