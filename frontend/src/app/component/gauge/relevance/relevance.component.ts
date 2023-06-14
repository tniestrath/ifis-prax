import {AfterViewInit, Component, EventEmitter} from '@angular/core';
import {ActiveElement, Chart, ChartEvent} from "chart.js/auto";
import {EmptyObject} from "chart.js/dist/types/basic";
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {UserService} from "../../../services/user.service";
import {Post} from "../../../Post";

@Component({
  selector: 'dash-relevance',
  templateUrl: './relevance.component.html',
  styleUrls: ['./relevance.component.css', "../../dash-base/dash-base.component.css"]
})
export class RelevanceComponent extends DashBaseComponent {
  canvas_id: string = "rel";
  chart: any;

  colors : string[] = ["rgb(149,29,64)", "#5A7995"];
  cutout: string = "80%";

  type : string = "rel";
  postID : string = "10445";
  postName: string = "";

  createChart(value : number, max : number){

    const canvas  = document.getElementById(this.canvas_id);
    // @ts-ignore
    var ctx : CanvasRenderingContext2D = (canvas as HTMLCanvasElement).getContext("2d");
    let img = new Image();
    img.src = "../../assets/flame_thicc.png";

    // @ts-ignore
    canvas.height = canvas.width*0.8;

    ctx.fillStyle = this.colors[1];
    // @ts-ignore
    let fillHeight = (value / max) * canvas.height;
    // @ts-ignore
    ctx.fillRect(0, 0, canvas.width , canvas.height - fillHeight - 5);

    ctx.fillStyle = this.colors[0];
    // @ts-ignore
    ctx.fillRect(0, canvas.height - fillHeight, canvas.width , canvas.height);

    ctx.globalCompositeOperation = 'destination-in';

    // @ts-ignore
    ctx.drawImage(img, canvas.width*0.1+5,5, canvas.height-10, canvas.height-5);
    ctx.save();

    ctx.globalCompositeOperation = 'source-over';

    ctx.fillStyle = "#000";
    ctx.textAlign = "center";
    ctx.textBaseline = "bottom";
    // @ts-ignore
    ctx.font = canvas.height/3 + "px sans-serif";
    // @ts-ignore
    ctx.fillText(  ((value / max) * 100).toFixed(), canvas.width/2, canvas.height+5);
  }

  ngOnInit(): void {
    this.setToolTip("Ihr Beitrag mit der höchsten berechneten Relevanz (aufg. Aufrufe der letzten 7 Tage)");

    this.db.getMaxRelevance().then(max => {
      this.db.getUserBestPost(UserService.USER_ID, "relevance").then(data => {
        let post : Post = data;
        this.createChart(post.relevance || 0, max);
        this.postName = post.title;
      });
    })
  }

}
