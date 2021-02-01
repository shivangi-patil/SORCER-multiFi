package edu.pjatk.inn.coffeemaker.impl;

import edu.pjatk.inn.coffeemaker.Pay;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

public abstract class Payment implements Pay {
    public Context payByCard(Context context) throws RemoteException, ContextException{
        return context;
    }

    public Context payByCash(Context context) throws RemoteException, ContextException{
        return context;
    }
}
