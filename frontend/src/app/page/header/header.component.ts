import {Component, EventEmitter, Output} from '@angular/core';
import {Subject} from "rxjs";


@Component({
  selector: 'dash-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  @Output() selected = new Subject<string>();
  navElements = ["Overview", "Posts", "Tags", "Users"];


  constructor() {
  }

  ngOnInit(): void {

  }

  setSelected(page : string){
    this.selected.next(page);
  }
}
