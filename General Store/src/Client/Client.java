package Client;

/**
 * @author A. F. Valora
 * @author D. Carattin
 * @sice 10-01-2025
 * @version 1.0
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import Models.Customer;
import Models.Product;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Client class that provides the client-side functionality for a general store application.
 * This application allows users to connect to a server, login, sign up, and interact with products.
 */

public class Client {

	// Console colors for better user interface
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_RED =  "\u001B[31m";
    
	/**
     * List of products available on the server.
    */
    private static List<Product> productList = new ArrayList<>();
    private static List<Product> suggestedproductList = new ArrayList<>();
       
    
    /**
     * Handles the login process for users. Prompts for email and password, validates input, and
     * communicates with the server to authenticate the user.
     *
     * @param input   the {@link DataInputStream} for receiving data from the server
     * @param output  the {@link DataOutputStream} for sending data to the server
     * @param scanner the {@link Scanner} for user input
     * @throws IOException If there is an error while obtaining input and output streams.
    */
    public static void HandleLogin(DataInputStream input, DataOutputStream output, Scanner scanner) throws IOException {
    	Customer customer = new Customer(null, null);
        
        System.out.println("  __           __");
        System.out.println(" |__   " + ANSI_GREEN + "Login" + ANSI_RESET + "   __|");
        
        boolean i = true;
        boolean j = true;
        
     // Email validation
        while (i) {
            System.out.print("\n Insert email: ");
            String email = scanner.nextLine();
            if (isValidEmail(email)) {
            	output.writeUTF(email);
                i = false;
            } else {
            	System.out.println(ANSI_RED + "  __                                         __" + ANSI_RESET);
                System.out.println(ANSI_RED + " |__ Invalid email format. Please try again. __|\n" + ANSI_RESET);
            }
        }

        // Password validation
        while (j) {
            System.out.print(" Insert password: ");
            String password = scanner.nextLine();
            if (isValidPassword(password)) {
            	output.writeUTF(password);
            	output.flush();
                j = false;
            } else {
            	System.out.println(ANSI_RED + "  __                                                                                                                               __" + ANSI_RESET);
                System.out.println(ANSI_RED + " |__ Password must be at least 8 characters long and include at least one number, one uppercase letter, and one special character. __|\n" + ANSI_RESET);
            }
        }
        
        String serverResponse = input.readUTF();
        
        if (serverResponse.equals("LoginSuccessful")) {
        	//field client
            String emailClient = input.readUTF();
            Integer idClient = input.readInt();
            
            customer.setEmail(emailClient);
            customer.setIdCustomer(idClient);
            
            String welcomeMessage = input.readUTF();

            System.out.println(ANSI_BLUE + "\n\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"
            		+ "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "    " + welcomeMessage + ANSI_RESET);
            System.out.println(ANSI_BLUE + "\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"
            		+ "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D\n" + ANSI_RESET);

            // Second menu for logged-in users
            while (true) {
                System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "1. View Products" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "2. View Purchases" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "3. Buy Products" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "4. Return Product" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "5. Suggest New Product" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "6. Exit" + ANSI_RESET);
                System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
                System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
                String choiceSecondMenu = scanner.nextLine();
                System.out.println("\n");

                switch (choiceSecondMenu) {
                    case "1":
                        output.writeUTF("ViewProducts");
                        output.flush();
                        
                        System.out.println("  __              __");
                        System.out.println(" |__   " + ANSI_BLUE + "Products" + ANSI_RESET + "   __|\n");
                        ViewProduct(input, output, scanner);
                        break;
                    case "2":
                    	System.out.println("  __                    __");
                        System.out.println(" |__   " + ANSI_BLUE + "View Purchases" + ANSI_RESET + "   __|\n");
                       
                        // Implement purchase logic here
                        output.writeUTF("ViewPurchases");
                        output.flush();
                        ViewPurchases(input, output, scanner, customer);
                        break;
                    case "3":
                    	System.out.println("  __                  __");
                        System.out.println(" |__   " + ANSI_BLUE + "Buy Products" + ANSI_RESET + "   __|\n");
                       
                        // Implement purchase logic here
                        output.writeUTF("BuyProducts");
                        output.flush();
                        BuyProduct(input, output, scanner, customer);
                        break;
                    case "4":
                    	System.out.println("  __                    __");
                        System.out.println(" |__   " + ANSI_BLUE + "Return Product" + ANSI_RESET + "   __|\n");
                       
                        // Implement purchase logic here
                        output.writeUTF("ReturnProduct");
                        output.flush();
                        ReturnProduct(input, output, scanner, customer);
                        break;
                    case "5":
                    	System.out.println("  __                         __");
                        System.out.println(" |__   " + ANSI_BLUE + "Suggest New Product" + ANSI_RESET + "   __|\n");
                        
                        // Implement product suggestion logic here
                        output.writeUTF("SuggestProduct");
                        output.flush();
                        SuggestProduct(input, output, scanner);
                        
                        break;

                    case "6":
                        System.out.println("  -----------------------------");
                        System.out.println("  |      Exit to Account      |");
                        System.out.println("  -----------------------------");
//                        output.writeUTF("CloseConnection");
//                        output.flush();
//                        System.out.println("\n\n  |--- Connection closed ---|");
                        return;

                    default:
                        System.out.println("Invalid option! Please try again.\n");
                }
            }

        } else if (serverResponse.equals("AdminLoginSuccessful")) {
        	String welcomeMessage = input.readUTF();

        	System.out.println(ANSI_BLUE + "\n\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"
        			+ "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "    " + welcomeMessage + ANSI_RESET);
            System.out.println(ANSI_BLUE + "\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550"
            		+ "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D\n" + ANSI_RESET);

            // Second menu for logged-in
            while (true) {
                System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "1. View Products" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "2. View Suggested Product" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "3. Add Stock Suggested Product" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "4. Exit" + ANSI_RESET);
                System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
                System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
                String choiceSecondMenu = scanner.nextLine();
                
                switch (choiceSecondMenu) {
                case "1":
                    output.writeUTF("ViewProducts");
                    output.flush();
                    
                    System.out.println("  __              __");
                    System.out.println(" |__   " + ANSI_BLUE + "Products" + ANSI_RESET + "   __|\n");
                    ViewProduct(input, output, scanner);
                    break;

                case "2":
                    output.writeUTF("ViewSuggestedProducts");
                    output.flush();
                    
                    System.out.println("  __                        __");
                    System.out.println(" |__   " + ANSI_BLUE + "Suggested Products" + ANSI_RESET + "   __|\n");
                    ViewSuggestedProducts(input, output, scanner);
                    
                    break;
                    
                case "3":
                	System.out.println("  __                           __");
                    System.out.println(" |__   " + ANSI_BLUE + "Add Stock New Product" + ANSI_RESET + "   __|\n");
                    
                    // Implement product suggestion logic here
                    output.writeUTF("AddNewProduct");
                    output.flush();
                    AddNewProduct(input, output, scanner);
                    
                    break;
                    
                case "4":
                    System.out.println("  -----------------------------");
                    System.out.println("  |   Exit to Admin Account   |");
                    System.out.println("  -----------------------------");
//                    output.writeUTF("CloseConnection");
//                    output.flush();
//                    System.out.println("\n\n  |--- Connection closed ---|");
                    return;

                default:
                    System.out.println("Invalid option! Please try again.\n");
            }
          }
            
		} else if (serverResponse.equals("LoginFailed")) {
            System.out.println(ANSI_BLUE + "----------------------------------------------" + ANSI_RESET);
            System.out.println(ANSI_RED + "  Error wrong email or password! Try again." + ANSI_RESET);
            System.out.println(ANSI_BLUE + "----------------------------------------------\n" + ANSI_RESET);

        } else if (serverResponse.equals("ServerError")) {
            System.out.println(ANSI_BLUE + "---------------------------------------------" + ANSI_RESET);
            System.out.println(ANSI_RED + "        Server error! Try again later.      " + ANSI_RESET);
            System.out.println(ANSI_BLUE + "---------------------------------------------\n" + ANSI_RESET);
        }
    }
    
    
    /**
     * Handles the sign-up process for new users. Collects user details, validates input, and
     * sends data to the server for account creation.
     *
     * @param input   the {@link DataInputStream} for receiving data from the server
     * @param output  the {@link DataOutputStream} for sending data to the server
     * @param scanner the {@link Scanner} for user input
     * @throws IOException If there is an error while obtaining input and output streams.
    */
	public static void HandleSignUp(DataInputStream input, DataOutputStream output, Scanner scanner) throws IOException {
		System.out.println("  __             __");
        System.out.println(" |__   " + ANSI_GREEN + "Sign up" + ANSI_RESET + "   __|");

        // Handle user sign-up process
        do {
            System.out.print("\n Name: ");
            String nameSignup = scanner.nextLine();
            while (nameSignup.trim().isEmpty() || !nameSignup.matches("[a-zA-Z ]+")) {
                System.out.println(ANSI_BLUE+"------------------------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_RED+ "  Invalid input! Name must be non-empty and only contain letters. "+ANSI_RESET);
                System.out.println(ANSI_BLUE+"------------------------------------------------------------------ \n" + ANSI_RESET);
                System.out.print(" Name: ");
                nameSignup = scanner.nextLine();
            }
            output.writeUTF(nameSignup.substring(0,1).toUpperCase() + nameSignup.substring(1).toLowerCase());

    	    System.out.print(" Surname: ");
    	    String surnameSignup = scanner.nextLine();
    	    while (surnameSignup.trim().isEmpty() || !surnameSignup.matches("[a-zA-Z]+")) {
    	    	System.out.println(ANSI_BLUE+"---------------------------------------------------------------------" + ANSI_RESET);
    	        System.out.println(ANSI_RED+"  Invalid input! Surname must be non-empty and only contain letters." + ANSI_RESET);
    	        System.out.println(ANSI_BLUE+"--------------------------------------------------------------------- \n" + ANSI_RESET);
    	        System.out.print(" Surname: ");
    	        surnameSignup = scanner.nextLine();
    	    }
    	    output.writeUTF(surnameSignup.substring(0,1).toUpperCase() + surnameSignup.substring(1).toLowerCase());

    	    System.out.print(" Email: ");
    	    String emailSignup = scanner.nextLine();
    	    while (emailSignup.trim().isEmpty() || !emailSignup.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
    	    	System.out.println(ANSI_BLUE+"----------------------------------------------" + ANSI_RESET);
    	        System.out.println(ANSI_RED+"  Invalid input! Enter a valid email address. "+ANSI_RESET);
    	        System.out.println(ANSI_BLUE+"---------------------------------------------- \n" + ANSI_RESET);
    	        System.out.print(" Email: ");
    	        emailSignup = scanner.nextLine();
    	    }
    	    output.writeUTF(emailSignup);

    	    System.out.print(" Password: ");
    	    String passwordSignup = scanner.nextLine();
    	    while (passwordSignup.trim().isEmpty()) {
    	    	System.out.println(ANSI_BLUE+"---------------------------------------------" + ANSI_RESET);
    	        System.out.println(ANSI_RED+"  Invalid input! Password must be non-empty. "+ANSI_RESET);
    	        System.out.println(ANSI_BLUE+"--------------------------------------------- \n" + ANSI_RESET);
    	        System.out.print(" Password: ");
    	        passwordSignup = scanner.nextLine();
    	    }
    	    output.writeUTF(passwordSignup);

    	    System.out.print(" Address: ");
    	    String addressSignup = scanner.nextLine();
    	    while (addressSignup.trim().isEmpty()) {
    	    	System.out.println(ANSI_BLUE+"--------------------------------------------" + ANSI_RESET);
    	        System.out.println(ANSI_RED+"  Invalid input! Address must be non-empty. "+ANSI_RESET);
    	        System.out.println(ANSI_BLUE+"-------------------------------------------- \n" + ANSI_RESET);
    	        System.out.print(" Address: ");
    	        addressSignup = scanner.nextLine();
    	    }
    	    output.writeUTF(addressSignup.substring(0,1).toUpperCase() + addressSignup.substring(1).toLowerCase());

    	    System.out.print(" City: ");
    	    String citySignup = scanner.nextLine();
    	    while (citySignup.trim().isEmpty() || !citySignup.matches("[a-zA-Z ]+")) {
    	    	System.out.println(ANSI_BLUE+"------------------------------------------------------------------" + ANSI_RESET);
    	        System.out.println(ANSI_RED+"  Invalid input! City must be non-empty and only contain letters. "+ANSI_RESET);
    	        System.out.println(ANSI_BLUE+"------------------------------------------------------------------ \n" + ANSI_RESET);
    	        System.out.print(" City: ");
    	        citySignup = scanner.nextLine();
    	    }
    	    output.writeUTF(citySignup.substring(0,1).toUpperCase() + citySignup.substring(1).toLowerCase());


            output.flush();
        } while (false);

        String serverResponse = input.readUTF();
        System.out.println("\n |--- " + serverResponse + " ---|\n");
	}
    
	
    /**
     * Displays the list of available products retrieved from the server.
     *
     * @param input   the DataInputStream to receive data from the server
     * @param output  the DataOutputStream to send data to the server
     * @param scanner the Scanner to read user input
     * @throws IOException if an I/O error occurs while communicating with the server
    */
    public static void ViewProduct(DataInputStream input, DataOutputStream output, Scanner scanner) throws IOException {
    	
        String response = input.readUTF();
        if (response.equals("correct")) {

        	productList.clear();
            int maxLengthName = "Name".length();

            while (true) {
                String productIndicator = input.readUTF();
                if (productIndicator.equals("Stop")) {
                    break;
                }

                if (productIndicator.equals("Product")) {
                    int id = input.readInt();
                    String name = input.readUTF();
                    float price = input.readFloat();
                    int quantity = input.readInt();

                    // Local list of products
                    Product product = new Product(id, name, price, quantity);
                    productList.add(product);

                    // Update max length of the name of products
                    if(name.length() > maxLengthName) {
                        maxLengthName = name.length();
                    }
                } else {
                    System.out.println("Unexpected data received: " + productIndicator);
                }
            }

            // Print table with dynamic formatting
            String nameColumnFormat = "%-" + (maxLengthName + 2) + "s"; // Add 2 spaces for padding
            String headerFormat = " " + nameColumnFormat + "  " + " | %8s | %8s\n";
            String rowFormat =" \u2022 " + nameColumnFormat + " | %8.2f | %8d\n";

            System.out.printf(headerFormat, "NAME", "PRICE", "  QUANTITY");
            System.out.println(" "+ "-".repeat(maxLengthName + 28)); // Dynamic divider

            for (Product product : productList) {
                System.out.printf(rowFormat, product.getName(), product.getPrice(), product.getQuantity());
            }
            System.out.println("  __                       __");
            System.out.println(" |__  End of product list  __|\n");

        } else {
            System.out.println("Error retrieving product list!\n");
        }
    	
    }
    
    
    /**
     * Displays the list of purchases made by a specific customer.
     *
     * @param input     the DataInputStream to receive data from the server
     * @param output    the DataOutputStream to send data to the server
     * @param scanner   the Scanner to read user input
     * @param customer  the Customer whose purchase history is being retrieved
     * @throws IOException if an I/O error occurs while communicating with the server
    */
    public static void ViewPurchases(DataInputStream input, DataOutputStream output, Scanner scanner, Customer customer) throws IOException {
	 //send id client
	 output.writeInt(customer.getIdCustomer());
     output.flush();
     
     //answer server
	 String response = input.readUTF();
     if (response.equals("correct")) {

     	productList.clear();
         int maxLengthName = "Name".length();

         while (true) {
             String productIndicator = input.readUTF();
             if (productIndicator.equals("Stop")) {
                 break;
             }

             if (productIndicator.equals("Product")) {
                 String name = input.readUTF();
                 Double price = input.readDouble();
                 int quantity = input.readInt();

                 // Local list of products
                 Product product = new Product(name, price, quantity);
                 productList.add(product);

                 // Update max length of the name of products
                 if(name.length() > maxLengthName) {
                     maxLengthName = name.length();
                 }
             } else {
                 System.out.println("Unexpected data received: " + productIndicator);
             }
         }

         // Print table with dynamic formatting
         String nameColumnFormat = "%-" + (maxLengthName + 2) + "s"; // Add 2 spaces for padding
         String headerFormat = " " + nameColumnFormat + "  " + " | %8s | %8s\n";
         String rowFormat =" \u2022 " + nameColumnFormat + " | %8.2f | %8d\n";

         System.out.printf(headerFormat, "NAME", "PRICE", "  QUANTITY");
         System.out.println(" "+ "-".repeat(maxLengthName + 28)); // Dynamic divider

         for (Product product : productList) {
             System.out.printf(rowFormat, product.getName(), product.getPrice(), product.getQuantity());
         }
         System.out.println("  __                       __");
         System.out.println(" |__  End of product list  __|\n");

     } else {
         System.out.println("Error retrieving product list!\n");
     }
  }
    
    
    /**
     * Displays a list of suggested products retrieved from the server.
     *
     * @param input   the DataInputStream to receive data from the server
     * @param output  the DataOutputStream to send data to the server
     * @param scanner the Scanner to read user input
     * @throws IOException if an I/O error occurs while communicating with the server
    */
    public static void ViewSuggestedProducts(DataInputStream input, DataOutputStream output, Scanner scanner) throws IOException {
	String dataSugg = null;
	String response = input.readUTF();
    
	if (response.equals("correct")) {

		suggestedproductList.clear();
        int maxLengthName = "Name".length();

        while (true) {
            String productIndicator = input.readUTF();
            if (productIndicator.equals("Stop")) {
                break;
            }

            if (productIndicator.equals("Product")) {
                String name = input.readUTF();
                dataSugg = input.readUTF();
                
                // Local list of products
                Product product = new Product(name, dataSugg);
                suggestedproductList.add(product);

                // Update max length of the name of products
                if(name.length() > maxLengthName) {
                    maxLengthName = name.length();
                }
            } else {	
                System.out.println("Unexpected data received: " + productIndicator);
            }
        }

        // Print table with dynamic formatting
        String nameColumnFormat = "%-" + (maxLengthName + 2) + "s"; // Add padding for name
        String dateColumnFormat = "%-20s"; // Fixed width for date
        String headerFormat = " " + nameColumnFormat + " " + dateColumnFormat + "\n";
        String rowFormat = " \u2022 " + nameColumnFormat + " " + dateColumnFormat + "\n";

        // Print the header
        System.out.printf(headerFormat, "NAME", "  DATE");
        System.out.println(" " + "-".repeat(maxLengthName + 28)); // Dynamic divider with padding for the date

        // Print the rows
        for (Product product : suggestedproductList) {
            String formattedDate = product.getDate() != null ? product.getDate().toString() : "N/A"; // Handle null dates
            System.out.printf(rowFormat, product.getName(), formattedDate);
        }

        System.out.println("  __                       __");
        System.out.println(" |__  End of product list  __|\n");

    } else {
        System.out.println("Error retrieving product list!\n");
    }
 }
    
    
    /**
     * Allows the customer to buy a product by providing its name and quantity.
     *
     * @param input     the DataInputStream to receive data from the server
     * @param output    the DataOutputStream to send data to the server
     * @param scanner   the Scanner to read user input
     * @param customer  the Customer making the purchase
     * @throws IOException if an I/O error occurs while communicating with the server
     */
    //Buy Product class 
    public static void BuyProduct(DataInputStream input, DataOutputStream output, Scanner scanner, Customer customer) throws IOException {
        try {
            String productName = "";
            int quantity = 0;

            // Validation product name
            while (true) {
                System.out.print("\nInsert Name product: ");
                productName = scanner.nextLine().trim();
                if (!productName.isEmpty() && productName.matches("[a-zA-Z ]+")) {
                    break;
                }
                System.out.println(ANSI_BLUE + "------------------------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_RED + "  Invalid input! Name must be non-empty and only contain letters. " + ANSI_RESET);
                System.out.println(ANSI_BLUE + "------------------------------------------------------------------ \n" + ANSI_RESET);
            }

            // Quantity validation
            while (true) {
                System.out.print("Insert number: ");
                String quantityInput = scanner.nextLine().trim(); 
                try {
                    quantity = Integer.parseInt(quantityInput); 
                    if (quantity > 0) {
                        break; 
                    } else {
                        System.out.println(ANSI_BLUE + "------------------------------------------------------------------" + ANSI_RESET);
                        System.out.println(ANSI_RED + "         Invalid input! Quantity must be a positive number.        " + ANSI_RESET);
                        System.out.println(ANSI_BLUE + "------------------------------------------------------------------ \n" + ANSI_RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ANSI_BLUE + "------------------------------------------------------------------" + ANSI_RESET);
                    System.out.println(ANSI_RED + "            Invalid input! Please enter a valid number.            " + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "------------------------------------------------------------------ \n" + ANSI_RESET);
                }
            }

            // Sent to server
            output.writeInt(customer.getIdCustomer());
            output.writeUTF(productName);
            output.writeInt(quantity);
            output.flush();

            // Server answer 
            String serverResponse = input.readUTF();
            System.out.println("\n |--- " + serverResponse + " ---|\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Allows the customer to return a product by providing its name.
     *
     * @param input     the DataInputStream to receive data from the server
     * @param output    the DataOutputStream to send data to the server
     * @param scanner   the Scanner to read user input
     * @param customer  the Customer returning the product
     * @throws IOException if an I/O error occurs while communicating with the server
    */
    public static void ReturnProduct(DataInputStream input, DataOutputStream output, Scanner scanner, Customer customer) throws IOException {
	  try {
		  
		  String returnedProduct = "";
	  	  boolean var = true;
	  		
	  		while (var) {
	  			System.out.println("What product do you want to return ? ");
	  			System.out.print("Name product: ");
	  			returnedProduct = scanner.nextLine();
	      		
	      		if(!returnedProduct.isEmpty() && returnedProduct.matches("[a-zA-Z ]+")) {
	      			output.writeInt(customer.getIdCustomer());
	      			output.writeUTF(returnedProduct);
	                
	      			output.flush();
	                var = false;
	      		}
	      		else {
	      			System.out.println(ANSI_BLUE + "------------------------------------------------------------------" + ANSI_RESET);
	                  System.out.println(ANSI_RED + "           Invalid input! Name must be a string.                  " + ANSI_RESET);
	                  System.out.println(ANSI_BLUE + "------------------------------------------------------------------ \n" + ANSI_RESET);
	  			}
	  		}
	  		
	  		// Server answer 
	          String serverResponse = input.readUTF();
	          System.out.println("\n |--- " + serverResponse + " ---|\n");
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
    
    
    /**
     * Allows a user to suggest a new product to be added to the store.
     *
     * @param input   the DataInputStream to receive data from the server
     * @param output  the DataOutputStream to send data to the server
     * @param scanner the Scanner to read user input
     * @throws IOException if an I/O error occurs while communicating with the server
    */
    public static void SuggestProduct(DataInputStream input, DataOutputStream output, Scanner scanner) throws IOException {
    	try {
    		String suggestedProd = "";
    		boolean var = true;
    		
    		while (var) {
    			System.out.println("What product do you suggest adding to our store ? ");
    			System.out.print("Name product: ");
        		suggestedProd = scanner.nextLine();
        		
        		if(!suggestedProd.isEmpty() && suggestedProd.matches("[a-zA-Z ]+")) {
        			output.writeUTF(suggestedProd);
                    output.flush();
                    var = false;
        		}
        		else {
        			System.out.println(ANSI_BLUE + "------------------------------------------------------------------" + ANSI_RESET);
                    System.out.println(ANSI_RED + "           Invalid input! Name must be a string.                  " + ANSI_RESET);
                    System.out.println(ANSI_BLUE + "------------------------------------------------------------------ \n" + ANSI_RESET);
    			}
    		}
    		
    		// Server answer 
            String serverResponse = input.readUTF();
            System.out.println("\n |--- " + serverResponse + " ---|\n");
			
		} catch (Exception e) {
			 e.printStackTrace();
		}
    }
    
    
    /**
     * Allows a new product to be added to the store by providing its details.
     *
     * @param input   the DataInputStream to receive data from the server
     * @param output  the DataOutputStream to send data to the server
     * @param scanner the Scanner to read user input
     * @throws IOException if an I/O error occurs while communicating with the server
    */
    public static void AddNewProduct(DataInputStream input, DataOutputStream output, Scanner scanner) throws IOException {
        try {
            do {
                System.out.print("\n Name Product: ");
                String nameProduct = scanner.nextLine();
                while (nameProduct.trim().isEmpty() || !nameProduct.matches("[a-zA-Z ]+")) {
                    System.out.println("------------------------------------------------------------------");
                    System.out.println("  Invalid input! Name must be non-empty and only contain letters. ");
                    System.out.println("------------------------------------------------------------------");
                    System.out.print(" Name: ");
                    nameProduct = scanner.nextLine();
                }
                output.writeUTF(nameProduct.substring(0, 1).toUpperCase() + nameProduct.substring(1).toLowerCase());

                double priceProduct = 0;
                while (true) {
                    System.out.print(" Price: ");
                    if (scanner.hasNextDouble()) {
                        priceProduct = scanner.nextDouble();
                        if (priceProduct > 0) break; 
                    } else {
                        System.out.println("------------------------------------------------------------------");
                        System.out.println("  Invalid price input! Please enter a valid positive number.");
                        System.out.println("------------------------------------------------------------------");
                    }
                    scanner.nextLine(); // clear buffer
                }

                output.writeDouble(priceProduct);
                scanner.nextLine(); // clear buffer

                int quantityProduct = 0;
                while (true) {
                    System.out.print(" Quantity: ");
                    if (scanner.hasNextInt()) {
                        quantityProduct = scanner.nextInt();
                        if (quantityProduct > 0) break; 
                    } else {
                        System.out.println("------------------------------------------------------------------");
                        System.out.println("  Invalid quantity input! Please enter a valid positive integer.");
                        System.out.println("------------------------------------------------------------------");
                    }
                    scanner.nextLine(); //clear buffer
                }
                
                output.writeInt(quantityProduct);
                scanner.nextLine(); // clear buffer

                output.flush();

            } while (false);

            // answer server
            String serverResponse = input.readUTF();
            System.out.println("\n |--- " + serverResponse + " ---|\n");
            //product delete message
            String serverResponse2 = input.readUTF();
            System.out.println("\n |--- " + serverResponse2 + " ---|\n");

        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Please try again.");
            scanner.nextLine(); // clear buffer
        } catch (IOException e) {
            System.out.println("Error: Communication issue with the server.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred.");
            e.printStackTrace();
        }
    }
    
    
    /**
     * Validates the format of an email address.
     *
     * @param email the email address to validate
     * @return true if the email format is valid, false otherwise
     */
    //Method to validate email
    private static boolean isValidEmail(String email) {
     String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
     return Pattern.matches(emailRegex, email);
    }

    
    /**
     * Validates the format and strength of a password.
     *
     * A valid password must:
     * <ul>
     *   <li>Be at least 8 characters long</li>
     *   <li>Contain at least one upper case letter</li>
     *   <li>Contain at least one lower case letter</li>
     *   <li>Contain at least one digit</li>
     *   <li>Contain at least one special character (@$!%*?&;)</li>
     * </ul>
     *
     * @param password the password to validate
     * @return true if the password meets all the criteria, false otherwise
    */
    // Method to validate password
    private static boolean isValidPassword(String password) {
     if (password.length() < 8) {
         return false;
     }
     String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&;])[A-Za-z\\d@$!%*?&;]{8,}$";
     return Pattern.matches(passwordRegex, password);
    }
 
 
   /**
     * This class represents a client-side application for interacting with a server that hosts a general store system.
     * The application allows users to log in, sign up, and perform various operations such as viewing products,
     * making purchases, returning products, and suggesting new products. Administrators have additional functionalities
     * like managing stock for suggested products.
     *
     * <p>The client communicates with the server using a TCP socket connection, exchanging data via {@link DataInputStream}
     * and {@link DataOutputStream}.
     *
     * <p>The program offers a command-line interface with menus for both general users and administrators.
     *
     * <h2>Features:</h2>
     * <ul>
     *   <li>User login with email and password validation.</li>
     *   <li>New user registration (SignUp) with input validation.</li>
     *   <li>Post-login operations, including viewing and managing products.</li>
     *   <li>Administrator functionalities like viewing and adding suggested products.</li>
     * </ul>
   */
    
    /**
     * The main method of the client application. It establishes a connection to the server,
     * handles user input, and facilitates interactions with the server.
     *
     * @param args command-line arguments (not used in this application)
    */
    public static void main(String args[]) {

        // Server IP and port configuration
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 6789;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(" |--- Connected to the server! ---|\n");
            System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "         GENERAL STORE              " + ANSI_RESET);
            System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);

            // Main menu loop
            while (true) {

                System.out.println(ANSI_YELLOW + "Choose an option:" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "1. Login" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "2. SignUp" + ANSI_RESET);
                System.out.println("  " + ANSI_GREEN + "3. Exit" + ANSI_RESET);
                System.out.println(ANSI_BLUE + "=====================================" + ANSI_RESET);
                System.out.print(ANSI_YELLOW + "Your choice: " + ANSI_RESET);
                String choice = scanner.nextLine();
                System.out.println("\n");

                switch (choice) {
                    case "1":
                        output.writeUTF("Login");
                        output.flush();

                        HandleLogin(input, output, scanner);
                        break;

                    case "2":
                        output.writeUTF("SignUp");
                        output.flush();

                        HandleSignUp(input, output, scanner);
                        break;

                    case "3":
                        System.out.println("  ---------------");
                        System.out.println("  |   Goodbye!  |");
                        System.out.println("  ---------------");
                        output.writeUTF("CloseConnection");
                        output.flush();
                        System.out.println("\n\n |--- Connection closed ---|");
                        return;

                    default:
                        System.out.println(ANSI_RED + "Option not valid!" + ANSI_RESET + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
