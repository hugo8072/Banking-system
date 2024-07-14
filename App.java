import java.time.LocalDate; // Importing the LocalDate class for date handling
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;


public class App {
    SistemaBancario sistemaBancario = new SistemaBancario();

    //Clears the terminal. If the operative system is windows does by the comand "cls" otherwise does with the comand "clear"
    public static void clearTerminal() 
    {
        try {
            if (System.getProperty("os.name").contains("Windows")) 
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } 
            else 
            {
                Runtime.getRuntime().exec("clear");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        
            } catch (Exception e) 
            {
            // Handle any exceptions that may occur
            e.printStackTrace();
            }
    }


    // Nested class representing a bank client
    public static class Client {
        private int number;
        private String name;
        private String agency;
        private String city;
        private LocalDate openingDate; 


        // the code initializes the instance variables of the Client class with the values provided as arguments. 
        //Each parameter is assigned to the corresponding instance variable using the this keyword to  refer to the current object. 
        //This constructor allows you to create a Client object by providing the necessary information.
       private  Client(int number, String name, String agency, String city, LocalDate openingDate) {
            this.number = number;
            this.name = name;
            this.agency = agency;
            this.city = city;
            this.openingDate = openingDate;
        }
//// Getter methods to retrieve the client's number from query's.
        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public String getAgency() {
            return agency;
        }

        public String getCity() {
            return city;
        }

        public LocalDate getOpeningDate() {
            return openingDate;
        }

        private void showRealBalance(Client client, int client_num) {
    int balance = 0;

    // Creating a Prolog query to get the real balance for the given client number
    Query q = new Query("get_real_balance(" + client_num + ", Balance)");
    Map<String, Term> solution = q.oneSolution();

    if (solution == null) {
        System.out.println("No clients found with this number: " + client_num + ".");
    } else {
        System.out.println("Your balance is: ");
        // Retrieving the balance from that user account
        Term balanceTerm = solution.get("Balance");
        balance = balanceTerm.intValue(); // Converting balance to int as initially is inside of a map of terms
        System.out.println(balance);
    }
}
        
            private static int getCurrentBalancePlusCredit(int accountNumber) 
            {
                 Query q = new Query("current_balance_plus_credit(" + accountNumber + ", Balance)");
                if (q.hasSolution()) 
                {
                     Term balanceTerm = q.oneSolution().get("Balance");
                    return balanceTerm.intValue();
                 }
        return 0; // Se não houver solução, retorna 0 como saldo padrão
            }
        
        private void showCreditBalance(Client client, int number) 
        {
          
            // Creating a Prolog query to get the credit balance for the given client number
            Query q = new Query("get_credit_balance(" + number + ", Balance)");
            Map<String, Term>[] solutions = q.allSolutions();

            if (solutions.length == 0)
            {
                System.out.println("No clients found with this number: " + number + ".");
                return;
            } 
            else 
            {
                System.out.println("Found clients with this number: " + number + ":");
                // Retrieving the balance from the solution and printing it
                for (Map<String, Term> solution : solutions) 
                {
                    Term balanceTerm = solution.get("Balance");
                    int balance = balanceTerm.intValue(); // Converting it to a numeric value
                    System.out.println(" - " + number + " (" + balance + ")");
                }
            }
        }
    
        private void showTransactions(Client client, int number) {
            Query q = new Query("consult('/home/hugo_mls/teste/src/updatesTransactions.pl')");
            
            if (q.hasSolution()) {
                Query q2 = new Query("get_Bank_Transactions(" + number + ", Value, Date)");
                Map<String, Term>[] solutions = q2.allSolutions();
                
                if (solutions.length == 0) {
                    System.out.println("No transactions found with this number: " + number);
                    return;
                } else {
                    System.out.println("Found transactions with the client number: " + number);
                    
                    for (Map<String, Term> solution : solutions) {
                        Term value = solution.get("Value");
                        int balance = value.intValue();
                        String date = solution.get("Date").toString();
                        
                        System.out.println( + balance + " (" + date + ")");
                    }
                }
            } else {
                System.out.println("Failed to load necessary file: updatesTransactions.pl");
            }
        }
       



    







        public static void update_balance_credit(Client client, int client_num, int newBalance) {
            String filePath = "/home/hugo_mls/teste/src/update_balance_plus_credit.pl";
            Query consultQuery = new Query("consult('/home/hugo_mls/teste/src/update_balance_plus_credit.pl')");
        
            if (consultQuery.hasSolution()) {
                Query saveQuery = new Query(
                    new Compound("save_update_current_balance_plus_credit", new Term[]{new Atom(filePath), new org.jpl7.Integer(client_num), new org.jpl7.Integer(newBalance)}));
        
                if (saveQuery.hasSolution()) {
                    System.out.println("Balance updated successfully.");
                } else {
                    System.out.println("Failed to update balance.");
                }
            } else {
                System.out.println("Failed to consult the Prolog file.");
            }
        }
    
       
        
    
        
        private void makeDeposit( Client client, int client_num, int value) 
        {
            
            String filePath = "/home/hugo_mls/teste/src/updatesTransactions.pl";
            int id = client_num;
            int amount = value;
    
            // Get the current date and format it in the desired format.
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = currentDate.format(formatter);
    
            // create the query to update the values after the deposit
            Query query = new Query(
                    new Compound("addTransaction", new Term[]{new org.jpl7.Integer(id), new org.jpl7.Integer(amount),new Atom(formattedDate)})
            );
    
            // If has soluction
            if (query.hasSolution()) 
            {
                System.out.println("Transaction completed successfully. ");
    
                //"Save the transaction in the file 'updatesTransactions.pl'
                Query saveQuery = new Query(
                        new Compound("save_transaction", new Term[]{new Atom(filePath), new org.jpl7.Integer(id), new org.jpl7.Integer(amount), new Atom(formattedDate)})
                );
                if (saveQuery.hasSolution()) 
                {
                    System.out.println("Transaction saved successfully.");
                } 
                else 
                {
                    System.out.println("Transaction not saved in the file.");
                }
                saveQuery.close();
    
            } 
            else 
            {
                System.out.println("Failed to add the transaction.");
            }
    
            // close the query
            query.close();
        }
    
        public int check_credit_balance(Client client, int client_num, int value) {
        //    Query consultQuery = new Query("consult('src/credit.pl')");
            Query creditQuery = new Query("current_balance_plus_credit(" + new Atom(Integer.toString(client_num)) + ", " + new Atom(Integer.toString(value)) + ")");
            Map<String, Term> solution = creditQuery.oneSolution();
        
            if (solution != null) {
                Term balanceTerm = solution.get("value");
                int balance = balanceTerm.intValue();
                return balance;
            } else 
            {
              //  System.out.println("The credit_balance query failed.");
                return 0;
            }
        }
    
    
        private static int credit_balance(Client client, int client_num, int value) 
        {
            String filePath = "/home/hugo_mls/teste/src/credit.pl";

        
       
        
            // Create the query with the predicate addTransaction and the terms
            Query query = new Query(
                    new Compound("update_credit_balance", new Term[]{new org.jpl7.Integer(client_num), new org.jpl7.Integer(value)})
            );
        
            // Check if the query has a solution
            if (query.hasSolution()) 
            {
              
        
                // Save the transactionon the file 'credit.pl'
                Query saveQuery = new Query(
                        new Compound("save_credit_balance", new Term[]{new Atom(filePath), new org.jpl7.Integer(client_num), new org.jpl7.Integer(value)})
                );
                if (saveQuery.hasSolution()) 
                {
                    System.out.println("Credit granted successfully");
                    saveQuery.close();
                } 
                else 
                {
                    System.out.println("Failed to grant credit");
                }
                saveQuery.close();
        
            }
        
            // Close the query
            query.close();
            return value;
        }
        // Client Menu
        public void showClientMenu(SistemaBancario sistemaBancario, Client client, int client_num) {
            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            while (choice < 1 || choice > 7) {
                System.out.println("---- Client Menu ----");
                System.out.println("1- Real Balance");
                System.out.println("2- Credit Balance");
                System.out.println("3- View transactions");
                System.out.println("4- Make a deposit");
                System.out.println("5- Cash withdrawal");
                System.out.println("6- Check eligibility for credit");
                System.out.println("7- Back to previous menu");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                switch (choice) 
                {
                    case 1:
                    clearTerminal();
                        showRealBalance(this, client_num);
                        showClientMenu(sistemaBancario, client, client_num);
                        break;
                    case 2:
                    clearTerminal();
                        showCreditBalance(this, client_num);
                        showClientMenu(sistemaBancario, client, client_num);
                        break;
                    case 3:
                    clearTerminal();
                        showTransactions(this, client_num);
                        showClientMenu(sistemaBancario, client, client_num);
                        break;
                    case 4:
                    clearTerminal();
                        System.out.println("Enter the deposit amount");
                        choice = scanner.nextInt();
                        while (choice <= 0) 
                        {
                            clearTerminal();
                            System.out.println("Enter the deposit amount. Please, enter a positive integer ");
                            choice = scanner.nextInt();
                        }
                        makeDeposit(this, client_num, choice);
                        update_balance_credit(client, client_num,getCurrentBalancePlusCredit( client_num) +choice);
                        showClientMenu(sistemaBancario, client, client_num);

                        
                        break;
                    case 5:
                    clearTerminal();
                        if (getCurrentBalancePlusCredit( client_num) < 0) 
                        {
                            System.out.println("You don't have enough funds to make a cash withdrawal");
                        } 
                        else 
                        {
                            System.out.println("Enter the withdrawal amount");
                            choice = scanner.nextInt();
                            while (choice <= 0 || choice > getCurrentBalancePlusCredit( client_num)) 
                            {
                                System.out.println("Enter the withdrawal amount. You cannot use an amount greater than your available balance: " + getCurrentBalancePlusCredit( client_num));
                                choice = scanner.nextInt();
                            }
                            makeDeposit(this, client_num, -choice);
                           if ((check_credit_balance(client, client_num, choice)==0))
                           {int newBalance = App.Client.getCurrentBalancePlusCredit(client_num) -choice; 
                          //  System.out.println(newBalance);
                             update_balance_credit(client, client_num, newBalance);
                            showClientMenu(sistemaBancario, client, client_num);}
                            else
                            {
                            int newBalance = App.Client.getCurrentBalancePlusCredit(client_num) - check_credit_balance(client, client_num, choice)-choice; 
                           // System.out.println(newBalance);
                             update_balance_credit(client, client_num, newBalance);
                            showClientMenu(sistemaBancario, client, client_num);
                            }
                        }
                        break;
                        case 6:
                        
                        clearTerminal();
                        Query query = new Query("eligible_clients(Number, Name, Agency, City, OpeningDate), Number = " + client_num);
                        if (!query.hasMoreSolutions())
                         {
                            System.out.println("You are not eligible for credit");
                            client.showClientMenu(sistemaBancario, client, client_num);
                         } 
                         else 
                         {
                            System.out.println("You are eligible for credit.");
                            System.out.println("Do you want a credit?");
                            String word = scanner.next();
                            if (word.equalsIgnoreCase("yes")) 
                            {//use of equalsIgnoreCase to match even if is Yes, or YESS etc
                                System.out.println("How much credit do you need?");
                                choice = scanner.nextInt();
                                makeDeposit(this,client_num, choice);
                               credit_balance(this, client_num, choice);
                                update_balance_credit(this,client_num, choice + App.Client.getCurrentBalancePlusCredit(client_num));
                               // updateBalance(this,client_num, choice);
                                showClientMenu(sistemaBancario, client, client_num);

                            } 
                            else 
                            {
                                System.out.println("You allways can get it ;) ");
                                showClientMenu(sistemaBancario, client, client_num);
                            }
                    
                            scanner.close();
                        }
                       

                        break;
                       
                     
                        case 7:
                        clearTerminal();
                        System.out.println("Returning to previous menu...");
                        sistemaBancario.showMenu();
                        break;
                    default:
                    clearTerminal();
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
              
                scanner.close();
        }
    }//end of class client

    private class SistemaBancario 
    {
        private List<Client> clients;
    
        private SistemaBancario() 
        {
            clients = new ArrayList<>();
            loadClientsFromProlog();
            sortClientsByNumber();
        }
    
        private void loadClientsFromProlog() 
        {
            Query query = new Query("get_clients(Number, Name, Agency, City, OpeningDate)");//get_clientsdesnecessário
            while (query.hasMoreSolutions()) 
            {
                Map<String, Term> solution = query.nextSolution();
                int number = solution.get("Number").intValue();
                String name = solution.get("Name").toString();
                String agency = solution.get("Agency").toString();
                String city = solution.get("City").toString();
                String openingDateStr = solution.get("OpeningDate").toString();

                // Removes the '' on the city
                openingDateStr = openingDateStr.substring(1, openingDateStr.length() - 1);
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate openingDate = LocalDate.parse(openingDateStr, formatter);
                
                //create each client and add it to our structure 
                Client cliente = new Client(number, name, agency, city, openingDate);
                clients.add(cliente);
            }
        }
    
        private void sortClientsByNumber() 
        {
            clients.sort(Comparator.comparingInt(Client::getNumber));//sort clients by number
        }


        public void printClientsByCity(String city) 
        
        {
      
             Query q = new Query("clients_by_city('" + city + "', Number, Name)");
            Map<String, Term>[] solutions = q.allSolutions();
            if (solutions.length == 0 )
             {
             System.out.println("No clients found in " + city + ".");
             return;
             }
             else 
             {
                System.out.println("Found clients in " + city + ":");
                for (Map<String, Term> solution : solutions) 
                    {
                     String name = solution.get("Number").toString();
                     String number = solution.get("Name").toString();
                     System.out.println(" - " + name + " (" + number + ")");
                     
                    }
             }

   
        }


        public void printCreditEligibleClients() 
        {
            Query query = new Query("eligible_clients(Number, Name, Agency, City, OpeningDate)");
            if (!query.hasMoreSolutions()) 
            {
                System.out.println("There are no eligible clients for credit.");
            }
            while (query.hasMoreSolutions()) 
            {
                Map<String, Term> solution = query.nextSolution();
                int number = solution.get("Number").intValue();
                String name = solution.get("Name").toString();
                String agency = solution.get("Agency").toString();
                String city = solution.get("City").toString();
                String openingDate = solution.get("OpeningDate").toString();
                
                System.out.println("Number: " + number);
                System.out.println("Name: " + name);
                System.out.println("Agency: " + agency);
                System.out.println("City: " + city);
                System.out.println("Opening Date: " + openingDate);
                System.out.println("-----------------------");
            }
          
        }


       public void printClients() 
       {
            for (Client client : clients) 
            {
                System.out.println("-------------------------");
                System.out.println("Client Number: " + client.getNumber());
                System.out.println("Name: " + client.getName());
                System.out.println("Agency: " + client.getAgency());
                System.out.println("City: " + client.getCity());
                LocalDate openingDate = client.getOpeningDate();
                DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String formattedOpeningDate = openingDate.format(formatterOutput);
                System.out.println("Opening Date: " + formattedOpeningDate);
                System.out.println("-------------------------");
            }
        }
           


        public void showMenu() 
        {
            Scanner scanner = new Scanner(System.in);
            try {
                int choice = 0;
                while (choice != 5) {
                    System.out.println("Please select an option:");
                    System.out.println("1. Show list of clients");
                    System.out.println("2. Choose by city");
                    System.out.println("3. Check eligible clients for credit");
                    System.out.println("4. Client Menu");
                    System.out.println("5. Exit");
                    choice = scanner.nextInt();
                    switch (choice) 
                    {
                        case 1:
                        clearTerminal();
                            printClients();
                            break;
                        case 2:
                        clearTerminal();
                            System.out.println("Please insert the name of the city");
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                                String city = reader.readLine();
                                sistemaBancario.printClientsByCity(city);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 3:
                        clearTerminal();
                            sistemaBancario.printCreditEligibleClients();
                            break;
                            
                            case 4:
                            clearTerminal();
                            Scanner scanner2 = new Scanner(System.in);
                            System.out.print("Enter the client number: ");
                            int clientNumber = scanner2.nextInt();
                            scanner2.nextLine(); // clear the input buffer
                            boolean client_found = false;
                            // Procura o cliente na lista
                            for (Client client : clients)
                             {
                                if (client.getNumber() == clientNumber)
                                 {
                                   client.showClientMenu(this,client,clientNumber);
                                    client_found=true;
                                    break;
                                 }
                            }
                                 if (client_found==false) 
                                 { 
                                 System.out.println("Client number " + clientNumber + " not found.");
                                 sistemaBancario.showMenu();
                                 }
                                    break;

                            case 5:
                            clearTerminal();
                            System.out.println("Quitting program...");
                            System.exit(0);
                            break;

                           
                            
                        default:
                        clearTerminal();
                           System.out.println("Error: Invalid value. Please provide an integer  between 1 and 5 .");
                           break;
                    }
                }
            } catch (InputMismatchException e) 
            {
                clearTerminal();
                System.out.println("Error: Invalid value. Please provide an number between 1 and 6 .");
                sistemaBancario.showMenu();
               
            }
            finally {
                scanner.close();
            }
        }
    }

    public static void main(String[] args) 
    {
        Query q1 = new Query("consult", new Term[]{new Atom("/home/hugo_mls/teste/src/test.pl")});
        if (q1.hasSolution()) 
        {
            System.out.println("Prolog database file loaded successfully.");
        } 
        else 
        {
            System.out.println("Error loading Prolog database file.");
        }

        App app = new App();

       
        SistemaBancario sistemaBancario = app.new SistemaBancario();
         sistemaBancario.showMenu();
       
    }
}