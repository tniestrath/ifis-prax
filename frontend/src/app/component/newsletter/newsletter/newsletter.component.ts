import {Component, OnInit} from '@angular/core';
import {DashColors} from "../../../util/Util";
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Newsletter} from "../Newsletter";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-newsletter',
  templateUrl: './newsletter.component.html',
  styleUrls: ['./newsletter.component.css', "../../dash-base/dash-base.component.css"]
})
export class NewsletterComponent extends DashBaseComponent implements OnInit{
  data : Newsletter = new Newsletter("", "_blank", 0, 0, 0, 0, []);
  protected interactionTimeMax = 0;
  protected readonly DashColors = DashColors;

  title : string = "Aktueller Newsletter"

  ngOnInit(): void {
    this.db.getLatestNewsletter().then(res => this.data = res);
    this.interactionTimeMax = this.data.interactionTimes.indexOf(Math.max(...this.data.interactionTimes));
    console.log(this.interactionTimeMax + " : " + this.data.interactionTimes)

    SysVars.SELECTED_NEWSLETTER.subscribe( nl => {
      this.data = nl;
      this.title = "Ausgewählter Newsletter";
    })
  }

  protected readonly Number = Number;
}
