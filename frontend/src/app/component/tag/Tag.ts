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
  constructor(public override id : string, public override name : string, public relevance : string, public views : string, public count : string) {
    super(id, name);
  }

  compareByRelevance(other: TagRanking): number {
    return Number.parseFloat(other.relevance) - Number.parseFloat(this.relevance);
  }

  compareByViews(other: TagRanking): number {
    return Number.parseFloat(other.views) - Number.parseFloat(this.views);
  }

  compareByCount(other: TagRanking): number {
    return Number.parseFloat(other.count) - Number.parseFloat(this.count);
  }
}
export class TagStats extends TagRanking{
  constructor(public override id : string, public override name : string, public override relevance : string, public override views : string, public override count : string, public date : string) {
    super(id, name, relevance, views, count);
  }
}

export class UserTagDist extends DbObject{
  constructor(public override id : string = "0", public override name : string = "", public count : number = -1, public ranking : string = "#-1/-1", public percentage : number = 0, public userIDs : number[] = []) {
    super(id, name);
  }
}
