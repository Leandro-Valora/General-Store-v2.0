package Server;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @sice 10-01-2025
 * @version 1.0
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import Models.Cart;
import Models.Product;
import Server.Server.globalConn;

/**
 * Class used for communicate client with the server.
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    
    /**
     * Constructor to initialize the client handler with the provided socket.
     * 
     * @param sock The socket representing the connection to the client.
     * @throws IOException If there is an error while obtaining input and output streams.
    */	
    public ClientHandler(Socket sock) throws IOException {
        try {
            socket = sock;
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Handles the login process. It reads the user name and password sent by the client,
     * verifies the credentials in the database, and sends the appropriate response to the client.
     * 
     * @throws IOException If there is an error during communication with the client or database.
    */
    public void Login() throws IOException {
        String username = input.readUTF();
        String password = input.readUTF();

        System.out.println("ClientHandler -> login");

        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String selectQuery = "SELECT * FROM Customers WHERE Email = ? AND Psw = ?";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String validName = resultSet.getString("Name");
                    int isAdmin = resultSet.getInt("Is_Admin");
                    
                    if(isAdmin == 1) {
                    	output.writeUTF("AdminLoginSuccessful");
                    	 output.writeUTF(" WELCOME ADMIN " + validName.toUpperCase());
                    } else {
                    	System.out.println("Login Successful");
                    	output.writeUTF("LoginSuccessful"); 
                    	output.writeUTF(resultSet.getString("Email"));
                        output.writeInt(resultSet.getInt("Id_Customer"));
                        
                        output.writeUTF(" WELCOME " + validName.toUpperCase());
					}
                    
                } else {
                	System.out.println("Login Failed");
                    output.writeUTF("LoginFailed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            output.writeUTF("ServerError");
        }
    }

    
    /**
     * Handles the user sign up process. It reads the required user details from the client, checks
     * if the email already exists in the database, and either creates a new account or informs the
     * client that the account already exists.
     * 
     * @throws IOException If there is an error during communication with the client or database.
    */
    public void SignUp() throws IOException {
        boolean exist = false;  
        String username = input.readUTF();
        String usersurname = input.readUTF();
        String email = input.readUTF();
        String password = input.readUTF();
        String address = input.readUTF();
        String city = input.readUTF();

        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT * FROM Customers WHERE Email = ? LIMIT 1";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, email);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    System.out.print("Already existent!\n");
                    output.writeUTF("Already existent!");
                } else {
                    exist = false;
                    System.out.print("Account doesn't exist, it will be created !\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(!exist) {
                String insertQuery = "INSERT INTO Customers (Name, Surname, Email, Psw, Drop_address, City) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, usersurname);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, password);
                    preparedStatement.setString(5, address);
                    preparedStatement.setString(6, city);

                    preparedStatement.executeUpdate();
                    System.out.print("Account created!\n");
                    output.writeUTF("Your account has been created!");
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        output.flush();
    }

    
    /**
     * Retrieves the available products from the database and sends the product details to the client.
     * The method sends product information in a loop until all products have been sent, followed by a "Stop" message.
     * 
     * @throws IOException If there is an error during communication with the client or database.
    */
    public void ViewProducts() throws IOException {
        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String selectQuery = "SELECT * FROM Products WHERE Quantity > 0;";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                output.writeUTF("correct");

                while (resultSet.next()) {
                    output.writeUTF("Product");
                    output.writeInt(resultSet.getInt("Id_Product"));
                    output.writeUTF(resultSet.getString("Name"));
                    output.writeFloat(resultSet.getFloat("Price"));
                    output.writeInt(resultSet.getInt("Quantity"));
                    output.flush();
                }

                output.writeUTF("Stop");
                output.flush();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Handles viewing purchases for a specific customer.
     * Reads the customer ID, retrieves purchases from the database, and sends purchase details to the client.
     * 
     * @throws IOException if an I/O error occurs during communication.
    */
    public void ViewPurchases() throws IOException {
    	
    	int idClient = input.readInt();
    	
    	try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String selectQuery = "SELECT CustomersPurchase.Id_Customer, Products.Name, Products.Price, CustomersPurchase.Quantity \r\n"
            					+ "FROM Products, CustomersPurchase \r\n"
            					+ "WHERE CustomersPurchase.Id_Product = Products.Id_Product AND CustomersPurchase.Id_Customer = ?;";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
            	preparedStatement.setInt(1, idClient);
                ResultSet resultSet = preparedStatement.executeQuery();
                output.writeUTF("correct");

                while (resultSet.next()) {
                    output.writeUTF("Product");
                    output.writeUTF(resultSet.getString("Products.Name"));
                    output.writeDouble(resultSet.getDouble("Products.Price"));
                    output.writeInt(resultSet.getInt("CustomersPurchase.Quantity"));
                    output.flush();
                }

                output.writeUTF("Stop");
                output.flush();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Retrieves and sends a list of suggested products from the database to the client.
     * Formats the suggestion date as a string before sending.
     * 
     * @throws IOException if an I/O error occurs during communication.
    */
    public void ViewSuggestedProducts() throws IOException {
        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String selectQuery = "SELECT * FROM SuggestedProduct ;";

            try (PreparedStatement preparedStatement = con.prepareStatement(selectQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                output.writeUTF("correct");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format DATETIME

                while (resultSet.next()) {
                    output.writeUTF("Product");
                    output.writeUTF(resultSet.getString("Name_Suggested"));

                    Timestamp dateSuggested = resultSet.getTimestamp("Data_Suggest"); // Use getTimestamp per DATETIME
                    
                    if (dateSuggested != null) {
                        output.writeUTF(dateFormat.format(dateSuggested));
                    } else {
                        output.writeUTF("null"); // null
                    }

                    output.flush();
                }

                output.writeUTF("Stop");
                output.flush();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Processes a product purchase request.
     * Validates product availability, updates inventory, and creates a receipt for the purchase.
     * 
     * @throws IOException if an I/O error occurs during communication.
    */
    public void BuyProducts() throws IOException {
        boolean exist = false;
        int calcQuantity = 0;
        
        Integer idClient = input.readInt();
        String prductName = input.readUTF();
        Integer quantity = input.readInt(); 
        
        Product product = new Product(0, prductName, 0, 0);
        Cart cart = new Cart(0, 0, 0);
        
        System.out.println("Buy prod -> " + prductName + " qntity ->" + quantity);
        
        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT * FROM Products WHERE Name = ? AND Quantity > 0 LIMIT 1; ";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, prductName);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    product.setQuantity(resultSetCheck.getInt("Quantity"));                    
                    cart.setIdCartProd(resultSetCheck.getInt("Id_Product"));
                    cart.setNumProduct(resultSetCheck.getInt("Quantity"));
                    
                    calcQuantity = cart.fillCart(product.getQuantity(), quantity);
                    
                } else {
                    exist = false;
                    System.out.print("Product doesn't exist!\n");
                    output.writeUTF("Product doesn't exist!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(exist) {
                String updatedQuery = "UPDATE Products SET Quantity = ? WHERE Id_Product = ?;";

                try (PreparedStatement preparedStatement = con.prepareStatement(updatedQuery)) {
                    preparedStatement.setInt(1, calcQuantity);
                    preparedStatement.setInt(2, cart.getIdCartProd());
                    preparedStatement.executeUpdate();
                    
                    cart.setNumProduct(quantity);
                    cart.setIdCartCust(idClient);
                    
                    CreateReceipt(cart);
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
        output.flush();
    }
    
    
    /**
     * Creates a receipt for a completed purchase by inserting data into the database.
     * 
     * @param cart the cart containing purchase details (customer ID, product ID, quantity).
     * @throws IOException if an I/O error occurs during communication.
    */
    public void CreateReceipt(Cart cart) throws IOException {
    	
    	try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "INSERT INTO CustomersPurchase (Id_Customer, Id_Product, Quantity) VALUES (?, ?, ?); ";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setInt(1, cart.getIdCartCust());
                preparedStatement.setInt(2, cart.getIdCartProd());
                preparedStatement.setInt(3, cart.getNumProduct());
                preparedStatement.executeUpdate();

                
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.print("Updated!\n");
            output.writeUTF("Updated product in your cart!");
            
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
        output.flush();
    }
    
    
    /**
     * Handles suggestions for new products from users.
     * Checks if the suggested product already exists; if not, inserts it into the suggested products table.
     * 
     * @throws IOException if an I/O error occurs during communication.
    */
    public void SuggestProduct() throws IOException {
    	boolean exist = false;
        
        String suggName = input.readUTF();
        Product suggProd = new Product(suggName);
                
        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT * FROM Products WHERE Name = ? AND Quantity > 0 LIMIT 1; ";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, suggName);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    System.out.print("Suggested product exist!\n");
                    output.writeUTF("Suggested product exist!");
                    
                } else {
                    exist = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(!exist) {
                String updatedQuery = "INSERT INTO SuggestedProduct (Name_Suggested) VALUES (?); ";

                try (PreparedStatement preparedStatement = con.prepareStatement(updatedQuery)) {
                    preparedStatement.setString(1, (suggProd.getName().substring(0,1).toUpperCase() + suggProd.getName().substring(1).toLowerCase()));

                    preparedStatement.executeUpdate();
                    System.out.print("Added suggested product in list!\n");
                    output.writeUTF("Thanks for your suggestion! We will look upon your request.");
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
        output.flush();
    }
    
    
    /**
     * Deletes a specific product suggestion from the database.
     * Checks for existence before attempting deletion.
     * 
     * @param deleteSuggName the name of the suggested product to delete.
     * @throws IOException if an I/O error occurs during communication.
    */
    public void DeleteSuggestProdList(String deleteSuggName) throws IOException {
    	
    	boolean exist = false;
                        
        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT * FROM SuggestedProduct WHERE Name_Suggested = ? ;";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
            	preparedStatement.setString(1, deleteSuggName);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                } else {
                    exist = false;
                    System.out.print("Suggested product " + deleteSuggName + " doesn't exist!\n");
                    output.writeUTF("Suggested product " + deleteSuggName + " doesn't exist!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(exist) {
                String updatedQuery = "DELETE FROM SuggestedProduct WHERE Name_Suggested = ?; ";

                try (PreparedStatement preparedStatement = con.prepareStatement(updatedQuery)) {
                    preparedStatement.setString(1, deleteSuggName);

                    preparedStatement.executeUpdate();
                    System.out.print("Deleted row!\n");
                    output.writeUTF("Deleted suggested " + deleteSuggName + " product!");
                }
            } else {
            	System.out.print("Error query!\n");
			}

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
        output.flush();
    }
    
    
    /**
     * Adds a new product to the inventory based on a suggestion.
     * Validates the suggestion's existence, inserts the product, and deletes the suggestion.
     * 
     * @throws IOException if an I/O error occurs during communication.
    */
    public void AddNewProduct() throws IOException {
    	boolean exist = false;  
        String prodName = input.readUTF();
        double prodPrice = input.readDouble();
        int prodQuantity = input.readInt();
        

        try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT Name_Suggested FROM SuggestedProduct WHERE Name_Suggested LIKE ? ;";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, "%" + prodName + "%");
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    prodName = resultSetCheck.getString("Name_Suggested");
                } else {
                    exist = false;
                    System.out.print("Product doesn't exist in suggested products!\n");
                    output.writeUTF("Product doesn't exist in suggested products!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(exist) {
                String insertQuery = "INSERT INTO Products (Name, Price, Quantity) VALUES (?, ?, ?)";

                try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, prodName);
                    preparedStatement.setDouble(2, prodPrice);
                    preparedStatement.setInt(3, prodQuantity);

                    preparedStatement.executeUpdate();
                    System.out.print("Product added!\n");
                    output.writeUTF("Product has been added!");
                }
                DeleteSuggestProdList(prodName);
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        output.flush();
    }
    
    
    /**
     * Processes a request to return a previously purchased product.
     * Updates inventory and removes the purchase record.
     * 
     * @throws IOException if an I/O error occurs during communication.
    */
    public void ReturnProduct() throws IOException {
    	boolean exist = false; 
    	int returnedQuantity = 0;
        int idReturnedProduct = 0;
        int idPurchase = 0;
        		
    	int returnedIdClient = input.readInt();
    	String returnedNameProd = input.readUTF();
        
    	try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "SELECT Products.Id_Product, Products.Name, CustomersPurchase.Quantity, CustomersPurchase.Id_CustomerPurchase\r\n"
            		+ "FROM Products, CustomersPurchase\r\n"
            		+ "WHERE CustomersPurchase.Id_Product = Products.Id_Product AND \r\n"
            		+ "Products.Name LIKE ? AND CustomersPurchase.Id_Customer = ? ;";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setString(1, returnedNameProd + "%");
                preparedStatement.setInt(2, returnedIdClient);
                ResultSet resultSetCheck = preparedStatement.executeQuery();

                if(resultSetCheck.next()) {
                    exist = true;
                    returnedQuantity = resultSetCheck.getInt("Quantity");
                    idReturnedProduct = resultSetCheck.getInt("Id_Product");
                    idPurchase = resultSetCheck.getInt("Id_CustomerPurchase");

                } else {
                    exist = false;
                    System.out.print("Past purchase doesn't exist!\n");
                    output.writeUTF("Past purchase doesn't exist!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(exist) {
            	String updatedQuery = "UPDATE Products SET Quantity = Quantity + ? WHERE Id_Product = ?; ";

                try (PreparedStatement preparedStatement = con.prepareStatement(updatedQuery)) {
                    preparedStatement.setInt(1, returnedQuantity);
                    preparedStatement.setInt(2, idReturnedProduct);

                    preparedStatement.executeUpdate();
                    
                    DeleteRefundedProduct(idPurchase);
                }
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        output.flush();
    }
    
    
    /**
     * Deletes a refunded product's record from the purchase history.
     * 
     * @param idPurchase the ID of the purchase to delete.
     * @throws IOException if an I/O error occurs during communication.
    */
    public void DeleteRefundedProduct(int idPurchase) throws IOException {
    	try (Connection con = DriverManager.getConnection(globalConn.jdbcURL, globalConn.usernameDb, globalConn.passwordDb)) {
            String queryCheck = "DELETE FROM CustomersPurchase WHERE CustomersPurchase.Id_CustomerPurchase = ?; ";

            try (PreparedStatement preparedStatement = con.prepareStatement(queryCheck)) {
                preparedStatement.setInt(1, idPurchase);
                preparedStatement.executeUpdate();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.print("The product has been successfully refaunded!\n");
            output.writeUTF("The product has been successfully refaunded!");
            
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
        output.flush();
    }
    
    
    
    /**
     * The run method continuously listens for client requests and invokes the corresponding method
     * to handle the request (e.g., Login, SignUp, ViewProducts). The server will keep listening
     * for commands until the client closes the connection.
    */
    @Override
    public void run() {
        try {
            while (true) {
                String commandToExecute;
                try {
                    commandToExecute = input.readUTF();
                } catch (EOFException e) {
                    System.out.println("Connessione terminata dal client.");
                    break;
                }

                System.out.println("Command received: " + commandToExecute);

                switch (commandToExecute) {
                    case "Login":
                        Login();
                        break;
                    case "SignUp":
                        SignUp();
                        break;
                    case "ViewProducts":
                        ViewProducts();
                        break;
                    case "ViewPurchases":
                    	ViewPurchases();
                        break;
                    case "ViewSuggestedProducts":
                    	ViewSuggestedProducts();
                        break;
                    case "BuyProducts":
                    	BuyProducts();
                        break;
                    case "SuggestProduct":
                    	SuggestProduct();
                        break;
                    case "AddNewProduct":
                    	AddNewProduct();
                        break;
                    case "ReturnProduct":
                    	ReturnProduct();
                        break;
                        
                    case "CloseConnection":
                        System.out.println("Client connection closed.");
                        socket.close();
                        return; // close thread
                    default:
                        System.out.println("Command not recognized: " + commandToExecute);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
