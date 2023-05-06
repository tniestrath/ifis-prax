import {
  Component,
  Directive,
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
export class SelectorComponent implements OnInit, OnChanges{
  @Input() items : SelectorItem[] = [];
  @Input() dataLoaded = new Observable<void>();

  @Output() selection = new EventEmitter<DbObject>();

  @ViewChild(SelectableDirective, {static: true}) dashSelectable!: SelectableDirective;

  private sub = new Subscription();
  private sub2 = new Subscription();
  private components : SelectableComponent[] = [];
  private clickEmitter = new EventEmitter<DbObject>();

  ngOnInit(): void {
    this.sub = (this.dataLoaded.subscribe(() =>{
      this.loadItems();
    }
    ));
    this.sub2 = (this.clickEmitter.subscribe(object => {
      let dbObject : DbObject = {id: object.id, name: object.name };
      this.selection.emit(dbObject);
      }
    ));
  }
  ngOnChanges(changes: SimpleChanges): void {
    this.loadItems();
  }

  private loadItems() {
    const viewContainerRef = this.dashSelectable.viewContainerRef;
    viewContainerRef.clear();
    for (let item of this.items) {
      const componentRef = viewContainerRef.createComponent<SelectableComponent>(item.component);
      componentRef.instance.data = item.data;
      componentRef.setInput("clicked", this.clickEmitter);
    }
  }






}

