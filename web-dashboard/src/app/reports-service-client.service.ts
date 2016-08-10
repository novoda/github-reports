import {Injectable} from "@angular/core";
import {ReportsService} from "./reports.service";
import {Observable} from "rxjs";

@Injectable()
export class ReportsServiceClient {

  constructor(private reportsService: ReportsService) {
  }

  getAggregatedStats(from: Date, to: Date): Observable<Array<{
    username: string,
    assignedProjects: string,
    assignedCount: number,
    externalCount: number
  }>> {

    return this.reportsService
      .getAggregatedStats(from, to)
      .map((stats: {usersStats: any}) => {
        return Object
          .keys(stats.usersStats)
          .map((key: string) => {
            const userStats = stats.usersStats[key];
            let projects = Object.keys(userStats.assignedProjectsStats);
            if (projects.length === 0) {
              projects = ['No assignment'];
            }
            const normalizedProjects = projects
              .map(project => {
                return project.replace(/(: (Scheduled|Verified))/g, '')
              })
              .join(', ');
            return {
              username: key,
              assignedProjects: normalizedProjects,
              assignedCount: userStats.assignedProjectsContributions,
              externalCount: userStats.externalRepositoriesContributions
            }
          });
      });
  }

}
