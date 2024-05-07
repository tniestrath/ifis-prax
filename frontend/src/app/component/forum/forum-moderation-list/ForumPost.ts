import {DbObject} from "../../../services/DbObject";

export class ForumPost extends DbObject{

  constructor(id: string = "", public userName : string = "", public email : string = "", public title : string = "", public date : string = "", public forum : string = "", public topic : string = "", public body : string = "", public preRating : string = "good", public parent : ForumPost | undefined = undefined) {
    super(id, id);
  }
}
