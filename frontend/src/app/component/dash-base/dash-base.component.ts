import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnInit} from '@angular/core';
import {DbService} from "../../services/db.service";
import {GridComponent} from "../../grid/grid.component";
import {UserService} from "../../services/user.service";
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'dash-dash-base',
  styleUrls: ['./dash-base.component.css'],
  templateUrl: 'dash-base.component.html'
})
export class DashBaseComponent {

  @Input() protected clicked : {} | undefined;
  @Input() protected grid_reference : GridComponent | undefined;
  @Input() protected grid_index : number | undefined;

  protected tooltip : HTMLSpanElement;
  protected helpButton : HTMLElement;

  constructor(protected element : ElementRef, protected db : DbService, protected us : UserService, protected cs : CookieService, protected cdr : ChangeDetectorRef) {
    this.helpButton = document.createElement("div");
    this.helpButton.style.color = "#A0A0A0";
    this.helpButton.innerText = "?";
    this.helpButton.style.height = "30px";
    this.helpButton.style.width = "30px";
    this.helpButton.style.textAlign = "center";
    this.helpButton.style.fontSize = "25px";
    this.helpButton.style.border = "1px solid #A0A0A0";
    this.helpButton.style.borderRadius = "5px";
    this.helpButton.style.position = "absolute";
    this.helpButton.style.top = "5px";
    this.helpButton.style.right = "5px";
    this.helpButton.style.boxSizing = "border-box";
    this.helpButton.classList.add("help-button");
    this.element.nativeElement.style.position = "relative";
    this.element.nativeElement.appendChild(this.helpButton);

    let tooltipContainer = document.createElement("div");
    tooltipContainer.setAttribute("style", "position: relative; display: inline-block;");
    this.helpButton.appendChild(tooltipContainer);

    this.tooltip = document.createElement("span");
    this.tooltip.setAttribute("style",
      "visibility: hidden;\n" +
      "  background-color: #fff;\n" +
      "  color: #000;\n" +
      "  border: 1px solid #A0A0A0;\n" +
      "  box-sizing: border-box;\n" +
      "  text-align: left;\n" +
      "  font-size: 15px;\n" +
      "  padding: 5px;\n" +
      "  border-radius: 5px;\n" +
      "  position: absolute;\n" +
      "  top: -24px;\n" +
      "  right: -8px;\n" +
      "  min-height: 30px;\n" +
      "  min-width: 250px;\n" +
      "  z-index: 1;");
    tooltipContainer.appendChild(this.tooltip);
    this.helpButton.addEventListener("mouseenter", () => {this.tooltip.style.visibility = "visible"; this.cdr.detectChanges()});
    this.helpButton.addEventListener("mouseleave", () => {this.tooltip.style.visibility = "hidden"; this.cdr.detectChanges()});
  }

  protected setToolTip(text?: string, enabled = true){
    if (typeof text === "string") {
      this.tooltip.innerText = text;
    }
    if (!enabled){
      this.helpButton.style.display = "none";
    }
  }
}
