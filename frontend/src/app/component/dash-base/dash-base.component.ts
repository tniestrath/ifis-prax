import {AfterViewInit, Component, ElementRef, OnInit} from '@angular/core';
import {AppComponent} from "../../app.component";
import {DbService} from "../../services/db.service";

@Component({
  selector: 'dash-dash-base',
  styleUrls: ['./dash-base.component.css'],
  templateUrl: 'dash-base.component.html'
})
export class DashBaseComponent{


  protected tooltip : HTMLSpanElement;

  constructor(protected element : ElementRef, protected db : DbService) {
    let helpButton : HTMLElement;
    helpButton = document.createElement("div");
    helpButton.innerText = "?";
    helpButton.style.height = "30px";
    helpButton.style.width = "30px";
    helpButton.style.textAlign = "center";
    helpButton.style.fontSize = "25px";
    helpButton.style.border = "1px solid black";
    helpButton.style.borderRadius = "5px";
    helpButton.style.position = "absolute";
    helpButton.style.top = "5px";
    helpButton.style.right = "5px";
    helpButton.style.boxSizing = "border-box";
    helpButton.classList.add("help-button");
    this.element.nativeElement.style.position = "relative";
    this.element.nativeElement.appendChild(helpButton);

    let tooltipContainer = document.createElement("div");
    tooltipContainer.setAttribute("style", "position: relative; display: inline-block;");
    helpButton.appendChild(tooltipContainer);

    this.tooltip = document.createElement("span");
    this.tooltip.setAttribute("style",
      "visibility: hidden;\n" +
      "  background-color: #fff;\n" +
      "  color: #000;\n" +
      "  border: 1px solid black;\n" +
      "  box-sizing: border-box;\n" +
      "  text-align: left;\n" +
      "  font-size: 15px;\n" +
      "  padding: 5px;\n" +
      "  border-radius: 6px;\n" +
      "  position: absolute;\n" +
      "  top: -23px;\n" +
      "  right: -9.5px;\n" +
      "  min-height: 30px;\n" +
      "  min-width: 250px;\n" +
      "  z-index: 1;");
    tooltipContainer.appendChild(this.tooltip);

    helpButton.addEventListener("mouseenter", () => {this.tooltip.style.visibility = "visible"});
    helpButton.addEventListener("mouseleave", () => {this.tooltip.style.visibility = "hidden"});
  }
}
