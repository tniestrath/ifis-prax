import {
  AfterViewInit,
  Component,
  ElementRef,
  OnInit,
  Pipe,
  PipeTransform,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";
import {ExternalService} from "../external-services-list.component";
import {DomSanitizer} from "@angular/platform-browser";
import {SysVars} from "../../../../services/sys-vars-service";



@Pipe({ name: 'unsafeURL'})
export class SafeUrlPipe implements PipeTransform {
  constructor(private sanitized: DomSanitizer) {
  }

  transform(value: string) {
    return this.sanitized.bypassSecurityTrustResourceUrl(value);
  }
}


@Component({
  selector: 'dash-external-services-list-item',
  templateUrl: './external-services-list-item.component.html',
  styleUrls: ['./external-services-list-item.component.css']
})
export class ExternalServicesListItemComponent extends DashListItemComponent {
  override data : ExternalService = new ExternalService("", "", "" , 2);
  @ViewChild('iframe', { static: true }) iframe: ElementRef<HTMLDivElement> | undefined;

  serviceTimer : number = 0;

  private evListener = (ev: KeyboardEvent) => {
    if (ev.key == "Escape"){
          this.onClick()
    }
  }

  override onClick() {
    if (this.iframe) {
      if (!SysVars.IS_POPUP){
        this.iframe.nativeElement.classList.remove("hidden");
        document.addEventListener("keydown", this.evListener);
        SysVars.IS_POPUP = true;
      } else {
        this.iframe.nativeElement.classList.add("hidden");
        document.removeEventListener("keydown", this.evListener);
        SysVars.IS_POPUP = false;
      }
    }
  }

  onLoad($event: Event){
    this.serviceTimer = Date.now() - this.serviceTimer;
    if (this.serviceTimer > 1000){
      this.data.check = 1;
    } else if (this.serviceTimer > 2000){
      this.data.check = 2;
    } else {
      this.data.check = 0;
    }
  }

  onLoadStart() {
    this.serviceTimer = Date.now();
    console.log("LOADSTART " + this.data.name);
  }

  protected readonly Date = Date;



}
