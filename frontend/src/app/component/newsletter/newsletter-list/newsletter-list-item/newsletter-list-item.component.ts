import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Newsletter} from "../../Newsletter";
import {DashColors} from "../../../../util/Util";
import {SysVars} from "../../../../services/sys-vars-service";

@Component({
  selector: 'dash-newsletter-list-item',
  templateUrl: './newsletter-list-item.component.html',
  styleUrls: ['./newsletter-list-item.component.css']
})
export class NewsletterListItemComponent implements AfterViewInit{
  data : Newsletter = new Newsletter("","", 0, 0, 0, 0, []);
  protected interactionTimeMax = -1;

  bgColor: string = "#FFFFFF";
  protected readonly Math = Math;
  protected readonly DashColors = DashColors;

  onClick() {
    SysVars.SELECTED_NEWSLETTER.emit(this.data);
  }

  ngAfterViewInit(): void {
    this.interactionTimeMax = this.data.interactionTimes.indexOf(Math.max(...this.data.interactionTimes, 1));
  }

}
