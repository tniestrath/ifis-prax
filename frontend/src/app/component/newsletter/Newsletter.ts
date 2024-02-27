import {DbObject} from "../../services/DbObject";

export class Newsletter extends DbObject{
  public subject : string;
  public totalOpens : number;
  public OR : number;
  public interactions : number;
  public problems : number;
  public interactionTimes : number[];

  constructor(id : string, subject : string, totalOpens : number, OR : number, interactions : number, problems : number, interactionTimes : number[]) {
    super(id, subject);
    this.subject = subject;
    this.totalOpens = totalOpens;
    this.OR = OR;
    this.interactions = interactions;
    this.problems = problems;
    this.interactionTimes = interactionTimes;
  }
}
