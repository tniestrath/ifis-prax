import {Component, OnInit} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {DashColors} from "../../../util/Util";

@Component({
  selector: 'dash-forum-profanity-filter-adder',
  templateUrl: './forum-profanity-filter-adder.component.html',
  styleUrls: ['./forum-profanity-filter-adder.component.css', "../../dash-base/dash-base.component.css"]
})
export class ForumProfanityFilterAdderComponent extends DashBaseComponent implements OnInit{
  checkbox : HTMLInputElement | undefined;
  listItems : HTMLElement | null | undefined;
  public badWords : string = "";
  ngOnInit(): void {
    this.setToolTip("Hier können weitere Wörter zur Filterliste hinzugefügt werden.<br><br>Hinter der geschwärzten Box sind die unzulässigen Wörter verdeckt")
    this.api.getForumBadWords().then(value => this.badWords = value.toString().replaceAll(",", ", "));
    this.checkbox = (document.getElementById("profanity-list-visibility") as HTMLInputElement)
    this.listItems = document.getElementById("profanity-list-items");
  }

  onSubmit(){
    let input = document.getElementById("profanity-filter-input");
    if ((input as HTMLInputElement).value.length > 2) {
      this.api.addForumBadWord((input as HTMLInputElement).value).then(r => {
        if (r) {
          (input as HTMLInputElement).value = "";
          (input as HTMLInputElement).placeholder = "Zu Filterndes Wort";
          this.reload();
        } else {
          (input as HTMLInputElement).value = "";
          (input as HTMLInputElement).placeholder = "Dieses Wort ist schon in der Liste";
        }
      });
      return true;
    }else return false;
  }
  onWithdraw(){
    let input = document.getElementById("profanity-filter-input");
    if ((input as HTMLInputElement).value.length > 2) {
      this.api.removeForumBadWord((input as HTMLInputElement).value).then(r => {
        if (r) {
          (input as HTMLInputElement).value = "";
          (input as HTMLInputElement).placeholder = "Zu Filterndes Wort";
          this.reload();
        } else {
          (input as HTMLInputElement).value = "";
          (input as HTMLInputElement).placeholder = "Dieses Wort ist nicht in der Liste";
        }
      });
      return true;
    }else return false;
  }

  onProfanityClick() {
    let listItems = document.getElementById("profanity-list-items");
    // @ts-ignore
    if (this.checkbox && this.checkbox.checked){
      this.reload()
      // @ts-ignore
      listItems.style.visibility = "visible"
    } else {
      // @ts-ignore
      listItems.style.visibility = "hidden"
    }
  }

  reload(){
    if (this.checkbox && this.checkbox.checked) {
      this.api.getForumBadWords().then(value => {
        this.badWords = value.toString().replaceAll(",", ", ");
        this.cdr.detectChanges();
      });
    }
  }
}
