import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import Util, {DashColors} from "../../util/Util";

@Component({
  selector: 'dash-newsletter-stats',
  templateUrl: './newsletter-stats.component.html',
  styleUrls: ['./newsletter-stats.component.css', "../dash-base/dash-base.component.css"]
})
export class NewsletterStatsComponent extends DashBaseComponent implements OnInit{
  protected readonly Util = Util;
  protected readonly DashColors = DashColors;

  verified: number = 0;
  not_jet_verified: number = 0;

  verified_today: number = 0;
  not_jet_verified_today: number = 0;

  verified_yesterday: number = 0;
  not_jet_verified_yesterday: number = 0;

  ngOnInit(): void {
    this.db.getNewsletterSubs().then(res => {
      for (let char of res) {
        if (char == "S") this.not_jet_verified++;
        if (char == "C") this.verified++;
      }
    }).then( () =>
      this.db.getNewsletterSubsYesterday().then(res => {
      for (let char of res) {
        if (char == "S") this.not_jet_verified_yesterday++;
        if (char == "C") this.verified_yesterday++;
      }
    })).then(() => {
      this.verified_today = this.verified - this.verified_yesterday;
      this.not_jet_verified_today = this.not_jet_verified - this.not_jet_verified_yesterday;
    })

  }
}
