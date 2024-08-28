export default class Util {
  static formatNumbers(n : any) : string {
    if (n == undefined) return "NaN";
    var formattedN = String(n);
    if (typeof n === "string") n = Number.parseInt(n);
    if (n < 100){
      formattedN = +parseFloat(n.toFixed( 2 )) + "";
    }
    if (n > 999){
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

  static formatDate(date : string | Date, year : boolean = false, delimiter : string = ".") : string{
    let formattedDate = "";
    let parsedDate = new Date();
    if (typeof date == "string"){
      parsedDate = new Date(Date.parse(date));
    }
    if (date instanceof Date){
      parsedDate = date;
    }
    if (parsedDate.getDate() <= 9) {
      formattedDate = formattedDate.concat("0", parsedDate.getDate().toString() + delimiter);
    } else {
      formattedDate = formattedDate.concat(parsedDate.getDate().toString() + delimiter);
    }
    if (parsedDate.getMonth() +1 <= 9) {
      formattedDate = formattedDate.concat("0", (parsedDate.getMonth() +1).toString());
    } else {
      formattedDate = formattedDate.concat((parsedDate.getMonth() +1).toString());
    } if (year){
      formattedDate = formattedDate.concat(delimiter + parsedDate.getFullYear().toString());
    }
    return formattedDate;
  }

  static getFormattedNow(offset_day?: number, delimiter = "."){
    let now = new Date(Date.now());
    let month = "";
    let day = "";
    if (offset_day) now.setDate(now.getDate() + offset_day);
    if (now.getMonth() + 1 < 10){
      month = "0" + (now.getMonth() + 1);
    } else {
      month = "" + (now.getMonth() +1);
    }
    if (now.getDate() < 10){
      day = "0" + now.getDate();
    } else {
      day = "" + now.getDate();
    }
    let formatteddate = now.getFullYear() + delimiter + month + delimiter + day;
    return formatteddate;
  }

  static readNewsletterTime(dateTime : string){
    return dateTime.substring(8, 10) + "." + dateTime.substring(5, 7) + "." + dateTime.substring(0, 4) + " " + dateTime.substring(11, 16) + "Uhr";
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

  static firstToUpperCase(string : string){
    return string[0].toUpperCase() + string.slice(1);
  }


  /**
   *
   * @param option 'plan' | 'post'
   * @param type
   */
  static getColor(option: string, type : string){
    option = option.toLowerCase();
    type = type.toLowerCase();
    if (type.startsWith("event")) return DashColors.EVENT;
    switch (option) {
      case "plan":
        switch (type) {
          case "basis":
          case "basic":
            return DashColors.PLAN_BASIC;
          case "basis-plus":
            return DashColors.PLAN_BASIC_PLUS;
          case "plus":
            return DashColors.PLAN_PLUS;
          case "premium":
          case "sponsor":
            return DashColors.PLAN_PREMIUM;
          case "moderator":
            return DashColors.MODERATOR;
          case "none":
            return DashColors.BLACK;
          case "registered":
          case "registriert":
            return DashColors.REGISTERED;
          default:
            return DashColors.GREY;
        }
      case "post":
        switch (type) {
          case "article":
          case "artikel":
            return DashColors.ARTICLE;
          case "blog":
            return DashColors.BLOG;
          case  "news":
            return DashColors.NEWS;
          case "whitepaper":
            return DashColors.WHITEPAPER;
          case "podcast":
          case"podcast_first_series":
            return DashColors.PODCAST;
          case "ratgeber":
            return DashColors.RATGEBER;
          case "newsletter":
            return DashColors.NEWSLETTER;
          case "video":
            return DashColors.VIDEO;
          default:
            return DashColors.GREY;
        }
      default:
        return DashColors.GREY;
    }
  }
  static getPlanColor(plan : string){
    return this.getColor("plan", plan);
  }

  static getColorByThreshold(value : number, threshold : number){
    let color = DashColors.BLACK;
      if (value > threshold){
        color = DashColors.RED;
      } if (value <= threshold){
        color = DashColors.ORANGE;
      } if (value <= 0){
        color = DashColors.BLUE;
      }
      return color;
  }

  static getLevenshteinDistance(s1 : string, s2 : string) : number {
    var array = new Array(s1.length + 1);
    for (var i = 0; i < s1.length + 1; i++)
      array[i] = new Array(s2.length + 1);

    for (var i = 0; i < s1.length + 1; i++)
      array[i][0] = i;
    for (var j = 0; j < s2.length + 1; j++)
      array[0][j] = j;

    for (var i = 1; i < s1.length + 1; i++) {
      for (var j = 1; j < s2.length + 1; j++) {
        if (s1[i - 1] == s2[j - 1]) array[i][j] = array[i - 1][j - 1];
        else {
          array[i][j] = Math.min(array[i][j - 1] + 1, array[i - 1][j] + 1);
          array[i][j] = Math.min(array[i][j], array[i - 1][j - 1] + 1);
        }
      }
    }
    return array[s1.length][s2.length];
  };

  static formatArray(array : string[]) : string{
    return array.toString().replace("[", "").replace("]", "");
  }

}
export enum DashColors {
  GREEN = "rgb(134,218,118)",
  OK = "rgb(134,218,118)",

  ORANGE = "rgb(218,134,118)",
  MEDIUM = "rgb(218,134,118)",

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
  PLAN_WITHOUT = "rgb(0,0,0)",

  BLACK_50 = "rgba(0,0,0, .5)",

  RATGEBER = "rgb(130,106,34)",

  NEWSLETTER = "rgb(34,106,130)",

  MODERATOR = "rgb(130,34,106)",
  EVENT = "rgb(130,34,106)",

  VIDEO = "rgb(106,34,130)",

  REGISTERED = "rgb(17,65,53)",

  FOOTER = "rgb(34,130,106)",

  GREY = "rgb(200,200,200)",
  BACKGROUND = "rgb(200,200,200)",

  GREY_50 = "rgba(200,200,200, .5)",

  DARK_GREY = "rgb(128,128,128)",

  LINKEDIN = "rgb(10,102,194)",
  TWITTER = "rgb(0,0,0)",
  FACEBOOK = "rgb(24, 119, 242)",
  YOUTUBE = "rgb(255,0,0)",

  WHITE = "rgb(255,255,255)",
  BASEPAINT = "rgb(255,255,255)",

}


