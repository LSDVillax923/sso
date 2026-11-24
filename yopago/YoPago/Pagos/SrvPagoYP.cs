using YoPago.Compras;
using YoPago.Datos;

namespace YoPago.Pagos;

public class SrvPagoYP
{
    private readonly IntegraBDPagoYP integraBDPago;
    private readonly IntegraBDCompraPendiente integraBDCompra;
    private readonly PublicadorPagoSSO publicador;

    public SrvPagoYP(IntegraBDPagoYP integraBDPago, IntegraBDCompraPendiente integraBDCompra, PublicadorPagoSSO publicador)
    {
        this.integraBDPago = integraBDPago;
        this.integraBDCompra = integraBDCompra;
        this.publicador = publicador;
    }

    /** Cobra el valor que registró SSO y publica la confirmación. Null si la compra no está pendiente o no es del cliente. */
    public ComprobanteYPDTO? Pagar(PagoYPDTO dto, string cedula)
    {
        var compra = integraBDCompra.BuscarPorNumero(dto.NumeroCompra);
        if (compra == null || compra.Cedula != cedula || compra.Estado != "PENDIENTE") return null;

        var pago = new PagoYP
        {
            NumeroCompra = compra.NumeroCompra,
            Cedula = cedula,
            ValorPagado = compra.Valor,
            Fecha = DateTime.Now,
            NotificadoSSO = false
        };
        integraBDPago.Agregar(pago);
        integraBDCompra.MarcarPagada(compra.NumeroCompra);
        Publicar(pago);
        return new ComprobanteYPDTO(pago.IdPago, pago.NumeroCompra, pago.ValorPagado, pago.Fecha);
    }

    /** Respuesta a pago.consulta: si la compra ya se pagó, se vuelve a publicar pago.confirmado. */
    public void ReenviarSiPagado(long numeroCompra)
    {
        var pago = integraBDPago.BuscarPorNumeroCompra(numeroCompra);
        if (pago != null) Publicar(pago);
    }

    public void Publicar(PagoYP pago)
    {
        if (publicador.PublicarConfirmado(new ConfirmacionPagoYPDTO(pago.Cedula, pago.NumeroCompra, pago.ValorPagado)))
        {
            integraBDPago.MarcarNotificado(pago.IdPago);
        }
    }
}
