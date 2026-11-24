using YoPago.Datos;

namespace YoPago.Pagos;

/** Disparador asíncrono: consume la cola yopago.consultas del tópico integracion.pagos. */
public class ListenerConsultaPago : BackgroundService
{
    private readonly ConfigRabbitYP rabbit;
    private readonly IServiceScopeFactory scopes;
    private readonly ILogger<ListenerConsultaPago> log;

    public ListenerConsultaPago(ConfigRabbitYP rabbit, IServiceScopeFactory scopes, ILogger<ListenerConsultaPago> log)
    {
        this.rabbit = rabbit;
        this.scopes = scopes;
        this.log = log;
    }

    protected override Task ExecuteAsync(CancellationToken ct) =>
        rabbit.ConsumirAsync<ConsultaPagoYPDTO>(ConfigRabbitYP.ColaConsultas, Recibir, log, ct);

    public void Recibir(ConsultaPagoYPDTO dto)
    {
        using var scope = scopes.CreateScope();
        scope.ServiceProvider.GetRequiredService<SrvPagoYP>().ReenviarSiPagado(dto.NumeroCompra);
    }
}
