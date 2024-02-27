import { Component } from '@angular/core';
import {DbObject} from "../../../../services/DbObject";
import {Newsletter} from "../../Newsletter";

@Component({
  selector: 'dash-newsletter-list-item',
  templateUrl: './newsletter-list-item.component.html',
  styleUrls: ['./newsletter-list-item.component.css']
})
export class NewsletterListItemComponent {
  data : Newsletter = new Newsletter("","", 0, 0, 0, 0, []);
  protected interactionTimeMax = this.data.interactionTimes.indexOf(Math.max(...this.data.interactionTimes));

  protected readonly Math = Math;
}
