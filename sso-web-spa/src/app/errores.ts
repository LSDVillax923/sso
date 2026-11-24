import { Router } from '@angular/router';

/** Si el API respondió 401 (token vencido o ausente) se cierra la sesión local y se vuelve a PanLogin. */
export function sesionVencida(error: unknown, router: Router): boolean {
  if ((error as { status?: number })?.status !== 401) return false;
  sessionStorage.clear();
  router.navigate(['/login']);
  return true;
}
