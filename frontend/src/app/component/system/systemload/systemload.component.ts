import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {Chart} from "chart.js/auto";
import {DashColors} from "../../../util/Util";
import {SysVars} from "../../../services/sys-vars-service";


export interface SystemUsage {
  cpu_record : number[],
  memory_record : number[]
}

@Component({
  selector: 'dash-systemload',
  templateUrl: './systemload.component.html',
  styleUrls: ['./systemload.component.css' , '../../dash-base/dash-base.component.css']
})
export class SystemloadComponent extends DashBaseComponent implements OnInit{
  chart_memory: any;


  ngOnInit(): void {
    this.db.getSystemUsage().then(res => {
      this.createCpuChart(res.cpu_record);
      this.createMemoryChart(res.memory_record);
    })
    setInterval(() => {
      if (SysVars.CURRENT_PAGE == "Übersicht"){
        this.db.getSystemUsageNow().then(res => {
          this.addData(this.chart, String(res.cpu), res.cpu);
          this.addData(this.chart_memory, String(res.memory), res.memory);
        });
      }
    }, 1000 * 60);
    this.setToolTip("Hier wird die Systemauslastung der letzten Stunde angezeigt.");
  }

  addData(chart: any, label: string, newData: number) {
    if (chart == undefined)return;
    chart?.data.datasets[0].data.shift();
    chart?.data.datasets[0].data.push(Number(newData));
    this.updateMaxAvg(chart);
    chart.update("none");
  }

  updateMaxAvg(chart: any) {
    let max = Math.max(...chart?.data.datasets[0].data);
    chart.options.plugins.annotation.annotations[0].value = max;
    chart.options.plugins.annotation.annotations[0].label.content = String((max * 100).toFixed()) + "%";

    let avg : number = chart.data.datasets[0].data.reduce((previousValue : number, currentValue : number) => Number(previousValue) + Number(currentValue));
    avg = avg / chart.data.datasets[0].data.length;
    chart.options.plugins.annotation.annotations[1].value = avg;
    chart.options.plugins.annotation.annotations[1].label.content = String((avg * 100).toFixed()) + "%";
  }

  createCpuChart(data : number[]){
    if (this.chart){
      this.chart.destroy();
    }

    let max = Math.max(...data);
    let cpu_avg = 0;
    data.forEach(value => {cpu_avg += value});
    cpu_avg = cpu_avg / data.length;

    // @ts-ignore
    this.chart = new Chart("systemload-cpu-chart", {
      type: "line",
      data: {
        labels: data,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: DashColors.BLUE,
          borderColor: DashColors.BLUE,
          borderJoinStyle: 'round',
          borderWidth: 1,
          pointRadius: 0
        }]
      },
      options: {
        clip: false,
        aspectRatio: .5,
        maintainAspectRatio: false,
        scales: {
          y: {
            min: 0,
            max: 1,
            ticks: {
              callback: tickValue =>{
                return String(Number(tickValue) * 100) + "%";
              },
              stepSize: .25,
              font: {
                size: 10
              }
            }
          },
          x: {
            display: false,
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          annotation: {
            annotations: [
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.RED,
                value: max,
                borderWidth: 2,
                label: {
                  content: String((max * 100).toFixed()) + "%",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 10
                  }
                }
              },
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.BLACK,
                value: cpu_avg,
                borderWidth: 2,
                label: {
                  content: String((cpu_avg * 100).toFixed()) + "%",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 10
                  }
                }
              }
            ]
          },
          title: {
            display: false,
            text: "",
            position: "bottom",
            fullSize: true,
            font: {
              size: 18,
              weight: "bold",
              family: 'Times New Roman'
            }
          },
          legend: {
            display: false,
            position: "bottom"
          },
          tooltip: {
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            },
            callbacks: {
            }
          }
        },
        interaction: {
          intersect: false
        },
        events: []
      }
    })
  }

  createMemoryChart(data : number[]){
    if (this.chart_memory){
      this.chart_memory.destroy();
    }
    let max = Math.max(...data);
    let memory_avg = 0;
    data.forEach(value => {memory_avg += value});
    memory_avg = memory_avg / data.length;

    // @ts-ignore
    this.chart_memory = new Chart("systemload-memory-chart", {
      type: "line",
      data: {
        labels: data,
        datasets: [{
          label: "",
          data: data,
          backgroundColor: DashColors.BLUE,
          borderColor: DashColors.BLUE,
          borderJoinStyle: 'round',
          borderWidth: 1,
          pointRadius: 0
        }]
      },
      options: {
        clip: false,
        aspectRatio: .5,
        maintainAspectRatio: false,
        scales: {
          y: {
            min: 0,
            max: 1,
            position: "right",
            ticks: {
              callback: tickValue =>{
                return String(Number(tickValue) * 100) + "%";
              },
              stepSize: .25,
              font: {
                size: 10
              }
            }
          },
          x: {
            display: false,
          }
        },
        plugins: {
          datalabels: {
            display: false
          },
          annotation: {
            annotations: [
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.RED,
                value: max,
                borderWidth: 2,
                label: {
                  content: String((max * 100).toFixed()) + "%",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 10
                  }
                }
              },
              {
                type: "line",
                scaleID: "y",
                borderColor: DashColors.BLACK,
                value: memory_avg,
                borderWidth: 2,
                label: {
                  content: String((memory_avg * 100).toFixed()) + "%",
                  display: true,
                  position: "center",
                  padding: 2,
                  font: {
                    size: 10
                  }
                }
              }
            ]
          },
          title: {
            display: false,
            text: "",
            position: "bottom",
            fullSize: true,
            font: {
              size: 18,
              weight: "bold",
              family: 'Times New Roman'
            }
          },
          legend: {
            display: false,
            position: "bottom"
          },
          tooltip: {
            titleFont: {
              size: 20
            },
            bodyFont: {
              size: 15
            },
            callbacks: {
            }
          }
        },
        interaction: {
          mode: "nearest",
          intersect: true
        },
        events: []
      }
    })
  }

}
