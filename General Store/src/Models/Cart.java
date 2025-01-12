package Models;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to contain information about the products bought by the customer.
 */

public class Cart {

    private int idCartCust;
    private int idCartProd;
    private int numProduct;
    
    
    /**
     * Constructor class carts
     * @param idCartCust The unique identifier for the customer's cart.
     * @param idCartProd The unique identifier for the product in the cart.
     * @param numProduct The number of units of the product in the cart.
     */
    public Cart(int idCartCust, int idCartProd, int numProduct) {
        super();
        this.idCartCust = idCartCust;
        this.idCartProd = idCartProd;
        this.numProduct = numProduct;
    }
    
    /**
     * Constructor class carts
     * @param idCartProd The unique identifier for the product in the cart.
     * @param numProduct The number of units of the product in the cart.
     */
    public Cart(int idCartProd, int numProduct) {
        super();
        this.idCartProd = idCartProd;
        this.numProduct = numProduct;
    }
    
    /**
     * Get the id number of the cart
     * @return The unique identifier for the customer's cart.
     */
    public int getIdCartCust() {
        return idCartCust;
    }


    /**
     * Get the id number of the product
     * @return The unique identifier for the product in the cart.
     */
    public int getIdCartProd() {
        return idCartProd;
    }

    /**
     * Get the number of the products sold out
     * @return The quantity of the product in the cart.
     */
    public int getNumProduct() {
        return numProduct;
    }

    
    /**
     * Set the id number of the cart
     * @param idCartCust The unique identifier to set for the customer's cart.
     */
    public void setIdCartCust(int idCartCust) {
        this.idCartCust = idCartCust;
    }
    
    /**
     * Set the id of the cart containing the product
     * @param idCartProd The unique identifier to set for the product in the cart.
     */
    public void setIdCartProd(int idCartProd) {
        this.idCartProd = idCartProd;
    }

    /**
     * Set the number of the products in the cart
     * @param numProduct The number of product units to set for the cart.
     */
    public void setNumProduct(int numProduct) {
        this.numProduct = numProduct;
    }

    
    /**
     * Method for calculating the remaining quantity of the product in stock after being added to the cart.
     * @param quantityTot The total quantity available for the product in stock.
     * @param qntyClient The quantity of the product being added to the cart by the customer.
     * @return The remaining quantity of the product in stock after the purchase.
     */
    public Integer fillCart(int quantityTot, int qntyClient) {
        int quantityVar = 0;
        if ((quantityTot > 0 && qntyClient > 0) && qntyClient <= quantityTot) {
            quantityVar = quantityTot - qntyClient;
        } else {
            quantityVar = 0;
            System.out.println("Quantity value is not valid!");
        }
        return quantityVar;
    }
}
