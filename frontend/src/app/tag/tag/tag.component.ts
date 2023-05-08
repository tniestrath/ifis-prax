import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Tag} from "../Tag";

@Component({
  selector: 'dash-tag-details',
  templateUrl: './tag.component.html',
  styleUrls: ['./tag.component.css']
})
export class TagComponent {

  @Input() data : Tag = new Tag("0", "", "");
  @Input() clicked = new EventEmitter<Tag>();


  onClick() {
    this.clicked.emit(this.data);
  }
}
