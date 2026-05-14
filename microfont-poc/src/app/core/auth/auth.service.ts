import { HttpClient, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { of } from 'rxjs';
import { inject, Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { AuthUtils } from './auth.utils';
import { User } from '../user/user.types';
import { UserService } from '../user/user.service';
import { environment } from '../../../environments/environment';
import { GlobalActivityTrackerService } from '../../shared/services/global-activity-tracker.service';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private _authenticated: boolean = false;
  private _keycloak: Keycloak | undefined;
  private http: HttpClient = inject(HttpClient);
  private _userService = inject(UserService);
  private activityTracker = inject(GlobalActivityTrackerService);
  private _token: string = '';
  get token(): string {
    return this._token;
  }
  // -----------------------------------------------------------------------------------------------------
  // @ Accessors
  // -----------------------------------------------------------------------------------------------------

  get keycloak(): Keycloak {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: environment.keycloak.url,
        realm: environment.keycloak.realm,
        clientId: environment.keycloak.clientId
      });
    }
    return this._keycloak;
  }

  get authenticated(): boolean {
    return this._authenticated;
  }

  async init(): Promise<boolean> {
    this._authenticated = await this.keycloak.init({
      onLoad: 'check-sso',
      redirectUri: window.location.origin + window.location.pathname,
      pkceMethod: 'S256',
      checkLoginIframe: true,
      checkLoginIframeInterval: 5
    });
    if (this._authenticated) {
      this._token = this.keycloak.token || '';
      this.decodeToken();
    }
    // 🔄 Token expired → auto refresh
    this.keycloak.onTokenExpired = () => {
      this.keycloak.updateToken(29)
        .then(refreshed => {
          if (refreshed) {
            this._token = this.keycloak.token || '';
            this.decodeToken();
          }
        })
        .catch(() => {
          this.login();
        });
    };
    // 🔔 Global logout detect
    this.keycloak.onAuthLogout = () => {
      this.login();
    };

    // ⏱️ Inactivity timeout → auto logout
    this.activityTracker.inactivity$.subscribe(() => {
      console.log('🔒 Inactivity timeout - logging out user');
      this.logout();
    });

    return this._authenticated;
  }

  login(): void {
    this.keycloak.login({
      redirectUri: environment.redirectUri
    });
  }

  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.keycloak.logout({ redirectUri: environment.loginUrl });
  }

  decodeToken(): void {
    const token = this.keycloak.token || '';
    if (!token) {
      return;
    }
    sessionStorage.setItem('access_token', token);
    this.userInfo(token);
  }

  userInfo(token: string) {
    const decodeToken = AuthUtils._decodeToken(token || this.keycloak.token || '');
    if (!decodeToken)
      return;
    let user = {} as User;
    user.name = decodeToken.name;
    user.email = decodeToken.email;
    user.realmAccess = decodeToken.realm_access;
    user.resourceAccess = decodeToken.resource_access;
    user.officeId = decodeToken.officeId;
    user.orgId = decodeToken.orgId;
    user.employeeId = decodeToken.employeeId;
    user.username = decodeToken.preferred_username;
    user.clickStreamTrack = decodeToken.clickStreamTrack;
    user.userTerminalIP = decodeToken.userTerminalIP;

    // Store user ID in session storage
    if (user.username) {
      sessionStorage.setItem('userId', user.username);
    }
    if (user.employeeId) {
      sessionStorage.setItem('employeeId', user.employeeId);
    }

    // Store app ID from environment
    if (environment.appId) {
      sessionStorage.setItem('appId', environment.appId);
    }

    this._userService.user = user;
    if (user.officeId) { this.getSessionData(user.officeId); }
  }

  getSessionData(officeId: string) {
    const existingOfficeId = sessionStorage.getItem('officeId');
    if (existingOfficeId === officeId && sessionStorage.getItem('companyId')) {
      return;
    }

    this.http.get(`${environment.centrinoUrl}/office-session/getOfficeSession/${officeId}`).subscribe({
      next: (officeData: any) => {
        console.log("officeData...", officeData);
        // Store office data in sessionStorage
        sessionStorage.setItem('companyId', officeData.companyId);
        sessionStorage.setItem('companyName', officeData.companyName);
        sessionStorage.setItem('officeId', officeData.officeId);
        sessionStorage.setItem('officeCode', officeData.officeCode);
        sessionStorage.setItem('officeNm', officeData.officeNm);
        sessionStorage.setItem('entityTypeId', officeData.entityTypeId);
        sessionStorage.setItem('officeTypeId', officeData.officeTypeId);
        sessionStorage.setItem('officeControlId', officeData.officeControlId);
        sessionStorage.setItem('txnDt', officeData.txnDt);
        sessionStorage.setItem('operationMode', officeData.operationMode);
        sessionStorage.setItem('operationModeFunction', officeData.operationModeFunction);
        sessionStorage.setItem('instituteType', officeData.instituteType);
        sessionStorage.setItem('localCurrencyId', officeData.localCurrencyId);
        sessionStorage.setItem('custProdIdLen', officeData.custProdIdLen);
        sessionStorage.setItem('custAccountNoLen', officeData.custAccountNoLen);
        sessionStorage.setItem('accountNoPtrnFlag', officeData.accountNoPtrnFlag);
        sessionStorage.setItem('errorCode', officeData.errorCode);
        sessionStorage.setItem('errorMessage', officeData.errorMessage);
      },
      error: (err) => {
        console.error('Error loading office session:', err);
      }
    });
  }


  private getUserId(): string {
    const storedUserId = sessionStorage.getItem('userId');
    if (storedUserId) {
      return storedUserId;
    }

    const token = this.keycloak.token || sessionStorage.getItem('access_token') || '';
    if (!token) {
      return '';
    }

    const decoded = AuthUtils._decodeToken(token);
    return decoded?.preferred_username || '';
  }

  getResources() {
    const userId = this.getUserId();
    if (!userId) {
      console.warn('getResources: missing userId, skipping request');
      return of([]);
    }
    const params = new HttpParams({
      fromObject: { userId: userId, appId: environment.appId }
    });
    let url = environment.sentinelUrl + '/resource-access/retrieveList';
    return this.http.get<any>(url, { params }).pipe(
      map((res: any) => {
        const transformed = (res.resourceUserList || []).map((item: any) => ({
          attributes: {
            functionId: item.functionId,
            functionName: item.functionNm,
            moduleId: item.moduleId,
            moduleName: item.moduleNm,
            functionType: item.functionType,
            quickRoute: item.quickRouteNo
          },
          uris: item.appRoute,
          routePath: item.routePath,
          createFlag: item.createFlag,
          updateFlag: item.updateFlag,
          deleteFlag: item.deleteFlag,
          readFlag: item.readFlag
        }));
        return transformed;
      })
    );
  }

  getUserWiseApplications() {
    const userId = this.getUserId();
    if (!userId) {
      console.warn('getUserWiseApplications: missing userId, skipping request');
      return of([]);
    }
    const params = new HttpParams({
      fromObject: { userId: userId }
    });
    let url = environment.sentinelUrl + '/user-access/getAppsByUserId';
    return this.http.get<any>(url, { params }).pipe(
      map((res: any) => {
        return (res.apps || []).map((app: any) => ({
          appId: app.appId,
          appName: app.appName,
          appUrl: app.appUrl || ''
        }));
      })
    );
  }



  /**
   * Store function ID in session storage
   * Call this when user selects or navigates to a function
   */
  setFunctionId(functionId: string): void {
    this.clearFunctionContext();
    if (functionId) {
      sessionStorage.setItem('functionId', functionId);
      localStorage.setItem('currentFunctionId', functionId);
    }
  }

  /**
   * Store function context in both session/local storage so interceptor can attach reliably.
   */
  setFunctionContext(functionId: string, functionName: string): void {
    this.clearFunctionContext();
    if (functionId) {
      sessionStorage.setItem('functionId', functionId);
      localStorage.setItem('currentFunctionId', functionId);
    }
    if (functionName) {
      sessionStorage.setItem('functionName', functionName);
      localStorage.setItem('currentFunctionName', functionName);
    }
  }

  /**
   * Clear function context for non-function pages (e.g. home/dashboard shell).
   */
  clearFunctionContext(): void {
    sessionStorage.removeItem('functionId');
    sessionStorage.removeItem('functionName');
    localStorage.removeItem('currentFunctionId');
    localStorage.removeItem('currentFunctionName');
  }

  /**
   * Get stored function ID from session storage
   */
  getFunctionId(): string {
    return (
      sessionStorage.getItem('functionId') ||
      localStorage.getItem('currentFunctionId') ||
      ''
    );
  }

  /**
   * Get stored function name from session/local storage
   */
  getFunctionName(): string {
    return (
      sessionStorage.getItem('functionName') ||
      localStorage.getItem('currentFunctionName') ||
      ''
    );
  }

  /**
   * Get stored user ID from session storage
   */
  getStoredUserId(): string {
    return sessionStorage.getItem('userId') || localStorage.getItem('userId') || '';
  }

  /**
   * Get stored app ID from session storage
   */
  getStoredAppId(): string {
    return sessionStorage.getItem('appId') || '';
  }

}
