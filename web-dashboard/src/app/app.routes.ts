import { Routes, RouterModule }   from '@angular/router';
import { WorkDashboardComponent } from './work-dashboard/work-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

const appRoutes: Routes = [
  {path: 'work', component: WorkDashboardComponent},
  {path: '**', component: PageNotFoundComponent}
];

export const routing = RouterModule.forRoot(appRoutes);
