import {Injectable} from "@angular/core";
import {Http, URLSearchParams, Response} from "@angular/http";
import {Observable} from "rxjs";

@Injectable()
export class ReportsService {

  private static API_BASE = 'https://t6lqw400oe.execute-api.us-east-1.amazonaws.com/api/';
  private static API_STATS_AGGREGATED = 'stats/aggregated';

  constructor(private http: Http) {
  }

  getAggregatedStats(from: Date, to: Date): Observable<Array<{username: string, assignedCount: number, externalCount: number}>> {
    let params = new URLSearchParams();
    params.set('from', from.toISOString());
    params.set('from', to.toISOString());

    return this.http
      .get(ReportsService.API_BASE + ReportsService.API_STATS_AGGREGATED, {
        search: params
      })
      .map((res: Response) => {
        return res.json();
      })
      .map((stats: {usersStats: any}) => {
        return Object
          .keys(stats.usersStats)
          .map((key: string) => {
            const userStats = stats.usersStats[key];
            return {
              username: key,
              assignedProjects: Object.keys(userStats.assignedProjectsStats).join(', '),
              assignedCount: userStats.assignedProjectsContributions,
              externalCount: userStats.externalRepositoriesContributions
            }
          });
      });
  }

}
