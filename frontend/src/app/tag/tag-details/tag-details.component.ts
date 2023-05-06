import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Tag} from "../Tag";

@Component({
  selector: 'dash-tag-details',
  templateUrl: './tag-details.component.html',
  styleUrls: ['./tag-details.component.css']
})
export class TagDetailsComponent {

  @Input() data : Tag = new Tag("0", "");
  @Input() clicked = new EventEmitter<Tag>();


  onClick() {
    this.clicked.emit(this.data);
  }
}
