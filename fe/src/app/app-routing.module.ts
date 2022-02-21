import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./home/login/login.component";
import {ResultComponent} from "./home/result/result.component";

const routes: Routes = [
  {path: "", component: LoginComponent},
  {path: "result", component: ResultComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
