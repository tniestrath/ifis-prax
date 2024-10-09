import {DbObject} from "../../services/DbObject";

export class Sub extends DbObject{

  constructor(id: string, public type : string, public tag : string, public author : string, public word : string) {
    super(id, id);
  }
}

export class UserSub extends DbObject{
  constructor(id : string, public userId : string, public subId : string, public time : string) {
    super(id, id);
  }
}
