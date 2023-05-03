import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Tag} from "../Tag";

@Component({
  selector: 'dash-tag-details',
  templateUrl: './tag-details.component.html',
  styleUrls: ['./tag-details.component.css']
})
export class TagDetailsComponent {

  @Input() tag: Tag | undefined;
  @Output() selectedTag = new EventEmitter<Tag>();


  onClick() {
    this.selectedTag.emit(this.tag);
  }
}
