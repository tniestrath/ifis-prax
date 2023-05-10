import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit, Output,
  SimpleChanges,
  Type,
  ViewChild
} from '@angular/core';
import {SelectableDirective} from "./selectable.directive";
import {SelectableComponent} from "./selectable.component";
import {Observable, Subscription} from "rxjs";
import {DbObject} from "../../services/DbObject";

export class SelectorItem {
  constructor(public component: Type<any>, public data: DbObject){}
}

@Component({
  selector: 'dash-selector',
  templateUrl: './selector.component.html',
  styleUrls: ['./selector.component.css']
})
export class SelectorComponent implements OnInit{
  @Input() columnsForm = "repeat(3, 1fr)";
  @Input() dataLoaded = new Observable<SelectorItem[]>();

  @Output() itemClick = new EventEmitter<DbObject>();

  @ViewChild(SelectableDirective, {static: true}) dashSelectable!: SelectableDirective;

  private sub = new Subscription();
  private components : SelectableComponent[] = [];


  ngOnInit(): void {
    console.log("Selector Component loaded");
    this.sub = (this.dataLoaded.subscribe(s =>{
      this.loadItems(s);
      console.log("Selector Items loaded");
    }
    ));
  }

  private loadItems(s : SelectorItem[]) {
    const viewContainerRef = this.dashSelectable.viewContainerRef;
    viewContainerRef.clear();
    s.sort((a, b) => a.data.compare(b.data));
    for (let item of s) {
      const componentRef = viewContainerRef.createComponent<SelectableComponent>(item.component);
      componentRef.instance.data = item.data;
      componentRef.setInput("clicked", this.itemClick);
    }
  }
}

