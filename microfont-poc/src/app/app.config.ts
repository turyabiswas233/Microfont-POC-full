import {
  ApplicationConfig,
  EnvironmentProviders,
  provideBrowserGlobalErrorListeners, Provider,
  provideZoneChangeDetection
} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideNativeDateAdapter} from '@angular/material/core';
import {provideToastr} from 'ngx-toastr';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {tokenInterceptor} from './TokenInterceptor';
import { provideAuth } from './core/auth/auth.provider';
import { authInterceptor } from './core/auth/auth.interceptor';
import { keycloakRefreshInterceptor } from './core/auth/keycloak-refresh.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideNativeDateAdapter(),
    provideAnimations(),
    provideToastr(),
    provideHttpClient(
      withInterceptors([tokenInterceptor, authInterceptor, keycloakRefreshInterceptor])
    ),
    provideAuth(),
  ]
};
