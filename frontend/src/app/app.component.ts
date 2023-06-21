import {Component, EventEmitter} from '@angular/core';
import {CookieService} from "ngx-cookie-service";
import {Observable, Subject} from "rxjs";



@Component({
  selector: 'dash-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Dashboard';
  selected = new Subject<string>();
  tag : string = "";

  selectedSearch : string = "";

  constructor() {
  }

  select(selection : string) {
    this.selected.next(selection);
  }
}


