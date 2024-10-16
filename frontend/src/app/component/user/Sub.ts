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

  public static getPrettyString(subWithCount : SubWithCount, filter : string) : string {
    let text : string = "";

    if (subWithCount.tag && filter != "tag"){
      if (subWithCount.tag != "none") {
        text += "Thema: " + subWithCount.tag + " ";
      }
    }
    if (subWithCount.author && filter != "author"){
      if (subWithCount.author != "none"){
        text += "Anbieter: " + subWithCount.author + " ";
      }
    }
    if (subWithCount.type && filter != "type"){
      if (subWithCount.type != "none"){
      text += "Art: " + subWithCount.type + " ";
      }
    }
    if (subWithCount.word && filter != "word"){
      if (subWithCount.word != "none"){
        text += "Wort: " + subWithCount.word + " ";
      }
    }
    if (text.length > 0 && subWithCount.count && subWithCount.count > 0){
      text += ":: " + subWithCount.count;
    }
    if (text.length <= 0){
      text += "-";
    }

    return text;
  }
}

export class FilteredSub extends DbObject{
  constructor(public filter : string, public count : number, public list : SubWithCount[]) {
    super(filter, filter);
  }
}

export class UserSub extends DbObject{
  constructor(id : string, public userId : string, public subId : string, public time : string) {
    super(id, id);
  }
}
