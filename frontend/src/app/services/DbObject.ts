export class DbObject {
  id : string;
  name : string;
  constructor(id : string, name : string) {
    this.id = id;
    this.name = name;
  }
  compare(other : DbObject) : number {
    return this.name.toLowerCase()
      .replace("ü", "ue")
      .replace("ä", "ae")
      .replace("ö", "oe")
      .replace(/[\W_]+/g,"")
      .localeCompare(other.name.toLowerCase());
  }

  toString() : string {
    return "id : " + this.id + "| name : " + this.name;
  }
}
