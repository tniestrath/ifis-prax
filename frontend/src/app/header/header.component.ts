import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'dash-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit{

  navElements = ["Kennzahlen", "Themen", "Einzelstatistiken"];

  constructor() {
  }

  ngOnInit(): void {
  }


}
