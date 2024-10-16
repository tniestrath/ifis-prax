import {DbObject} from "../../services/DbObject";
import _default from "chart.js/dist/plugins/plugin.tooltip";
import type = _default.defaults.animations.numbers.type;

export class Sub extends DbObject{

  constructor(id : string, public type : string | undefined = undefined, public tag : string | undefined = undefined, public author : string | undefined = undefined, public word : string | undefined = undefined) {
    super(id, id);
  }
}

export class SubWithCount extends Sub{

  constructor(id : string, public count : number, type : string | undefined = undefined, tag : string | undefined = undefined, author : string | undefined = undefined, word : string | undefined = undefined) {
    super(id, type, tag, author, word);
  }

  public override toString() : string{
    return "Typ: " + this.type + " Thema: " + this.tag + " Autor: " + this.author + " Wort: " + this.word + " Anzahl: " + this.count + "\n";
  }
}

export class FilteredSub extends DbObject{
  constructor(public filter : string, public total : number, public filterDetails : SubWithCount[]) {
    super(filter, filter);
  }
}

export class UserSub extends DbObject{
  constructor(id : string, public userId : string, public subId : string, public time : string) {
    super(id, id);
  }
}
