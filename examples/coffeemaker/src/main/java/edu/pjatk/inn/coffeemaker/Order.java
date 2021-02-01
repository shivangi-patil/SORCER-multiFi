package edu.pjatk.inn.coffeemaker;

import sorcer.service.Context;
import sorcer.service.ContextException;
import java.rmi.RemoteException;
public interface Order {
    Context addRecipe(Context context) throws RemoteException, ContextException;
    Context makeOrder(Context context) throws ContextException, RemoteException;
}
