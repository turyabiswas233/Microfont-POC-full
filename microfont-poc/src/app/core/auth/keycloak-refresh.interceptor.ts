import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, from, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const keycloakRefreshInterceptor = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: unknown) => {
      if (!(error instanceof HttpErrorResponse) || error.status !== 401) {
        return throwError(() => error);
      }

      return from(authService.keycloak.updateToken(29)).pipe(
        switchMap((refreshed: boolean) => {
          const token = authService.keycloak.token || '';
          if (refreshed && token) {
            authService.decodeToken();
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${token}`,
              },
            });
            return next(retryReq);
          }

          authService.login();
          return throwError(() => error);
        }),
        catchError((refreshError: unknown) => {
          authService.login();
          return throwError(() => refreshError);
        })
      );
    })
  );
};
