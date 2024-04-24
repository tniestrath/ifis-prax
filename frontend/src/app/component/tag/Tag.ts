import {DbObject} from "../../services/DbObject";

export class Tag extends DbObject{
  constructor(public override id : string, public override name : string) {
    super(id, name);
  }
}

export class TagRanking extends Tag{
  constructor(public override id : string, public override name : string, public viewsPost : number, public viewsCat : number, public count : number) {
    super(id, name);
  }

  compareByViewsPost(other: TagRanking): number {
    return other.viewsPost - this.viewsPost;
  }

  compareByViewsCat(other: TagRanking): number {
    return other.viewsCat - this.viewsCat;
  }

  compareByViews(other: TagRanking): number {
    return (other.viewsPost + other.viewsCat) - (this.viewsPost + this.viewsCat);
  }

  compareByCount(other: TagRanking): number {
    return other.count - this.count;
  }
}
export class TagStats extends TagRanking{
  constructor(public override id : string, public override name : string, public override viewsPost : number, public override viewsCat : number, public override count : number, public date : string) {
    super(id, name, viewsPost, viewsCat, count);
  }
}

export class UserTagDist extends Tag{
  constructor(public override id : string = "0", public override name : string = "", public count : number = -1, public ranking : string = "#-1/-1", public percentage : number = 0, public userIDs : number[] = []) {
    super(id, name);
  }
}
