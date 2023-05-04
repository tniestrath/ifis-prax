import {DbObject} from "../services/DbObject";

export interface WPTerm extends DbObject{
  id : string;
  name : string;
  slug : string;
  termGroup : number;
}

export interface Tag extends DbObject{
  id : string;
  name : string;
}
