import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthoricationRequest} from "../model/login/AuthoricationRequest";
import {Observable} from "rxjs";
import {AuthoricationResponse} from "../model/login/AuthoricationResponse";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  API = "http://localhost:8080/";

  constructor(private httpClient: HttpClient) { }

  login(authoricationRequest: any): Observable<AuthoricationResponse> {
    return this.httpClient.post<AuthoricationResponse>("http://localhost:8080/login", authoricationRequest);
  }


}
