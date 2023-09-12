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
  Red = "rgb(148,28,62)",
  DarkRed = "rgb(84, 16, 35)",
  Blue = "rgb(90, 121, 149)",
  Red_50 = "rgba(148,28,62, .5)",
  DarkRed_50 = "rgba(84, 16, 35, .5)",
  Blue_50 = "rgba(90, 121, 149, .5)",
  DarkBlue = "#354657",
  Black = "#000",
  Grey = "#E5E5E5"
}
