import {Component, Input} from '@angular/core';

@Component({
  selector: 'dash-counter',
  templateUrl: './counter.component.html',
  styleUrls: ['./counter.component.css']
})
export class CounterComponent {

  @Input() number : string = "0";
  @Input() desc : string = "NaN";

}
