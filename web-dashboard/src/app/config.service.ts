import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { ReplaySubject, Observable } from 'rxjs';
import { Config } from './Config';

@Injectable()
export class ConfigService {

  private subject: ReplaySubject<Config>;

  constructor(private http: Http) {
    this.subject = new ReplaySubject<Config>(1);
    this.http
      .get('config.json')
      .map((response: Response) => {
        return <Config> response.json();
      })
      .subscribe(this.subject);
  }

  private getConfig(): Observable<Config> {
    return this.subject.asObservable();
  }

  getApiBase(): Observable<string> {
    return this.getConfig()
      .map((config: Config) => {
        return config.api;
      })
      .catch(() => {
        return Observable.from([null]);
      })
      .first();
  }

}
