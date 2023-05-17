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

export class TagRanking extends DbObject{
  constructor(public override id : string, public override name : string, public rank : string, public count : string) {
    super(id, name);
  }

  compareByRank(other: TagRanking): number {
    return Number.parseFloat(this.rank) - Number.parseFloat(other.rank);
  }

  compareByCount(other: TagRanking): number {
    return Number.parseFloat(other.count) - Number.parseFloat(this.count);
  }
}
