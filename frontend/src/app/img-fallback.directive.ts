import {Directive, ElementRef, HostListener, Input} from '@angular/core';

@Directive({
  selector: '[dashImgFallback]'
})
export class ImgFallbackDirective {

  @Input() ImgFallback: string = "assets/user_img/404_img_.jpg";

  constructor(private eRef: ElementRef) { }

  @HostListener('error')
  loadFallbackOnError(){
    const element : HTMLImageElement = <HTMLImageElement>this.eRef.nativeElement;
    element.src = this.ImgFallback;
  }
}
