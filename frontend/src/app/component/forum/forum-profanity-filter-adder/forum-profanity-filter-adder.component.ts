import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-forum-profanity-filter-adder',
  templateUrl: './forum-profanity-filter-adder.component.html',
  styleUrls: ['./forum-profanity-filter-adder.component.css', "../../dash-base/dash-base.component.css"]
})
export class ForumProfanityFilterAdderComponent extends DashBaseComponent implements OnInit{

  public badWords : string = "";
  ngOnInit(): void {
    this.api.getForumBadWords().then(value => this.badWords = value.toString().replaceAll(",", ", "));
  }

  onSubmit(){
    let input = document.getElementById("profanity-filter-input");
    this.api.addForumBadWord((input as HTMLInputElement).value).then(r => console.log("DELETED?: " + r));
  }

  onProfanityClick() {
    let checkbox = document.getElementById("profanity-list-visibility");
    let list = document.getElementById("profanity-list");
    // @ts-ignore
    if (checkbox.checked){
      // @ts-ignore
      list.style.color = DashColors.WHITE;
    } else {
      // @ts-ignore
      list.style.color = DashColors.BLACK;
    }
  }
}
