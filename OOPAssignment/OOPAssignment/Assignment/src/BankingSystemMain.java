import banking.Bank;
import banking.BankAccount;
import banking.CurrentAccount;
import banking.InsufficientFundsException;
import banking.InvalidMenuChoiceException;
import banking.InvalidTransactionException;
import banking.SavingsAccount;
import banking.TransactionLogger;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class BankingSystemMain
{
    public static void main(String[] args)
    {
        Bank bank = new Bank(new TransactionLogger("transactions.txt"));

        // POLYMORPHISM: BankAccount references hold SavingsAccount and CurrentAccount objects
        BankAccount acc1 = new SavingsAccount("901020463", "Katleho", 1500.00);
        BankAccount acc2 = new CurrentAccount("901020123", "Puseletso", 500.00);
        BankAccount acc3 = new SavingsAccount("901020567", "Ruth", 1000.00);

        bank.addAccount(acc1);
        bank.addAccount(acc2);
        bank.addAccount(acc3);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Console Banking System");

        while (running)
        {
            printMenu();
            // EXCEPTION HANDLING: every menu action is guarded so bad input never crashes the program
            try
            {
                int choice = readMenuChoice(scanner);
                switch (choice)
                {
                    case 1:
                        displayAllAccounts(bank);
                        break;

                    case 2:
                    {
                        String type = chooseAccountType(scanner);
                        String no = readAccountOfType(scanner, bank, type);
                        double amount = readAmount(scanner, "Deposit amount: M");
                        bank.deposit(no, amount);
                        System.out.println("Deposit successful.");
                        break;
                    }

                    case 3:
                    {
                        String type = chooseAccountType(scanner);
                        String no = readAccountOfType(scanner, bank, type);
                        double amount = readAmount(scanner, "Withdrawal amount: M");
                        bank.withdraw(no, amount);
                        System.out.println("Withdrawal successful.");
                        break;
                    }

                    case 4:
                    {
                        String from = readText(scanner, "Transfer FROM account number: ");
                        String to = readText(scanner, "Transfer TO account number: ");
                        double amount = readAmount(scanner, "Transfer amount: M");
                        bank.transfer(from, to, amount);
                        System.out.println("Transfer successful.");
                        break;
                    }

                    case 5:
                    {
                        String no = readText(scanner, "Account number: ");
                        double interest = bank.calculateInterest(no);
                        System.out.println("Interest earned: " + BankAccount.formatMoney(interest)
                                + (interest == 0 ? " (this account earns no interest)" : " (credited to balance)"));
                        break;
                    }

                    case 6:
                    {
                        String no = readText(scanner, "Account number: ");
                        double amount = readAmount(scanner, "Loan amount: M");
                        bank.requestLoan(no, amount);
                        System.out.println("Loan approved and credited to the account.");
                        break;
                    }

                    case 7:
                    {
                        String no = readText(scanner, "Account number: ");
                        bank.findAccount(no).displayDetails();   // POLYMORPHISM
                        break;
                    }

                    case 8:
                        running = false;
                        System.out.println("Thank you for banking with us. Goodbye!");
                        break;

                    default:
                        throw new InvalidMenuChoiceException("Please choose a number from 1 to 8.");
                }
            }
            catch (InvalidMenuChoiceException | InvalidTransactionException | InsufficientFundsException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
            catch (NoSuchElementException e)
            {
                System.out.println("\nInput closed. Exiting.");
                running = false;
            }
            catch (RuntimeException e)
            {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printMenu()
    {
        System.out.println();
        System.out.println("MENU");
        System.out.println("1. Display all accounts");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Transfer money");
        System.out.println("5. Calculate interest");
        System.out.println("6. Request bank loan");
        System.out.println("7. Display account details");
        System.out.println("8. Exit");
    }

    private static void displayAllAccounts(Bank bank)
    {
        printTableHeader();
        // POLYMORPHISM: toString()/getAccountType() resolve to the subclass at run time
        for (BankAccount account : bank.getAccounts())
        {
            System.out.println(account);
        }
    }

    private static void printTableHeader()
    {
        System.out.println();
        System.out.printf("%-9s | %-15s | %-8s | %s%n", "ACCOUNT", "HOLDER", "TYPE", "BALANCE");
        System.out.println("------------------------------------------------------");
    }

    private static String chooseAccountType(Scanner scanner) throws InvalidMenuChoiceException
    {
        System.out.println("Choose account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        int choice = readMenuChoice(scanner, "Enter choice (1-2): ", 1, 2);
        return choice == 1 ? "Savings" : "Current";
    }

    // Reads an account number and checks it belongs to the chosen account type
    private static String readAccountOfType(Scanner scanner, Bank bank, String type)
            throws InvalidTransactionException
    {
        String no = readText(scanner, "Account number: ");
        BankAccount account = bank.findAccount(no);
        if (!account.getAccountType().equals(type))
        {
            throw new InvalidTransactionException("Account " + account.getAccountNumber()
                    + " is a " + account.getAccountType() + " account, not a " + type + " account.");
        }
        return no;
    }

    private static int readMenuChoice(Scanner scanner, String prompt, int min, int max)
            throws InvalidMenuChoiceException
    {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try
        {
            int choice = Integer.parseInt(input);
            if (choice < min || choice > max)
            {
                throw new InvalidMenuChoiceException("Please choose a number from " + min + " to " + max + ".");
            }
            return choice;
        }
        catch (NumberFormatException e)
        {
            throw new InvalidMenuChoiceException("\"" + input + "\" is not a valid menu choice. Enter a number.");
        }
    }

    private static int readMenuChoice(Scanner scanner) throws InvalidMenuChoiceException
    {
        return readMenuChoice(scanner, "Enter your choice: ", 1, 8);
    }

    private static String readText(Scanner scanner, String prompt) throws InvalidTransactionException
    {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        if (text.isEmpty())
        {
            throw new InvalidTransactionException("Account number cannot be empty.");
        }
        return text;
    }

    private static double readAmount(Scanner scanner, String prompt) throws InvalidTransactionException
    {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try
        {
            return Double.parseDouble(input);
        }
        catch (NumberFormatException e)
        {
            throw new InvalidTransactionException("'" + input + "' is not a valid number.");
        }
    }
}
