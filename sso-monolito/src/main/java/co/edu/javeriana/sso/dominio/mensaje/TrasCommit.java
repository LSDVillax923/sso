package co.edu.javeriana.sso.dominio.mensaje;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Publica un mensaje solo cuando la transacción hizo commit, para que el consumidor ya encuentre la compra en la BD. */
public final class TrasCommit {

    private TrasCommit() {
    }

    public static void ejecutar(Runnable accion) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    accion.run();
                }
            });
        } else {
            accion.run();
        }
    }
}
