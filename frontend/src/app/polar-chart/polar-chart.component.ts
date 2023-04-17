import {Component, Input, OnInit} from '@angular/core';
import { Chart } from 'chart.js/auto';
import _default from "chart.js/dist/plugins/plugin.tooltip";
import {CompanyService} from "../services/company.service";
import {Company} from "../company-details/Company";

@Component({
  selector: 'dash-polar-chart',
  templateUrl: './polar-chart.component.html',
  styleUrls: ['./polar-chart.component.css']
})
export class PolarChartComponent implements OnInit{
  chart : any;

  @Input() labels : string[] = [];
  @Input() data : number[] = [];

  constructor(private service: CompanyService) {
  }

  createChart(labels : string[], data : number[]){
    this.chart = new Chart("chart", {
      type: 'doughnut', data: {
        labels: labels,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: [
            "rgb(255, 0, 0)",
            "rgb(255, 255, 0)",
            "rgb(0, 255, 255)",
            "rgb(0, 0, 255)"
          ]
        }]
      }
    })
  }

  ngOnInit(): void {
    //this.createChart(["#Hardware", "#Software", "#Home", "#VoIP"], [3, 1, 2, 6]);
    let companies :Company[] = [];
    let keywords : string[] = [];
    let counts = {};
    this.service.getAllCompanies().then(res => companies = res).finally(
      () => {
        for (const company of companies) {
          let keys = company.keywords.split(",");
          if (keys[0] == ""){
            keys[0] = "None";
          }
          keys.forEach(key => keywords.push(key))
        }
        const map = keywords.reduce((acc, e) => acc.set(e, (acc.get(e) || 0) + 1), new Map());
        map.delete("None");
        map.forEach( (value, key, map) => {if(value < 0){map.delete(key)}})
        this.createChart(Array.from(map.keys()), Array.from(map.values()));
      }
    );


  }
}


