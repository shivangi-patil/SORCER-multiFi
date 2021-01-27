package edu.pjatk.inn.coffeemaker.impl;

import sorcer.core.context.ServiceContext;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * The class Recipe represents a generic recipe.
 * @author   Sarah & Mike
 */
public class Recipe implements Serializable {
	/**
	 * Name of recipe.
	 */
	private String name;
	/**
	 * Price of recipe.
	 */
	private int price;
	/**
	 * Amount of coffee in the recipe.
	 */
	private int amtCoffee;
	/**
	 * Amount of milk in the recipe.
	 */
	private int amtMilk;
	/**
	 * Amount of sugar in the recipe.
	 */
	private int amtSugar;
	/**
	 * Amount of chocolate in the recipe.
	 */
    private int amtChocolate;
	
	/**
	 * Constructor for the recipe.
	 * 
	 */ 
    public Recipe() {
    	this.name = "";
    	this.price = 0;
    	this.amtCoffee = 0;
    	this.amtMilk = 0;
    	this.amtSugar = 0;
    	this.amtChocolate = 0;
    }
    
    /**
	 * Get the recipes amount of chocolate.
	 * @return   Returns the amtChocolate.
	 */
    public int getAmtChocolate() {
		return amtChocolate;
	}
    /**
	 * Amount chocolate setter method.
	 * @param amtChocolate   The amtChocolate to setValue.
	 */
    public void setAmtChocolate(int amtChocolate) {
		if (amtChocolate >= 0) {
			this.amtChocolate = amtChocolate;
		} 
	}
    /**
	 * Get the recipes amount of coffee.
	 * @return   Returns the amtCoffee.
	 */
    public int getAmtCoffee() {
		return amtCoffee;
	}
    /**
	 * Amount coffee setter method.
	 * @param amtCoffee   The amtCoffee to setValue.
	 */
    public void setAmtCoffee(int amtCoffee) {
		if (amtCoffee >= 0) {
			this.amtCoffee = amtCoffee;
		} 
	}
    /**
	 * Get the recipes amount of milk.
	 * @return   Returns the amtMilk.
	 */
    public int getAmtMilk() {
		return amtMilk;
	}
    /**
	 * Amount milk setter method.
	 * @param amtMilk   The amtMilk to setValue.
	 */
    public void setAmtMilk(int amtMilk) {
		if (amtMilk >= 0) {
			this.amtMilk = amtMilk;
		} 
	}
    /**
	 * Get the recipes amount of sugar.
	 * @return   Returns the amtSugar.
	 */
    public int getAmtSugar() {
		return amtSugar;
	}
    /**
	 * Amount sugar setter method.
	 * @param amtSugar   The amtSugar to setValue.
	 */
    public void setAmtSugar(int amtSugar) {
		if (amtSugar >= 0) {
			this.amtSugar = amtSugar;
		} 
	}
    /**
	 * Get the recipes name.
	 * @return   Returns the key.
	 */
    public String getName() {
		return name;
	}
    /**
	 * Name setter method.
	 * @param name   The key to setValue.
	 */
    public void setName(String name) {
    	if(name != null) {
    		this.name = name;
    	}
	}
    /**
	 * Get the recipes price.
	 * @return   Returns the price.
	 */
    public int getPrice() {
		return price;
	}
    /**
	 * Price setter method
	 * @param price   The price to setValue.
	 */
    public void setPrice(int price) {
		if (price >= 0) {
			this.price = price;
		} 
	} 
	/**
	 * Returns true if a recipe is already added.
	 * @param r   Recipe to be added
	 * @return boolean
	 */
    public boolean equals(Recipe r) {
        if((this.name).equals(r.getName())) {
            return true;
        }
        return false;
	}
	
	/**
	 * @return String  The name of the recipe
	 */
    public String toString() {
    	return name;
    }

	/**
     * Returns the new recipe
     * @param context
     * @return Recipe
	 * @throws ContextException  If an exception occurred
     */
	static public Recipe getRecipe(Context context) throws ContextException {
		Recipe r = new Recipe();
		try {
			r.name = (String)context.getValue("key");
			r.price = (int)context.getValue("price");
			r.amtCoffee = (int)context.getValue("amtCoffee");
			r.amtMilk = (int)context.getValue("amtMilk");
			r.amtSugar = (int)context.getValue("amtSugar");
			r.amtChocolate = (int)context.getValue("amtChocolate");
		} catch (RemoteException e) {
			throw new ContextException(e);
		}
		return r;
	}

	/**
     * Returns the service context of the recipe
     * @param recipe
     * @return Context
	 * @throws ContextException  If an exception occurred
     */
	static public Context getContext(Recipe recipe) throws ContextException {
		Context cxt = new ServiceContext();
		cxt.putValue("key", recipe.getName());
		cxt.putValue("price", recipe.getPrice());
		cxt.putValue("amtCoffee", recipe.getAmtCoffee());
		cxt.putValue("amtMilk", recipe.getAmtMilk());
		cxt.putValue("amtSugar", recipe.getAmtSugar());
		cxt.putValue("amtChocolate", recipe.getAmtChocolate());
		return cxt;
	}
}