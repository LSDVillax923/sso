namespace YoPago.Pagos;

/** Disparador asíncrono: cada 2 minutos publica los pagos que no se pudieron publicar (RabbitMQ caído). */
public class TimerReintentoNotificacion : BackgroundService
{
    private readonly IServiceScopeFactory scopes;
    private readonly ILogger<TimerReintentoNotificacion> log;
    private readonly TimeSpan intervalo = TimeSpan.FromMinutes(2);

    public TimerReintentoNotificacion(IServiceScopeFactory scopes, ILogger<TimerReintentoNotificacion> log)
    {
        this.scopes = scopes;
        this.log = log;
    }

    protected override async Task ExecuteAsync(CancellationToken ct)
    {
        using var timer = new PeriodicTimer(intervalo);
        while (await timer.WaitForNextTickAsync(ct))
        {
            try
            {
                using var scope = scopes.CreateScope();
                var integraBDPago = scope.ServiceProvider.GetRequiredService<IntegraBDPagoYP>();
                var srvPago = scope.ServiceProvider.GetRequiredService<SrvPagoYP>();
                foreach (var pago in integraBDPago.BuscarNoNotificados()) srvPago.Publicar(pago);
            }
            catch (Exception e)
            {
                log.LogError(e, "Falló el reintento de publicación de pagos");
            }
        }
    }
}
