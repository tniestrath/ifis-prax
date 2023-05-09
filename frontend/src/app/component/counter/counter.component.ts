import {Component, HostBinding, Input} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";

@Component({
  selector: 'dash-counter',
  templateUrl: './counter.component.html',
  styleUrls: ['./counter.component.css', "../../component/dash-base/dash-base.component.css"]
})
export class CounterComponent extends DashBaseComponent{

  toggle : boolean = true;
  displayDetails : string = "none";

  @Input() number : string = "0";
  @Input() desc : string = "NaN";
  @Input() details: string = "";
  @Input() size : string = "small";

  @HostBinding('class.big') get isBig() {
    return this.size === "big"
  }
  @HostBinding('class.small') get isSmall() {
    return this.size === "small"
  }
  @HostBinding('class.double-big') get isDoubleBig() {
    return this.size === "double-big"
  }
  @HostBinding('class.double') get isDouble() {
    return this.size === "double"
  }

  onToggle(){
    if (this.toggle){
      if (this.size == "small"){
        this.size = "big";
      }
      if (this.size == "double") {
        this.size = "double-big"
      }
      this.toggle = !this.toggle;
      this.displayDetails = "flex";
    } else {
      if (this.size == "big"){
        this.size = "small";
      }
      if (this.size == "double-big") {
        this.size = "double"
      }
      this.toggle = !this.toggle;
      this.displayDetails = "none";
    }
  }
}
