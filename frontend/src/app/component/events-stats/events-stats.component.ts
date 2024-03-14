import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import Util, {DashColors} from "../../util/Util";
import {SysVars} from "../../services/sys-vars-service";

@Component({
  selector: 'dash-newsletter-stats',
  templateUrl: './events-stats.component.html',
  styleUrls: ['./events-stats.component.css', "../dash-base/dash-base.component.css"]
})
export class EventsStatsComponent extends DashBaseComponent implements OnInit{
  protected readonly Util = Util;
  protected readonly DashColors = DashColors;

  upcoming : number = 0;
  upcoming_today : number = 0;
  upcoming_yesterday : number = 0;

  current : number = 0;
  current_today : number = 0;
  current_yesterday : number = 0;
  current_text : string = "";


  u_congresses = 0;
  u_messes = 0;
  u_seminars = 0;
  u_workshops = 0;
  u_rest = 0;

  c_congresses = 0;
  c_messes = 0;
  c_seminars = 0;
  c_workshops = 0;
  c_rest = 0;

  ngOnInit(): void {
    this.db.getEvents().then(res => {
      for (let event of res) {
        let eventSplits = event.split("|");
        if (eventSplits[0].startsWith("u")) {
          this.upcoming++;
          this.createEventTooltip(eventSplits[1], "u");
        }
        if (eventSplits[0].startsWith("c")) {
          this.current++;
          this.createEventTooltip(eventSplits[1], "c");
        }
      }
    }).then( () =>
      this.db.getEventsYesterday().then(res => {
        for (let event of res) {
          if (event.startsWith("u")) this.upcoming_yesterday++;
          if (event.startsWith("c")) this.current_yesterday++;
        }
      })).then(() => {
      this.upcoming_today = this.upcoming - this.upcoming_yesterday;
      this.current_today = this.current - this.current_yesterday;
    });

    this.setToolTip("Hier sind die aktuellen Veranstaltungen angezeigt.<br><br> Mit Hover über die Zahlen werden genauere Daten angezeigt.");
  }

  createEventTooltip(event : string, type: string) {
    if (type == "u") {
      if (event.startsWith("k")) this.u_congresses++;
      if (event.startsWith("m")) this.u_messes++;
      if (event.startsWith("s")) this.u_seminars++;
      if (event.startsWith("w")) this.u_workshops++;
      if (event.startsWith("r")) this.u_rest++;
    } else {
      if (event.startsWith("k")) this.c_congresses++;
      if (event.startsWith("m")) this.c_messes++;
      if (event.startsWith("s")) this.c_seminars++;
      if (event.startsWith("w")) this.c_workshops++;
      if (event.startsWith("r")) this.c_rest++;
    }
  }

  getEventToolTip(type : string){
    if (type == "u") {
      return  "Kongresse: " + this.u_congresses + "<br>" +
              "Messen: " + this.u_messes + "<br>" +
              "Seminare: " + this.u_seminars + "<br>" +
              "Workshops: " + this.u_workshops + "<br>" +
              "Sonstige: " + this.u_rest;
    } else {
      return  "Kongresse: " + this.c_congresses + "<br>" +
              "Messen: " + this.c_messes + "<br>" +
              "Seminare: " + this.c_seminars + "<br>" +
              "Workshops: " + this.c_workshops + "<br>" +
              "Sonstige: " + this.c_rest;
    }
  }
}
@Component({
  selector: 'dash-newsletter-stats',
  templateUrl: './events-stats.component.html',
  styleUrls: ['./events-stats.component.css', "../dash-base/dash-base.component.css"]
})
export class UserEventsStatsComponent extends EventsStatsComponent{

  override ngOnInit(): void {
    this.db.getUserEventCount(SysVars.USER_ID).then((res : {newEvents: string[], countTotal: number, countOld: number}) => {
      for (let event of res.newEvents) {
        let eventSplits = event.split("|");
        if (eventSplits[0].startsWith("u")) {
          this.upcoming++;
          this.createEventTooltip(eventSplits[1], "u");
        }
        if (eventSplits[0].startsWith("c")) {
          this.current++;
          this.createEventTooltip(eventSplits[1], "c");
        }
      }
    }).then( () =>
      this.db.getUserEventCountYesterday(SysVars.USER_ID).then(res => {
        for (let event of res) {
          if (event.startsWith("u")) this.upcoming_yesterday++;
          if (event.startsWith("c")) this.current_yesterday++;
        }
      })).then(() => {
      this.upcoming_today = this.upcoming - this.upcoming_yesterday;
      this.current_today = this.current - this.current_yesterday;
    });

    this.setToolTip("Hier sind die aktuellen Veranstaltungen des Anbieters angezeigt.<br><br> Mit Hover über die Zahlen werden genauere Daten angezeigt.");
  }
}
