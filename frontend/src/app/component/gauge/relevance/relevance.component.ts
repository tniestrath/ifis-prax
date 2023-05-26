import {AfterViewInit, Component, EventEmitter} from '@angular/core';
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {DashBaseComponent} from "../../dash-base/dash-base.component";

@Component({
  selector: 'dash-relevance',
  templateUrl: './relevance.component.html',
  styleUrls: ['./relevance.component.css', "../../dash-base/dash-base.component.css"]
})
export class RelevanceComponent extends DashBaseComponent {
  canvas_id: string = "rel";
  chart: any;

  colors : string[] = ["rgb(149,29,64)", "rgb(229,229,229)"];
  cutout: string = "80%";

  type : string = "rel";
  postID : string = "10445";
  postName: string = "";

  createChart(value : number, max : number){

    const canvas  = document.getElementById(this.canvas_id);
    // @ts-ignore
    var ctx : CanvasRenderingContext2D = (canvas as HTMLCanvasElement).getContext("2d");
    let img = new Image();
    img.src = "../../assets/flame.png";

    // @ts-ignore
    canvas.height = canvas.width*0.8;

    ctx.fillStyle = "#E5E5E5FF";
    // @ts-ignore
    let fillHeight = (value / max) * canvas.height;
    // @ts-ignore
    ctx.fillRect(0, 0, canvas.width , canvas.height - fillHeight - 5);

    ctx.fillStyle = "#951D40FF";
    // @ts-ignore
    ctx.fillRect(0, canvas.height - fillHeight, canvas.width , canvas.height);

    ctx.globalCompositeOperation = 'destination-in';

    // @ts-ignore
    ctx.drawImage(img, canvas.width*0.1,5, canvas.height, canvas.height-5);
    ctx.save();

    ctx.globalCompositeOperation = 'source-over';

    ctx.fillStyle = "#000";
    ctx.textAlign = "center";
    ctx.textBaseline = "bottom";
    // @ts-ignore
    ctx.font = canvas.height/3.5 + "px sans-serif";
    // @ts-ignore
    ctx.fillText(  ((value / max) * 100).toFixed(), canvas.width/2, canvas.height+5);
  }

  ngOnInit(): void {
    this.setToolTip("Ihr Beitrag mit der höchsten berechneten Performance (aufg. Aufrufe der ersten 7 Tage)");

    this.db.getPerformanceById(this.postID).then(data => {
      this.createChart(data[0], data[1]);
    });
    this.db.getPostById(this.postID).then(post => this.postName = post.title);
  }

}
