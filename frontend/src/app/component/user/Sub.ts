import {DbObject} from "../../services/DbObject";

export class Sub extends DbObject{

  constructor(id : string, public type : string | undefined = undefined, public tag : string | undefined = undefined, public author : string | undefined = undefined, public word : string | undefined = undefined) {
    super(id, id);
  }
}

export class SubWithCount extends Sub{

  constructor(id : string, public count : number, type : string | undefined = undefined, tag : string | undefined = undefined, author : string | undefined = undefined, word : string | undefined = undefined) {
    super(id, type, tag, author, word);
  }
}

export class UserSub extends DbObject{
  constructor(id : string, public userId : string, public subId : string, public time : string) {
    super(id, id);
  }
}
