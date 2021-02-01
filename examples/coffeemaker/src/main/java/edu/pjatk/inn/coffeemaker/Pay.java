package edu.pjatk.inn.coffeemaker;
import sorcer.service.Context;
import sorcer.service.ContextException;
import java.rmi.RemoteException;
public interface Pay {
    Context payByCard(Context context) throws ContextException, RemoteException;

    Context payByCash(Context context) throws ContextException, RemoteException;
}
