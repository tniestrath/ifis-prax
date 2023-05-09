import {DbObject} from "../../services/DbObject";

export interface WPTerm extends DbObject{
  id : string;
  name : string;
  slug : string;
  termGroup : number;
}

export class Tag implements DbObject{
  constructor(public id : string, public name : string, public  img_src : string) {
  }
}
