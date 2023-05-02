import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'dash-tag-details',
  templateUrl: './tag-details.component.html',
  styleUrls: ['./tag-details.component.css']
})
export class TagDetailsComponent {

  @Input() tag: string = "";
  @Output() selectedTag = new EventEmitter<string>();


  onClick() {
    this.selectedTag.emit(this.tag);
  }
}
