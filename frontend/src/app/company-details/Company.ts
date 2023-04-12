export class Contact {
  private _email : string;
  private _tel : string;
  private _link : string;

  constructor(email: string, tel: string, link: string) {
    this._email = email;
    this._tel = tel;
    this._link = link;
  }
  get email(): string {
    return this._email;
  }
  set email(value: string) {
    this._email = value;
  }
  get tel(): string {
    return this._tel;
  }
  set tel(value: string) {
    this._tel = value;
  }
  get link(): string {
    return this._link;
  }
  set link(value: string) {
    this._link = value;
  }
}

export class Company{
  private _id : string;
  private _name : string;
  private _size : string;
  private _contact : Contact;
  private _countries : string[];
  private _type : string[];
  private _mediaType : string;

  constructor(id: string, name: string, size: string, contact: Contact, countries: string[], type: string[], mediaType: string) {
    this._id = id;
    this._name = name;
    this._size = size;
    this._contact = contact;
    this._countries = countries;
    this._type = type;
    this._mediaType = mediaType;
  }

  get id(): string {
    return this._id;
  }

  set id(value: string) {
    this._id = value;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }

  get size(): string {
    return this._size;
  }

  set size(value: string) {
    this._size = value;
  }

  get contact(): Contact {
    return this._contact;
  }

  set contact(value: Contact) {
    this._contact = value;
  }

  get countries(): string[] {
    return this._countries;
  }

  set countries(value: string[]) {
    this._countries = value;
  }

  get type(): string[] {
    return this._type;
  }

  set type(value: string[]) {
    this._type = value;
  }

  get mediaType(): string {
    return this._mediaType;
  }

  set mediaType(value: string) {
    this._mediaType = value;
  }
}
