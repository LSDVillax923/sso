using System.Text.Json;
using RabbitMQ.Client;
using YoPago.Datos;

namespace YoPago.Pagos;

/** Lado .NET del tópico integracion.pagos: avisa al monolito SSO que el cliente pagó. */
public class PublicadorPagoSSO
{
    private readonly ConfigRabbitYP rabbit;
    private readonly ILogger<PublicadorPagoSSO> log;

    public PublicadorPagoSSO(ConfigRabbitYP rabbit, ILogger<PublicadorPagoSSO> log)
    {
        this.rabbit = rabbit;
        this.log = log;
    }

    public bool PublicarConfirmado(ConfirmacionPagoYPDTO dto)
    {
        try
        {
            using var canal = rabbit.Conectar().CreateModel();
            ConfigRabbitYP.DeclararTopologia(canal);
            var propiedades = canal.CreateBasicProperties();
            propiedades.ContentType = "application/json";
            propiedades.Persistent = true;
            canal.BasicPublish(ConfigRabbitYP.Exchange, ConfigRabbitYP.ClaveConfirmado, propiedades,
                JsonSerializer.SerializeToUtf8Bytes(dto, ConfigRabbitYP.Json));
            return true;
        }
        catch (Exception e)
        {
            log.LogWarning("No se pudo publicar el pago de la compra {Numero}: {Error}", dto.NumeroCompra, e.Message);
            return false;
        }
    }
}
