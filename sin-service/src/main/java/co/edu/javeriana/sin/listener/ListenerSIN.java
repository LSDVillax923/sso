package co.edu.javeriana.sin.listener;

import co.edu.javeriana.sin.config.ConfigRabbitSIN;
import co.edu.javeriana.sin.dto.DTOVentaSIN;
import co.edu.javeriana.sin.srb.SRBAlmacenarVenta;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerSIN {

    private final SRBAlmacenarVenta srbAlmacenarVenta;

    public ListenerSIN(SRBAlmacenarVenta srbAlmacenarVenta) {
        this.srbAlmacenarVenta = srbAlmacenarVenta;
    }

    @RabbitListener(queues = ConfigRabbitSIN.SIN_VENTAS)
    public void recibir(DTOVentaSIN dto) {
        srbAlmacenarVenta.almacenarVenta(dto);
    }
}
