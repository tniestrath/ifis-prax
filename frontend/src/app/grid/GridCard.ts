import {Type} from "@angular/core";
import {DashBaseComponent} from "../component/dash-base/dash-base.component";

export interface GridCard {
  // @ts-ignore
  type: Type<DashBaseComponent>;
  row : number;
  col : number;
  width : number;
  height : number;
}
