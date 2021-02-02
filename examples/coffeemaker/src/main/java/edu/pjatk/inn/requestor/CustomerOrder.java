package edu.pjatk.inn.requestor;
import edu.pjatk.inn.coffeemaker.CoffeeService;
import edu.pjatk.inn.coffeemaker.impl.OrderConfirmation;
import edu.pjatk.inn.coffeemaker.impl.Payment;
import sorcer.service.*;

import static sorcer.co.operator.*;
import static sorcer.eo.operator.*;
import static sorcer.eo.operator.context;
public class CustomerOrder {
    private Mogram order() throws Exception{

        /**
         *
         * @author Shivangi Patil
         *
         * Customer Makes order and pays for it
         */
    Context espresso = context(val("key", "espresso"), val("price", 50),
            val("amtCoffee", 8), val("amtMilk", 1),
            val("amtSugar", 1), val("amtChocolate", 2));


    Task order = task("order", sig("makeOrder", OrderConfirmation.class), context(
            val("recipe/key", "mocha"),
            val("order/quantity", 3),
            val("order/price"),
            val("recipe", "espresso")));

    Task payment = task("payment", sig("payByCard", Payment.class), context(val("/payByCard", "1234"), val("/price")));

    Task makeCoffee = task("makeCoffee", sig("makeCoffee", CoffeeService.class), context(val("/paid", 50), val("/status")));

    Job orderCoffee = job(order, payment,
            pipe(outPoint(order, "order/price"), inPoint(payment, "payment/price")),
            pipe(outPoint(payment, "payment/status"), inPoint(makeCoffee, "coffee/status")));

        return orderCoffee;
}
}
