import { Routes, RouterModule } from '@angular/router';
import { ContributorsVsSlackersDashboardComponent } from './contributors-vs-slackers-dashboard/contributors-vs-slackers-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

const appRoutes: Routes = [
  {path: 'work', component: ContributorsVsSlackersDashboardComponent},
  {path: '**', component: PageNotFoundComponent}
];

export const routing = RouterModule.forRoot(appRoutes);
