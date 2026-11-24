using YoPago.Datos;

namespace YoPago.Compras;

/** Disparador asíncrono: consume la cola yopago.pendientes del tópico integracion.pagos. */
public class ListenerComprasSSO : BackgroundService
{
    private readonly ConfigRabbitYP rabbit;
    private readonly IServiceScopeFactory scopes;
    private readonly ILogger<ListenerComprasSSO> log;

    public ListenerComprasSSO(ConfigRabbitYP rabbit, IServiceScopeFactory scopes, ILogger<ListenerComprasSSO> log)
    {
        this.rabbit = rabbit;
        this.scopes = scopes;
        this.log = log;
    }

    protected override Task ExecuteAsync(CancellationToken ct) =>
        rabbit.ConsumirAsync<CompraPendienteYPDTO>(ConfigRabbitYP.ColaPendientes, Recibir, log, ct);

    public void Recibir(CompraPendienteYPDTO dto)
    {
        using var scope = scopes.CreateScope();
        scope.ServiceProvider.GetRequiredService<SrvComprasPendientes>().Registrar(dto);
    }
}
