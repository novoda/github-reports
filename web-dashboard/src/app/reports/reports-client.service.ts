import { Injectable } from '@angular/core';
import { ReportsService } from './reports.service';
import { Observable } from 'rxjs';
import { UserStats } from './user-stats';

@Injectable()
export class ReportsClient {

  constructor(private reportsService: ReportsService) {
  }

  getAggregatedStats(from: Date, to: Date): Observable<Array<UserStats>> {

    return this.reportsService
      .getAggregatedStats(from, to)
      .map((stats: {usersStats: any}) => {
        return Object
          .keys(stats.usersStats)
          .map(this.toUserStats(stats));
      });
  }

  private toUserStats(stats: {usersStats: any}) {
    return (key: string) => {
      const userStats = stats.usersStats[key];
      let projects = Object.keys(userStats.assignedProjectsStats);
      if (projects.length === 0) {
        projects = ['No assignment'];
      }
      const normalizedProjects = this.normaliseProjects(projects);
      const allProjects = this.removeDuplicates(normalizedProjects);
      return new UserStats(
        key,
        allProjects,
        userStats.assignedProjectsContributions,
        userStats.externalRepositoriesContributions
      );
    };
  }

  private normaliseProjects(projects: string[]) {
    return projects
      .map(project => {
        return project.replace(/(: (Scheduled|Verified))/g, '');
      });
  }

  private removeDuplicates(normalizedProjects: string[]) {
    return Array
      .from(new Set(normalizedProjects))
      .join(', ');
  }

}
