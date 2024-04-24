import {DbObject} from "../../services/DbObject";
import {Tag} from "../tag/Tag";

export class Post extends DbObject{
  constructor(public title : string = "placeholder title",
              public date : string = "00/00/0000",
              public type : string = "default",
              public clicks : string = "0",
              public tags : Tag[] = [],
              public performance: number = 0,
              public relevance: number = 0,
              public searchSuccesses: number = 0,
              public searchSuccessRate: number = 0,
              public referrings: number = 0,
              public articleReferringRate: number = 0,
              public lettercount: number = 0,
              public duration: number = 0,
              public authors: string[] = [""],
              public downloads: number = 0,
              public content: string = "",
              public img: string = "",
              public override id : string = "-1") {
    super(id, title);
  }
}

