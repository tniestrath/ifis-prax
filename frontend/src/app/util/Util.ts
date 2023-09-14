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

  static formatDate(date : string | Date) : string{
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
      formattedDate = formattedDate.concat("0", (parsedDate.getMonth() +1).toString()  + "-");
    } else {
      formattedDate = formattedDate.concat((parsedDate.getMonth() +1).toString() + "-");
    }
    formattedDate = formattedDate.concat(parsedDate.getFullYear().toString());
    return formattedDate;
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
  PLAN_SPONSOR = "rgb(0,0,0)",

  BLACK_50 = "rgba(0,0,0, .5)",

  GREY = "rgb(229,229,229)",
  PLAN_WITHOUT = "rgb(229,229,229)",
  BACKGROUND = "rgb(229,229,229)",

  GREY_50 = "rgba(229,229,229, .5)"
}
