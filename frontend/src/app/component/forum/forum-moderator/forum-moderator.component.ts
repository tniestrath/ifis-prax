import {Component, OnInit, ViewChild} from '@angular/core';
import {DashBaseComponent} from "../../dash-base/dash-base.component";
import {ForumModerationListComponent} from "../forum-moderation-list/forum-moderation-list.component";
import {ForumModerationDisplayComponent} from "../forum-moderation-display/forum-moderation-display.component";
import {SysVars} from "../../../services/sys-vars-service";
import {SelectorItem} from "../../../page/selector/selector.component";
import {
  ForumModerationListItemComponent
} from "../forum-moderation-list/forum-moderation-list-item/forum-moderation-list-item.component";
import {ForumPost} from "../forum-moderation-list/ForumPost";
import {ForumStat} from "../forum-stats/forum-stats.component";
import {filter, Subscription} from "rxjs";


@Component({
  selector: 'dash-forum-moderator',
  templateUrl: './forum-moderator.component.html',
  styleUrls: ['./forum-moderator.component.css', "/../../dash-base/dash-base.component.css"]
})
export class ForumModeratorComponent extends DashBaseComponent implements OnInit{

  @ViewChild(ForumModerationListComponent, {static : true}) list!: ForumModerationListComponent;
  @ViewChild(ForumModerationDisplayComponent, {static : true}) display!: ForumModerationDisplayComponent;

  listenerToggle: boolean = true;

  selectedFilter : ForumStat = new ForumStat("", 0,0,0,0);

  private filterListener! : Subscription;

  private evListener = (ev: KeyboardEvent) => {
    if (ev.ctrlKey && this.listenerToggle){
      switch (ev.key) {
        case "Insert":
        case "ArrowUp":
          this.listenerToggle = false;
          this.display.onAcceptClick();
          break;
        case "Delete":
        case "ArrowDown":
          this.listenerToggle = false;
          this.display.onDeleteClick()
          break;
        case "End":
        case "ArrowRight":
          this.listenerToggle = false;
          this.display.onStackClick();
          break;
      }
    }
  }
  private evUpListener = (ev: KeyboardEvent) => {
    switch (ev.key) {
      case "Insert":
      case "ArrowUp":
        this.listenerToggle = true;
        break;
      case "Delete":
      case "ArrowDown":
        this.listenerToggle = true;
        break;
      case "End":
      case "ArrowRight":
        this.listenerToggle = true;
        break;
    }
  }

  ngOnInit(): void {
    this.setToolTip("", 0,false);

    SysVars.SELECTED_FORUM_POST.subscribe(post => {
      if (post) {
        this.api.unlockForumPost(this.display.data.id);
        this.list.selectorItems.push(new SelectorItem(ForumModerationListItemComponent, this.display.data));
        let index = this.list.selectorItems.findIndex((item: SelectorItem) => {
          return item.data.id == (post as ForumPost).id
        });
        this.display.data = this.list.selectorItems.splice(index, 1)[0].data as ForumPost;
        this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
          this.display.isLocked = res;
          this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
        })
      }
      this.cdr.detectChanges();
      this.list.selectorItemsLoaded.next(this.list.selectorItems);
      this.list.g_cdr.detectChanges();
      this.display.resetEditButton();
    });
    this.filterListener = SysVars.SELECTED_FORUM_FILTER.subscribe(filter => {
      //TODO: REPLACE NAME WITH SEARCH? OR FIND OTHER SOLUTION
      this.selectedFilter = filter;
      this.list.onModeratedCheckboxChange(this.list.isModeratedChecked());
    })

    this.list.onModeratedCheckboxChange = (checked : boolean) => {
      if(this.display.data.id != ""){
        this.api.unlockForumPost(this.display.data.id);
      }
      if (checked){
        this.list.reload(this.api.getModeratedForumPostsByFilter(this.selectedFilter.forumId, this.selectedFilter.catId, this.selectedFilter.topicId, "").then(data  =>{
          if (data.length > 0){
            this.bulkDisplayDataMapping(data);
            this.display.data = data.splice(0, 1)[0];
            this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
              this.display.isLocked = res;
              this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
            })
            this.cdr.detectChanges();
          }
          else {
            this.display.data = new ForumPost()
          }
          return data;
        }), ForumModerationListItemComponent);
      } else {
        this.list.reload(this.api.getUnmoderatedForumPostsByFilter(this.selectedFilter.forumId, this.selectedFilter.catId, this.selectedFilter.topicId, "").then(data  =>{
          if (data.length > 0){
            this.bulkDisplayDataMapping(data);
            this.display.data = data.splice(0, 1)[0];
            this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
              this.display.isLocked = res;
              this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
            })
            this.cdr.detectChanges();
          }
          else {
            this.display.data = new ForumPost()
          }
          return data;
        }), ForumModerationListItemComponent);
      }
      SysVars.REMOVE_DIALOG.call(this);
    }
    this.list.load(this.api.getUnmoderatedForumPostsByFilter(this.selectedFilter.forumId, this.selectedFilter.catId, this.selectedFilter.topicId, "").then(data  =>{
      if (data.length > 0){
        this.bulkDisplayDataMapping(data);
        this.display.data = data.splice(0, 1)[0];
        this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
          this.display.isLocked = res;
          this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
        })
        this.cdr.detectChanges();
      }
      else {
        this.display.data = new ForumPost()
      }
      return data;
    }), ForumModerationListItemComponent);

    this.display.onDeleteClick = () => {
      SysVars.CREATE_DIALOG("sure").awaitAnswer().subscribe((value: boolean) => {
        if (value){
          this.api.deleteForumPost(this.display.data.id).then(value => {
            if(value){
              if (this.list.selectorItems.length > 0){
                this.display.data = this.list.selectorItems.splice(0, 1)[0].data as ForumPost;
                this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
                  this.display.isLocked = res;
                  this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
                })
              } else {
                this.display.data = new ForumPost();
              }
              this.cdr.detectChanges();
              this.list.selectorItemsLoaded.next(this.list.selectorItems);
              this.list.g_cdr.detectChanges();
              this.display.resetEditButton();
            }
          });
        } else {
        }
      })
    }
    this.display.onAcceptClick = () => {
        this.api.modifyForumPost(this.modify(), true).then(value => {
          if (value) {
            this.display.data = this.displayDataMapping(this.display.data);
            if (this.list.selectorItems.length > 0){
              this.display.data = this.list.selectorItems.splice(0, 1)[0].data as ForumPost;
              this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
                this.display.isLocked = res;
                this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
              })
            } else {
              this.display.data = new ForumPost();
            }
            this.cdr.detectChanges();
            this.list.selectorItemsLoaded.next(this.list.selectorItems);
            this.list.g_cdr.detectChanges();
            this.display.resetEditButton();

          }
        });
    }
    this.display.onStackClick = () => {
      this.api.modifyForumPost(this.modify(), this.list.isModeratedChecked()).then(value => {
        if (this.list.selectorItems.length > 0){
          this.list.selectorItems.push(new SelectorItem(ForumModerationListItemComponent, this.displayDataMapping(this.display.data)));
          this.display.data = this.list.selectorItems.splice(0, 1)[0].data as ForumPost;
          this.api.isForumPostLocked(this.display.data.id, SysVars.ACCOUNT.id).then(res => {
            this.display.isLocked = res;
            this.api.getForumPostLinksById(this.display.data.id).then(res => this.display.setPostLinks(res));
          })
        } else {
          this.display.data = new ForumPost();
        }
        this.cdr.detectChanges();
        this.list.selectorItemsLoaded.next(this.list.selectorItems);
        this.list.g_cdr.detectChanges();
        this.display.resetEditButton();
      });
    }

    document.addEventListener("keydown", this.evListener);
    document.addEventListener("keyup", this.evUpListener);
  }

  private bulkDisplayDataMapping(data : ForumPost[]) : ForumPost[] {
    for (let post of data) {
      post = this.displayDataMapping(post);
    }
    return data;
  }

  private displayDataMapping(data : ForumPost) : ForumPost{
    data.rawBody = data.body;
    let quoteRegex = new RegExp("\\[quote.* data-postid=\"(\\d+)\"\]", "gm");
    for(let match of data.body.matchAll(quoteRegex)){
      this.api.getForumPostById(match[1]).then(parent => {
        data.body = data.body.replace(match[0],
          "<div contentEditable=\"false\" style=\"color: grey; margin-left: 5px\">Zitat von @" + parent.userName + ":</div>" +
          "<div contentEditable=\"false\" style=\"border: 1px dotted; margin-left: 5px\">");
      });
    }
    let quoteEndRegex = new RegExp("\\[\/quote\]", "gm");
    for (let match of data.body.matchAll(quoteEndRegex)) {
      data.body = data.body.replace(match[0], "</div>");
    }

    return data;
  }

  private modify() : ForumPost {
    let update = this.display.data;
    let fields = this.display.isEditedWhere();
    for (const field of fields) {
      if (field == "body"){
        let editorField = this.display.getEditorField();
        let editedIndex = editorField?.innerHTML.lastIndexOf("</div>");
        let rawIndex = this.display.data.rawBody.lastIndexOf("[/quote]");
        let editedBody;
        if (editorField && editedIndex) {
          if (editedIndex > 0) {
            editedBody = editorField.innerHTML.slice(editedIndex);
          } else {
            editedBody = editorField.innerHTML;
          }
          if (rawIndex && rawIndex > 0 && editedIndex > 0) {
            editedBody = this.display.data.rawBody?.slice(0, rawIndex) + editedBody;
          }
        }
        // @ts-ignore
        update.body = editedBody;
      }
      if (field == "email"){
        // @ts-ignore
        update.email = this.display.getEmailField().innerHTML;
      }
      if (field == "name"){
        // @ts-ignore
        update.userName = this.display.getNameField().innerHTML;
      }
    }
    return update;
  }

  override ngOnDestroy() {
    super.ngOnDestroy();
    this.api.unlockForumPostAllByUser();
    document.removeEventListener("keydown", this.evListener);
    document.addEventListener("keyup", this.evUpListener);
    this.filterListener.unsubscribe();
  }

}
