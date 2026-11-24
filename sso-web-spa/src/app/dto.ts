// Mismos DTO que expone sso-monolito (records de Java), vistos desde el navegador.

export interface LoginDTO {
  usuario: string;
  clave: string;
}

export interface TokenDTO {
  token: string;
  cedula: string;
  nombre: string;
}

export interface BienDTO {
  idSubasta: number;
  idBien: number;
  nombre: string;
  descripcion: string;
  valorMinimo: number;
  fechaCierre: string;
}

export interface ItemCarritoDTO {
  idItem: number;
  idSubasta: number;
  nombreBien: string;
  valorMinimo: number;
  fechaCierre: string;
}

export interface ItemOfertaDTO {
  idSubasta: number;
  valor: number;
}

export interface PeticionOfertaDTO {
  items: ItemOfertaDTO[];
}

export interface RespuestaOfertaDTO {
  mensaje: string;
  aceptadas: number[];
  rechazadas: Record<string, string>;
}

export type EstadoOferta = 'REGISTRADA' | 'RECHAZADA' | 'GANADORA' | 'PERDEDORA';

export type EstadoCompra = 'VERIFICANDO_OFAC' | 'RECHAZADA_OFAC' | 'APROBADA_OFAC' | 'PENDIENTE_PAGO'
  | 'PAGADA' | 'DISTRIBUIDA' | 'TERMINADA';

export interface OfertaDTO {
  idOferta: number;
  idSubasta: number;
  nombreBien: string;
  valor: number;
  estado: EstadoOferta;
}

export interface PeticionCompraDTO {
  idOferta: number;
}

export interface RespuestaCompraDTO {
  numeroCompra: number;
  estado: EstadoCompra;
  mensaje: string;
}

export interface CompraDTO {
  numeroCompra: number;
  nombreBien: string;
  valorOferta: number;
  impuestos: number;
  valorTotal: number;
  estado: EstadoCompra;
}

/** El monolito responde los errores de negocio como {"mensaje": "..."} (ManejadorErrores). */
export function mensajeDeError(error: unknown, porDefecto: string): string {
  const e = error as { status?: number; error?: { mensaje?: string } };
  if (e?.error?.mensaje) return e.error.mensaje;
  if (e?.status === 0) return 'No hay conexión con el SSO, intente más tarde';
  if (e?.status === 401) return 'Su sesión venció, ingrese de nuevo';
  if ((e?.status ?? 0) >= 500) return 'El SSO no está disponible en este momento, intente más tarde';
  return porDefecto;
}
