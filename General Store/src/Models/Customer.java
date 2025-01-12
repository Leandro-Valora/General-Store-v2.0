package Models;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @version 1.0
 * This Class is used to contain information about the customer
 */

public class Customer {

    private int idCustomer;
    private String nameCustomer;
    private String email;
    private String password;
    
    /**
     * Constructor class customer 
     * @param idCustomer The unique identifier for the customer.
     * @param name The name of the customer.
     * @param email The email address of the customer.
     * @param password The password of the customer for authentication.
     */
    public Customer(int idCustomer, String name, String email, String password) {
        this.idCustomer = idCustomer;
        nameCustomer = name;
        this.email = email;
        this.password = password;
    }
    
    /**
     * Constructor used for login page 
     * @param email The email address of the customer used for login.
     * @param password The password of the customer for authentication.
     */
    public Customer(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    /**
     * Constructor used for login page 
     * @param idCustomer The unique identifier of the customer.
     * @param email The email address of the customer used for login.
     */
    public Customer(int idCustomer, String email) {
        this.idCustomer = idCustomer;
        this.email = email;
    }
    
    
    /**
     * Get Number id of the customer
     * @return customers' id
     */
    public int getIdCustomer() {
        return idCustomer;
    }

    /**
     * Get the customer name
     * @return customers' name
     */
    public String getName() {
        return nameCustomer;
    }
    
    /**
     * Get the email of the customer used for access 
     * @return customers' email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the password of the customer used for access
     * @return customers' password
     */
    public String getPassword() {
        return password;
    }

    
    /**
     * Set the id of the customer
     * @param id The unique identifier to set for the customer.
     */
    public void setIdCustomer(Integer id) {
        idCustomer = id;
    }
    
    /**
     * Set the name of the customer
     * @param name The name to set for the customer.
     */
    public void setName(String name) {
        nameCustomer = name;
    }

    /**
     * Set the email of the customer
     * @param email The email to set for the customer.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the password of the customer
     * @param password The password to set for the customer.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
