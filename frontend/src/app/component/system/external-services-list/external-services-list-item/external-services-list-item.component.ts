import {AfterViewInit, Component, OnInit, Pipe, PipeTransform, ViewChild, ViewChildren} from '@angular/core';
import {DashListItemComponent} from "../../../dash-list/dash-list-item/dash-list-item.component";
import {ExternalService} from "../external-services-list.component";
import {ForumModerationListComponent} from "../../../forum/forum-moderation-list/forum-moderation-list.component";
import {DomSanitizer} from "@angular/platform-browser";



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
export class ExternalServicesListItemComponent extends DashListItemComponent{
  override data : ExternalService = new ExternalService("", "", "" , 2);
  @ViewChild('iframe', { static: true }) iframe: HTMLIFrameElement | undefined;

  override onClick(data: any): any {

  }

  onLoad($event: Event){
    this.data.check = 0;
  }


}
