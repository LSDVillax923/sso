using System.Net.Http.Headers;
using YoPago.Web.Models;

namespace YoPago.Web.Proxy;

/** Cliente HTTP hacia el monolito YoPago.dll (PC1). Aquí no hay reglas de negocio. */
public class ProxyYoPagoAPI
{
    private readonly HttpClient http;

    public ProxyYoPagoAPI(HttpClient http)
    {
        this.http = http;
    }

    public async Task<string?> LoginAsync(LoginVM vm)
    {
        var respuesta = await http.PostAsJsonAsync("api/auth", new { cedula = vm.Cedula, contrasena = vm.Contrasena });
        return respuesta.IsSuccessStatusCode ? await respuesta.Content.ReadAsStringAsync() : null;
    }

    public async Task<List<CompraPendienteVM>> ObtenerPendientesAsync(string token)
    {
        using var peticion = new HttpRequestMessage(HttpMethod.Get, "api/compras-pendientes");
        peticion.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        using var respuesta = await http.SendAsync(peticion);
        respuesta.EnsureSuccessStatusCode();
        return await respuesta.Content.ReadFromJsonAsync<List<CompraPendienteVM>>() ?? new List<CompraPendienteVM>();
    }

    public async Task<ComprobanteVM?> PagarAsync(PagoVM vm, string token)
    {
        using var peticion = new HttpRequestMessage(HttpMethod.Post, "api/pagos")
        {
            Content = JsonContent.Create(new { numeroCompra = vm.NumeroCompra })
        };
        peticion.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        using var respuesta = await http.SendAsync(peticion);
        return respuesta.IsSuccessStatusCode ? await respuesta.Content.ReadFromJsonAsync<ComprobanteVM>() : null;
    }
}
