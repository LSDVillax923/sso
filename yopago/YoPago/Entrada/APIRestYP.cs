namespace YoPago.Entrada;

/** Boundary de entrada del monolito .NET: recibe las peticiones de YoPago.Web (PC2), las marca y las entrega a los WS (controladores). */
public class APIRestYP
{
    private readonly RequestDelegate siguiente;
    private readonly ILogger<APIRestYP> log;

    public APIRestYP(RequestDelegate siguiente, ILogger<APIRestYP> log)
    {
        this.siguiente = siguiente;
        this.log = log;
    }

    public async Task InvokeAsync(HttpContext contexto)
    {
        contexto.Response.Headers["X-Atendido-Por"] = "YoPago.dll";
        log.LogInformation("{Metodo} {Ruta} desde {Origen}", contexto.Request.Method, contexto.Request.Path, contexto.Connection.RemoteIpAddress);
        await siguiente(contexto);
    }
}
