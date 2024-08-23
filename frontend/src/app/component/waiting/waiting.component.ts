import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../dash-base/dash-base.component";
import {DashColors} from "../../util/Util";

class Snake {
  length = 1;
  color = DashColors.RED;
}

@Component({
  selector: 'dash-waiting',
  templateUrl: './waiting.component.html',
  styleUrls: ['./waiting.component.css']
})
export class WaitingComponent extends DashBaseComponent implements OnInit{
  fruits: any[] = [];
  snake: Snake = new Snake();

  ngOnInit(): void {
    this.run();
  }

  run(){
  }

  start(){
  }
  stop(){
  }

  pause(){
  }

}
