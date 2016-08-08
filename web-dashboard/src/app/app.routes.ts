import {RouterConfig, provideRouter} from "@angular/router";
import {WorkDashboardComponent} from "./work-dashboard/work-dashboard.component";
import {PageNotFoundComponent} from "./page-not-found/page-not-found.component";

const routes: RouterConfig = [
  {path: 'work', component: WorkDashboardComponent},
  {path: '**', component: PageNotFoundComponent}
];

export const appRouterProviders = [
  provideRouter(routes)
];
