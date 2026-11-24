using YoPago.Datos;

namespace YoPago.Compras;

/** Disparador asíncrono: consume la cola yopago.pendientes del tópico integracion.pagos. */
public class LisenerComprasSSO : BackgroundService
{
    private readonly ConfigRabbitYP rabbit;
    private readonly IServiceScopeFactory scopes;
    private readonly ILogger<LisenerComprasSSO> log;

    public LisenerComprasSSO(ConfigRabbitYP rabbit, IServiceScopeFactory scopes, ILogger<LisenerComprasSSO> log)
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
