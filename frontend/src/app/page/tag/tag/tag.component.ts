import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Tag} from "../Tag";
import {DashBaseComponent} from "../../../component/dash-base/dash-base.component";

@Component({
  selector: 'dash-tag-details',
  templateUrl: './tag.component.html',
  styleUrls: ['./tag.component.css']
})
export class TagComponent extends DashBaseComponent{

  @Input() data : Tag = new Tag("0", "", "");
  @Input() override clicked = new EventEmitter<Tag>();


  onClick() {
    this.clicked.emit(this.data);
  }
}
