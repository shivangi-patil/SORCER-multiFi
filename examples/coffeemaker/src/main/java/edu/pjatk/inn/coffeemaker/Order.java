package edu.pjatk.inn.coffeemaker;

import sorcer.service.Context;
import sorcer.service.ContextException;
import java.rmi.RemoteException;
public interface Order {
    Context makeOrder(Context context) throws RemoteException, ContextException;
    Context pay(Context context) throws ContextException, RemoteException;
}
