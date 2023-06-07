export class Post {
  constructor(public title : string,
              public date : string,
              public type : string,
              public clicks : string,
              public tags : string[],
              public performance: number,
              public relevance: number,
              public searchSuccesses?: number,
              public searchSuccessRate?: number,
              public referrings?: number,
              public articleReferringRate?: number,
              public id? : number) {
  }
}
