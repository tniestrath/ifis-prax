import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[dashSelectable]'
})
export class SelectableDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
