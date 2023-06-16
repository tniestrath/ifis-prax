import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Observable} from "rxjs";


@Component({
  selector: 'dash-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  @Output() selected = new EventEmitter<string>();
  navElements = ["Kennzahlen", "Admin", "Einzelstatistiken"];


  constructor() {
  }

  ngOnInit(): void {

  }

  setSelected(page : string){
    this.selected.emit(page)
  }
}
