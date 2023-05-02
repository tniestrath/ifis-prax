import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Observable} from "rxjs";


@Component({
  selector: 'dash-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit{

  navElements = ["Kennzahlen", "Themen", "Einzelstatistiken"];
  @Output() selected = new EventEmitter<string>();
  @Output() searchInput = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit(): void {

  }

  setSelected(page : string){
    this.selected.emit(page)
  }

  onKey(value : string) {
    this.searchInput.emit(value);
  }
}
