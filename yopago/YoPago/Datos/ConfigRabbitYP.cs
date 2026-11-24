using System.Text.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace YoPago.Datos;

/** Conexión al RabbitMQ de PC2 y nombres del tópico integracion.pagos (los mismos que declara sso-monolito). */
public sealed class ConfigRabbitYP : IDisposable
{
    public const string Exchange = "integracion.pagos";
    public const string ColaPendientes = "yopago.pendientes";
    public const string ColaConsultas = "yopago.consultas";
    public const string ColaConfirmados = "sso.pagos.confirmados";
    public const string ClavePendiente = "pago.pendiente";
    public const string ClaveConsulta = "pago.consulta";
    public const string ClaveConfirmado = "pago.confirmado";

    public static readonly JsonSerializerOptions Json = new(JsonSerializerDefaults.Web);

    private readonly ConnectionFactory fabrica;
    private readonly object candado = new();
    private IConnection? conexion;

    public ConfigRabbitYP(IConfiguration cfg)
    {
        fabrica = new ConnectionFactory
        {
            HostName = cfg["Rabbit:Host"] ?? "localhost",
            UserName = cfg["Rabbit:Usuario"] ?? "guest",
            Password = cfg["Rabbit:Clave"] ?? "guest",
            AutomaticRecoveryEnabled = true
        };
    }

    public IConnection Conectar()
    {
        lock (candado)
        {
            if (conexion == null || !conexion.IsOpen) conexion = fabrica.CreateConnection("YoPago");
            return conexion;
        }
    }

    /** Colas durables sin argumentos, igual que Colas.declararTopicoPagos() en Java, para que no choquen. */
    public static void DeclararTopologia(IModel canal)
    {
        canal.ExchangeDeclare(Exchange, ExchangeType.Topic, durable: true, autoDelete: false);
        foreach (var (cola, clave) in new[] { (ColaPendientes, ClavePendiente), (ColaConsultas, ClaveConsulta), (ColaConfirmados, ClaveConfirmado) })
        {
            canal.QueueDeclare(cola, durable: true, exclusive: false, autoDelete: false, arguments: null);
            canal.QueueBind(cola, Exchange, clave);
        }
    }

    /** Consume una cola del tópico hasta que se detenga la aplicación. Si RabbitMQ no está, reintenta cada 10 s. */
    public async Task ConsumirAsync<T>(string cola, Action<T> manejar, ILogger log, CancellationToken ct)
    {
        while (!ct.IsCancellationRequested)
        {
            try
            {
                var canal = Conectar().CreateModel();
                DeclararTopologia(canal);
                canal.BasicQos(0, 10, false);
                var consumidor = new EventingBasicConsumer(canal);
                consumidor.Received += (_, ea) =>
                {
                    try
                    {
                        var dto = JsonSerializer.Deserialize<T>(ea.Body.Span, Json);
                        if (dto != null) manejar(dto);
                        canal.BasicAck(ea.DeliveryTag, false);
                    }
                    catch (Exception e)
                    {
                        log.LogError(e, "No se pudo procesar el mensaje de la cola {Cola}", cola);
                        canal.BasicNack(ea.DeliveryTag, false, false);
                    }
                };
                canal.BasicConsume(queue: cola, autoAck: false, consumer: consumidor);
                await Task.Delay(Timeout.Infinite, ct);
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception e)
            {
                log.LogWarning("RabbitMQ no disponible para {Cola}, se reintenta en 10 s: {Error}", cola, e.Message);
                await Task.Delay(TimeSpan.FromSeconds(10), ct);
            }
        }
    }

    public void Dispose() => conexion?.Dispose();
}
