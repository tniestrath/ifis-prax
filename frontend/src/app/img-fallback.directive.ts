import {Directive, ElementRef, HostListener, Input} from '@angular/core';

@Directive({
  selector: '[dashImgFallback]'
})
export class ImgFallbackDirective {

  @Input() ImgFallback: string = "";

  constructor(private eRef: ElementRef) { }

  @HostListener('error')
  loadFallbackOnError(){
    const element : HTMLImageElement = <HTMLImageElement>this.eRef.nativeElement;
    element.src = this.ImgFallback;
  }
}
