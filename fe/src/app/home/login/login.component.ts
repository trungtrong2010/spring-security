import {Component, OnInit} from '@angular/core';
import {LoginService} from "../../service/login.service";
import {FormControl, FormGroup} from "@angular/forms";
import {AuthoricationRequest} from "../../model/login/AuthoricationRequest";
import {AuthoricationResponse} from "../../model/login/AuthoricationResponse";
import {Router} from "@angular/router";
import {FacebookLoginProvider, GoogleLoginProvider, SocialAuthService} from "angularx-social-login";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  authoricationRequest : AuthoricationRequest | any;
  authoricationResponse :AuthoricationResponse | any;
  isLogin: boolean | any;

  constructor(private loginService: LoginService,
              private router: Router,
              private socialAuthService: SocialAuthService) {
  }

  formLogin = new FormGroup({
    username : new FormControl(),
    password: new FormControl()
  });

  ngOnInit(): void {
    this.socialAuthService.authState.subscribe(
      data => {
        // if data != null => isLogin = true
        this.isLogin = (data != null)
      }
    );
  }

  loginWithGoogle() {
    this.socialAuthService.signIn(GoogleLoginProvider.PROVIDER_ID).then(
      data => {
        console.log(data)
      }
    );
  }

  loginWithFacebook() {
    this.socialAuthService.signIn(FacebookLoginProvider.PROVIDER_ID).then(
      data => {
        console.log(data);
      }
    );
  }

  login() {
    this.loginService.login(this.formLogin.value).subscribe(
      (data) => {
        this.authoricationResponse = data;
        console.log(this.authoricationResponse);
        this.router.navigateByUrl("/result")
      }
    );
  }

  signOut() {
    this.socialAuthService.signOut();
  }
}
