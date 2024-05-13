import {DbObject} from "../../../services/DbObject";

export class ForumPost extends DbObject{

  constructor(id: string = "", public userName : string = "", public email : string = "", public title : string = "", public date : string = "", public forum : string = "", public topic : string = "", public body : string = "", public preRatingEmail : string = "good", public preRatingSwearing : string = "good") {
    super(id, id);
  }
}
