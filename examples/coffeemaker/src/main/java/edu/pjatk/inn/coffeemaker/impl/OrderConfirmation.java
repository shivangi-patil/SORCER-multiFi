package edu.pjatk.inn.coffeemaker.impl;


import edu.pjatk.inn.coffeemaker.Order;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

/**
 *
 * @author Shivangi Patil
 *
 * Display the order name on the screen , if order is correct
 */
public class OrderConfirmation implements Order {

    @Override
    public Context makeOrder(Context context) throws RemoteException, ContextException {
        // from/to any place the flat rate $0.60
        String coffeeName = "espresso";

        if (context.getContextReturn() != null) {
            context.setReturnValue(coffeeName);
        }
        return context;
    }

    @Override
    public Context pay(Context context) throws RemoteException, ContextException {
        Integer amtPaid = 60;
        context.putValue("coffee/cost", amtPaid);

        if (context.getContextReturn() != null) {
            context.setReturnValue(amtPaid);
        }
        return context;
    }

    //customer can choose coffee by Name or serial no.
   /* private String coffeeName;

    public OrderConfirmation(String coffeeName, int serialNo) {
        this.coffeeName = coffeeName;

        public String getCoffeeName () {
            return coffeeName;
        }
        public void setCoffeeName () {
            if (!coffeeName.equals("")) {
                this.coffeeName = coffeeName;
            }
        }
    }

        public void coffeeName (OrderConfirmation coffeeName){
            if (this.coffeeName.equals(coffeeName.getCoffeeName())) {
                System.out.println("Coffee Ordered" + coffeeName);

            }
        } */
}
