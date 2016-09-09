import { Routes, RouterModule } from '@angular/router';
import { ContributorsDashboardComponent } from './contributors-dashboard/contributors-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

const appRoutes: Routes = [
  {path: 'work', component: ContributorsDashboardComponent},
  {path: '**', component: PageNotFoundComponent}
];

export const routing = RouterModule.forRoot(appRoutes);
