import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-newsletter-stats',
  templateUrl: './newsletter-stats.component.html',
  styleUrls: ['./newsletter-stats.component.css', "../../dash-base/dash-base.component.css"]
})
export class NewsletterStatsComponent extends DashBaseComponent implements OnInit{
  protected readonly Util = Util;
  protected readonly DashColors = DashColors;

  verified: number = 0;
  verified_today: number = 0;
  verified_yesterday: number = 0;

  openRate : number = 0;

  ngOnInit(): void {
    this.api.getNewsletterSubs().then(res => {
      for (let char of res) {
        if (char == "C") this.verified++;
      }
    }).then( () =>
      this.api.getNewsletterSubsYesterday().then(res => {
      for (let char of res) {
        if (char == "C") this.verified_yesterday++;
      }
    })).then(() => {
      this.verified_today = this.verified - this.verified_yesterday;
    }).then( () =>
      this.api.getNewslettersOR().then(res => {
        this.openRate = res;
      })
    )

    this.setToolTip("Hier werden die aktuellen Newsletter-Abonnenten und die globale Öffnungsrate angezeigt.");
  }

  protected readonly Math = Math;
}
