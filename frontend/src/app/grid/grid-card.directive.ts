import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[dashGridCard]'
})
export class GridCardDirective {

  constructor(public viewContainerRef : ViewContainerRef) { }

}
