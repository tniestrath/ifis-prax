import {
  Component, ElementRef,
  EventEmitter,
  Input,
  OnInit, Output,
  Type,
  ViewChild
} from '@angular/core';
import {SelectableDirective} from "./selectable.directive";
import {SelectableComponent} from "./selectable.component";
import {Observable, Subscription} from "rxjs";
import {DbObject} from "../../services/DbObject";
import {TagListItemComponent} from "../../component/tag/tag-list/tag-list-item/tag-list-item.component";
import {DashListItemComponent} from "../../component/dash-list/dash-list-item/dash-list-item.component";

export class SelectorItem {
  constructor(public component: Type<DashListItemComponent>, public data: DbObject){}
}

@Component({
  selector: 'dash-selector',
  templateUrl: './selector.component.html',
  styleUrls: ['./selector.component.css']
})
export class SelectorComponent implements OnInit{
  @Input() columnsForm = "repeat(3, 1fr)";
  @Input() padding : string = "0 5vw";
  @Input() dataLoaded = new Observable<SelectorItem[]>();
  @Input() zebraColorMode: boolean = false;

  @Output() scrollEnd = new EventEmitter<void>();
  @Output() freeSpace = new EventEmitter<number>;

  @ViewChild(SelectableDirective, {static: true}) dashSelectable!: SelectableDirective;
  @ViewChild(SelectorComponent, {static: true}) scrollable!: SelectorComponent;

  private sub = new Subscription();
  private availableSpace : number = 0;

  constructor(protected element : ElementRef) {
  }

  ngOnInit(): void {
    this.sub = (this.dataLoaded.subscribe(s =>{
      this.loadItems(s, !(typeof TagListItemComponent == typeof s[0]?.component));
      console.log("Selector Items loaded " + s.length);
    }
    ));
    let container = this.element.nativeElement as HTMLDivElement;
    this.availableSpace = container.clientHeight;
  }

  private loadItems(s : SelectorItem[], sort : boolean) {
    const viewContainerRef = this.dashSelectable.viewContainerRef;
    var index = 0;

    viewContainerRef.clear();
    if (sort){s.sort((a, b) => a.data.compare(b.data))}
    for (let item of s) {
      const componentRef = viewContainerRef.createComponent<SelectableComponent>(item.component);
      componentRef.instance.data = item.data;
      if (index % 2 == 0 && this.zebraColorMode){
        componentRef.location.nativeElement.setAttribute("style", "background : #EFEFEF; border-radius : 5px");
        componentRef.instance.bgColor = "#EFEFEF";
      }
      if (this.columnsForm.includes("1fr 1fr") && index < 2){
        componentRef.instance.bgColor = "60px";
      }
      index++;
    }
    let container = this.element.nativeElement as HTMLDivElement;
    // @ts-ignore
    let itemHeight = (container.firstChild.firstChild as HTMLElement).clientHeight;
    this.freeSpace.next(container.clientHeight - (itemHeight * s.length));
  }

  onScroll($event: Event) {
    console.log("keep on scrollin bebe");
    let container = $event.target as HTMLDivElement;
    let scrollPos = container.scrollTop;
    let maxScroll = container.scrollHeight - container.clientHeight;
    let scrollPercentage = (scrollPos / maxScroll)* 100;

    if (scrollPercentage > 80){
      this.scrollEnd.emit();
    }
  }
}

