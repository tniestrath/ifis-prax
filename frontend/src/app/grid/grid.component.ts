import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {Observable, Subscription} from "rxjs";
import {GridCardDirective} from "./grid-card.directive";
import {DashBaseComponent} from "../component/dash-base/dash-base.component";
import {GridCard} from "./GridCard";
import {SysVars} from "../services/sys-vars-service";

@Component({
  selector: 'dash-grid',
  templateUrl: './grid.component.html',
  styleUrls: ['./grid.component.css']
})
export class GridComponent implements OnInit{
  @Input() columnsForm = "repeat(6, 1fr)";
  @Input() rowsForm = "repeat(6, 1fr)"
  @Input() padding : string = "5px";
  @Input() dataLoaded = new Observable<GridCard[]>();

  @Output() itemClick = new EventEmitter<DashBaseComponent>();

  @ViewChild(GridCardDirective, {static: true}) dashGridCard!: GridCardDirective

  private index = 0;

  private sub = new Subscription();
  ngOnInit(): void {
    console.log("Grid Component loaded");
    this.sub = (this.dataLoaded.subscribe(g =>{
        this.loadCards(g);
        console.log("Grid Items loaded " + g.length);
      }
    ));
  }

  public loadCards(g : GridCard[]) {
    const viewContainerRef = this.dashGridCard.viewContainerRef;
    viewContainerRef.clear();
    this.index = 0;
    for (let item of g) {
      // @ts-ignore
      const componentRef = viewContainerRef.createComponent<typeof item.type>(item.type, {index: this.index});
      componentRef.setInput("clicked", this.itemClick);
      componentRef.setInput("grid_reference", this);
      // @ts-ignore
      componentRef.setInput("grid_index", this.index);
      componentRef.location.nativeElement.style.gridArea = item.row +"/" + item.col + "/" + (item.row + item.height) + "/" + (item.col + item.width);
      this.index++;
    }
  }
}
