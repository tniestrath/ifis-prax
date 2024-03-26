import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import Util, {DashColors} from "../../../util/Util";
import {SysVars} from "../../../services/sys-vars-service";

@Component({
  selector: 'dash-seo-stat-display',
  templateUrl: './seo-stat-display.component.html',
  styleUrls: ['./seo-stat-display.component.css', "../../dash-base/dash-base.component.css"]
})
export class SeoStatDisplayComponent extends DashBaseComponent implements OnInit{


  public seoNowDesktop : number = 0;
  public seoChangeDesktop : number = 0;

  public seoNowMobile : number = 0;
  public seoChangeMobile : number = 0;

  ngOnInit(): void {
    this.setToolTip("Her sehen sie den Sichtbarkeitsindex des Marktplatzes, die kleinere Zahl beschreibt den Unterschied zur Messung der letzten Woche")
    SysVars.SEO_DATA.subscribe( value => {
      this.seoNowDesktop = value.desktop.now;
      this.seoChangeDesktop = value.desktop.now - value.desktop.last;

      this.seoNowMobile = value.mobile.now;
      this.seoChangeMobile = value.mobile.now - value.mobile.last;
    })
  }

  protected readonly DashColors = DashColors;
  protected readonly Util = Util;
  protected readonly Number = Number;
}
