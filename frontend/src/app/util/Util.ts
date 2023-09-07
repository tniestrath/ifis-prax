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
}
export enum DashColors {
  Red = "rgb(148,28,62)",
  DarkRed = "rgb(84, 16, 35)",
  Blue = "rgb(90, 121, 149)",
  DarkBlue = "#354657",
  Black = "#000",
  Grey = "#E5E5E5"
}
