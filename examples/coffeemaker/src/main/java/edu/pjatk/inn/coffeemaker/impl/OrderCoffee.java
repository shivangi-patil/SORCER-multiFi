package edu.pjatk.inn.coffeemaker.impl;
import edu.pjatk.inn.coffeemaker.Order;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

public class OrderCoffee implements Order {
    private Recipe [] recipeArray;
    private final int NUM_RECIPES = 3;


    public Recipe getRecipeForName(String name) {
        Recipe r = null;
        for(int i = 0; i < NUM_RECIPES; i++) {
            if(recipeArray[i].getName() != null) {
                if((recipeArray[i].getName()).equals(name)) {
                    r = recipeArray[i];
                }
            }
        }
        return r;
    }


    @Override
    public Context addRecipe(Context context) throws RemoteException, ContextException {
        Recipe r = Recipe.getRecipe(context);
        return context;
    }
    @Override
    public Context makeOrder(Context context) throws ContextException, RemoteException{
        String recipeName = (String)context.getValue("recipe/key");
        Context recipeContext = (Context)context.getValue("recipe");
        if (recipeContext != null)
            addRecipe(recipeContext);
        Recipe r = getRecipeForName(recipeName);
        context.putValue("coffee/price", r.getPrice());

        if (context.getContextReturn() != null) {
            context.setReturnValue(r.getPrice());
        }
        return context;

    }
}
