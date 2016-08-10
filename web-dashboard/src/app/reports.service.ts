import {Injectable} from "@angular/core";
import {Http, URLSearchParams, Response} from "@angular/http";
import {Observable} from "rxjs";

@Injectable()
export class ReportsService {

  private static API_BASE = 'https://t6lqw400oe.execute-api.us-east-1.amazonaws.com/api/';
  private static API_STATS_AGGREGATED = 'stats/aggregated';

  constructor(private http: Http) {
  }

  getAggregatedStats(from: Date, to: Date): Observable<{usersStats: any}> {
    let params = new URLSearchParams();
    params.set('from', from.toISOString());
    params.set('from', to.toISOString());

    return this.http
      .get(ReportsService.API_BASE + ReportsService.API_STATS_AGGREGATED, {
        search: params
      })
      .map((res: Response) => {
        return res.json();
      });
  }

}
