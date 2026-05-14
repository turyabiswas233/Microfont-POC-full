import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService } from './core/auth/auth.service';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token || sessionStorage.getItem('access_token') || '';

  // APIs where token must be added
  const shouldAttachToken =
    req.url.startsWith(environment.apiBaseUrl) ||
    req.url.startsWith(environment.sentinelUrl)

  if (shouldAttachToken && token) {
    // Get session data
    const userId = auth.getStoredUserId();
    const appId = auth.getStoredAppId() || sessionStorage.getItem('appId') || '';
    const functionId = auth.getFunctionId();
    const functionName = auth.getFunctionName();

    // Build headers object
    let headers = {
      Authorization: `Bearer ${token}`,
    } as any;

    // Add custom headers if values exist
    if (userId) {
      headers['X-User-Id'] = userId;
    }
    if (appId) {
      headers['X-App-Id'] = appId;
    }
    if (functionId) {
      headers['X-Function-Id'] = functionId;
    }

    if (functionName) {
      headers['X-Function-Name'] = functionName;
    }

    const authReq = req.clone({
      setHeaders: headers,
    });

    return next(authReq);
  }

  // Default: no token added
  return next(req);
};

