package Models;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This class is used to contain information about the products.
*/
public class Product {

    private int idProduct;
    private String name;
    private double price;
    private int quantity;
    private String date;

    /**
     * Constructor for creating a product with all details.
     * @param idProduct The unique identifier of the product.
     * @param name The name of the product.
     * @param price The price of the product.
     * @param quantity The quantity of the product in stock.
    */
    public Product(int idProduct, String name, double price, int quantity) {
        this.idProduct = idProduct;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Constructor for creating a product with name, price, and quantity.
     * @param name The name of the product.
     * @param price The price of the product.
     * @param quantity The quantity of the product in stock.
    */
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Constructor for creating a product with name and date.
     * @param name The name of the product.
     * @param date The date associated with the product (e.g., manufacture date or update date).
    */
    public Product(String name, String date) {
        this.name = name;
        this.date = date;
    }

    /**
     * Constructor for creating a product with only the name.
     * @param name The name of the product.
    */
    public Product(String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier of the product.
     * @return The product's unique identifier (ID).
    */
    public int getIdProduct() {
        return idProduct;
    }

    /**
     * Gets the name of the product.
     * @return The name of the product.
    */
    public String getName() {
        return name;
    }

    /**
     * Gets the price of the product.
     * @return The price of the product.
    */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the quantity of the product in stock.
     * @return The quantity of the product.
    */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the date associated with the product.
     * @return The date (e.g., manufacture or update date) of the product.
    */
    public String getDate() {
        return date;
    }

    /**
     * Sets the quantity of the product.
     * @param quantity The quantity to set for the product.
    */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the name of the product.
     * @param name The name to set for the product.
    */
    public void setName(String name) {
        this.name = name;
    }
}
