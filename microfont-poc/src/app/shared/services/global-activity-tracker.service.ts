import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class GlobalActivityTrackerService {
  private renderer: Renderer2;
  private trackingEnabled = true;
  private previousRoute: string | null = null;
  private previousFunctionId: string | null = null;
  private currentRoute: string | null = null;
  private initialized = false;
  private readonly BRANCH_ID = 'BR001';  // Dummy
  private readonly ORG_ID = 'ORG001';    // Dummy

  // Inactivity timeout (default fallback: 5 minutes)
  private readonly DEFAULT_INACTIVITY_TIMEOUT_MS = 5 * 60 * 1000;
  private inactivityTimer: any;
  private inactivitySubject = new Subject<void>();
  public inactivity$ = this.inactivitySubject.asObservable();

  constructor(private router: Router, rendererFactory: RendererFactory2) {
    this.renderer = rendererFactory.createRenderer(null, null);
    this.init();
  }

  /** Enable or disable tracking manually */
  enable() { this.trackingEnabled = true; }
  disable() { this.trackingEnabled = false; }

  /** Centralized init guard (avoid reinit on hot reloads) */
  private init() {
    if (this.initialized) return;
    this.initialized = true;

    this.initRouterTracking();
    this.initClickTracking();
    this.initFormFieldTracking();
  }

  /** Retrieve currently active functionId from localStorage */
  private getCurrentFunctionId(): string | null {
    return localStorage.getItem('currentFunctionId');
  }

  /** Extract inactivity timeout from token's screenLockTime field */
  private getInactivityTimeout(): number {
    try {
      const accessToken = sessionStorage.getItem('access_token');
      if (!accessToken) {
        return this.DEFAULT_INACTIVITY_TIMEOUT_MS;
      }

      // Decode JWT payload (base64 decode the middle part)
      const parts = accessToken.split('.');
      if (parts.length !== 3) {
        return this.DEFAULT_INACTIVITY_TIMEOUT_MS;
      }

      const payload = JSON.parse(atob(parts[1]));
      const screenLockTime = payload.screenLockTime;

      if (!screenLockTime) {
        return this.DEFAULT_INACTIVITY_TIMEOUT_MS;
      }

      // Parse "5min", "10min", etc. to milliseconds
      const match = screenLockTime.match(/(\d+)(\w+)/);
      if (!match) {
        return this.DEFAULT_INACTIVITY_TIMEOUT_MS;
      }

      const value = parseInt(match[1], 10);
      const unit = match[2].toLowerCase();

      switch (unit) {
        case 'min':
          return value * 60 * 1000;
        case 'sec':
          return value * 1000;
        case 'hour':
        case 'h':
          return value * 60 * 60 * 1000;
        default:
          return this.DEFAULT_INACTIVITY_TIMEOUT_MS;
      }
    } catch (error) {
      console.error('Error parsing screenLockTime from token:', error);
      return this.DEFAULT_INACTIVITY_TIMEOUT_MS;
    }
  }

  /** Reset inactivity timer on user activity */
  private resetInactivityTimer() {
    if (this.inactivityTimer) {
      clearTimeout(this.inactivityTimer);
    }

    const timeoutMs = this.getInactivityTimeout();
    this.inactivityTimer = setTimeout(() => {
      console.warn(`⏱️ Inactivity timeout (${timeoutMs}ms) reached - user will be logged out`);
      this.inactivitySubject.next();
    }, timeoutMs);
  }

  //  PAGE NAVIGATION TRACKING
  private initRouterTracking() {
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        if (!this.trackingEnabled) return;

        this.resetInactivityTimer();

        const newRoute = event.urlAfterRedirects;

        const payload = {
          eventType: 'PAGE_NAVIGATION',
          previousRoute: this.previousRoute,
          previousFunctionId: this.previousFunctionId,
          currentRoute: newRoute,
          functionId: this.getCurrentFunctionId(),
          branchId: this.BRANCH_ID,
          orgId: this.ORG_ID,
          timestamp: new Date(),
        };

        console.log('🧾 Activity:', payload);
        this.sendToBackend(payload);

        this.previousRoute = this.currentRoute;
        this.previousFunctionId = this.getCurrentFunctionId();
        this.currentRoute = newRoute;
      });
  }

  // 🖱️ BUTTON CLICK TRACKING
  private initClickTracking() {
    this.renderer.listen('document', 'click', (event: MouseEvent) => {
      if (!this.trackingEnabled) return;

      this.resetInactivityTimer();

      const target = event.target as HTMLElement;

      if (!target) return;

      // Ignore routerLink buttons
      if (target.closest('a[routerLink], button[routerLink]')) return;

      // Detect button click (direct or inside button)
      const buttonEl = target.closest('button');
      if (buttonEl) {
        const label = this.getButtonLabel(buttonEl);
        const payload = {
          eventType: 'BUTTON_CLICK',
          currentRoute: this.currentRoute,
          functionId: this.getCurrentFunctionId(),
          label: label,
          id: buttonEl.id,
          cls: buttonEl.className,
          branchId: this.BRANCH_ID,
          orgId: this.ORG_ID,
          timestamp: new Date(),
        };

        console.log('🧾 Activity:', payload);
        this.sendToBackend(payload);
      }
    });
  }

  private getButtonLabel(buttonEl: HTMLElement): string {
    const text = buttonEl.textContent?.trim();
    if (text) return text;

    const ariaLabel = buttonEl.getAttribute('aria-label');
    if (ariaLabel) return ariaLabel;

    const tooltip = buttonEl.getAttribute('mattooltip') ?? buttonEl.getAttribute('matTooltip');
    if (tooltip) return tooltip;

    if (buttonEl.querySelector('svg')) return 'ICON_SVG';
    const matIcon = buttonEl.querySelector('mat-icon');
    if (matIcon) return matIcon.textContent?.trim() ?? 'ICON_MAT';

    return 'UNKNOWN';
  }

  // 🧾 FORM FIELD TRACKING
  private initFormFieldTracking() {
    // Blur event → input/textarea/select loses focus
    this.renderer.listen('document', 'blur', (event: FocusEvent) => {
      if (!this.trackingEnabled) return;

      this.resetInactivityTimer();

      const target = event.target as HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement;

      if (!target) return;

      const tag = target.tagName.toUpperCase();
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') {
        const payload = {
          eventType: 'FIELD_INTERACTION',
          currentRoute: this.currentRoute,
          functionId: this.getCurrentFunctionId(),
          tag,
          id: target.id || target.getAttribute('formControlName') || '',
          cls: target.className,
          branchId: this.BRANCH_ID,
          orgId: this.ORG_ID,
          value: (target as any).value,
          timestamp: new Date(),
        };

        console.log('🧾 Activity:', payload);
        this.sendToBackend(payload);
      }
    }, { capture: true });

    // Select change → capture option selection
    this.renderer.listen('document', 'change', (event: Event) => {
      if (!this.trackingEnabled) return;

      this.resetInactivityTimer();

      const target = event.target as HTMLSelectElement | HTMLInputElement;

      if (!target) return;

      const tag = target.tagName.toUpperCase();
      if (tag === 'SELECT') {
        const payload = {
          eventType: 'SELECT_CHANGE',
          currentRoute: this.currentRoute,
          functionId: this.getCurrentFunctionId(),
          id: target.id || target.getAttribute('formControlName') || '',
          cls: target.className,
          value: target.value,
          branchId: this.BRANCH_ID,
          orgId: this.ORG_ID,
          timestamp: new Date(),
        };

        console.log('🧾 Activity:', payload);
        this.sendToBackend(payload);
      }
    });
  }

  // 🌐 Send payload to backend (use HttpClient later if needed)
  private sendToBackend(payload: any) {
    // if (!this.trackingEnabled) return;

    // fetch('http://localhost:8085/api/activity/save', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(payload),
    // }).catch(err => console.error('Tracking error:', err));
  }
}
