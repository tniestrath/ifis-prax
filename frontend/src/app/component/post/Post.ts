export class Post {
  constructor(public title : string = "placeholder title",
              public date : string = "00/00/0000",
              public type : string = "default",
              public clicks : string = "0",
              public tags : string[] = [],
              public performance: number = 0,
              public relevance: number = 0,
              public searchSuccesses: number = 0,
              public searchSuccessRate: number = 0,
              public referrings: number = 0,
              public articleReferringRate: number = 0,
              public lettercount: number = 0,
              public id : number = -1) {
  }
}
