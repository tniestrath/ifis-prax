import {DbObject} from "../../services/DbObject";

export interface WPTerm extends DbObject{
  id : string;
  name : string;
  slug : string;
  termGroup : number;
}

export class Tag extends DbObject{
  constructor(public override id : string, public override name : string, public  img_src : string) {
    super(id, name);
  }
}
