export default class Util {
  static formatNumbers(n : any) : string {
    var formattedN = String(n);
    if (typeof n === "string") n = Number.parseInt(n);
    if (n > 1000){
      formattedN = +parseFloat(String(n / 1000)).toFixed( 1 ) + "K";
    }
    if (n > 9999){
      formattedN = (n/1000).toFixed() + "K";
    }
    if (n > 1000000){
      formattedN = (n/1000000).toFixed(1) + "M";
    }
    if (n > 9999999){
      formattedN = (n/1000000).toFixed() + "M";
    }
    return formattedN;
  }

  static formatDate(date : string | Date, year? : boolean) : string{
    let formattedDate = "";
    let parsedDate = new Date();
    if (typeof date == "string"){
      parsedDate = new Date(Date.parse(date));
    }
    if (date instanceof Date){
      parsedDate = date;
    }
    if (parsedDate.getDate() <= 9) {
      formattedDate = formattedDate.concat("0", parsedDate.getDate().toString() + "-");
    } else {
      formattedDate = formattedDate.concat(parsedDate.getDate().toString() + "-");
    }
    if (parsedDate.getMonth() +1 <= 9) {
      formattedDate = formattedDate.concat("0", (parsedDate.getMonth() +1).toString());
    } else {
      formattedDate = formattedDate.concat((parsedDate.getMonth() +1).toString());
    } if (year){
      formattedDate = formattedDate.concat("-" + parsedDate.getFullYear().toString());
    }
    return formattedDate;
  }

  static getFormattedNow(){
    let now = new Date(Date.now());
    let formatteddate = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate();
    return formatteddate;
  }



  static readFormattedDate(date : string){
    let splitted = date.split("-");
    // @ts-ignore
    return new Date(Date.UTC(Number.parseInt(splitted.at(2)), Number.parseInt(splitted.at(1))-1, Number.parseInt(splitted.at(0))));
  }

  static getDayString(day : number){
    switch (day) {
      case 0: return "So";
      case 1: return "Mo";
      case 2: return "Di";
      case 3: return "Mi";
      case 4: return "Do";
      case 5: return "Fr";
      case 6: return "Sa";
      default : return "ERR";
    }
  }
}
export enum DashColors {
  RED = "rgb(148,28,62)",
  BLOG = "rgb(148,28,62)",
  PLAN_PLUS = "rgb(148,28,62)",

  RED_50 = "rgba(148,28,62, .5)",

  DARK_RED = "rgb(84, 16, 35)",
  NEWS = "rgb(84, 16, 35)",
  PLAN_PREMIUM = "rgb(84, 16, 35)",

  DARK_RED_50 = "rgba(84, 16, 35, .5)",

  BLUE = "rgb(90, 121, 149)",
  ARTICLE = "rgb(90, 121, 149)",
  PLAN_BASIC = "rgb(90, 121, 149)",

  BLUE_50 = "rgba(90, 121, 149, .5)",

  DARK_BLUE = "rgb(53,70,87)",
  WHITEPAPER = "rgb(53,70,87)",
  PLAN_BASIC_PLUS = "rgb(53,70,87)",

  DARK_BLUE_50 = "rgba(53,70,87, .5)",

  BLACK = "rgb(0,0,0)",
  PODCAST = "rgb(0,0,0)",
  PLAN_SPONSOR = "rgb(0,0,0)",

  BLACK_50 = "rgba(0,0,0, .5)",

  GREY = "rgb(229,229,229)",
  PLAN_WITHOUT = "rgb(229,229,229)",
  BACKGROUND = "rgb(229,229,229)",

  GREY_50 = "rgba(229,229,229, .5)"
}
