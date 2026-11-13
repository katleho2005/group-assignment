package banking;

import java.util.Locale;

/**
 * ABSTRACTION: BankAccount is an abstract superclass. It holds the data and
 * behaviour common to every account and declares the banking processes that
 * each subclass must implement in its own way.
 */
public abstract class BankAccount
{
    // ENCAPSULATION: all fields are private and only reachable through
    // the constructor, getters and the controlled protected setter below.
    private final String accountNumber;
    private final String holderName;
    private double balance;

    protected BankAccount(String accountNumber, String holderName, double openingBalance)
    {
        if (accountNumber == null || accountNumber.isBlank())
        {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (holderName == null || holderName.isBlank())
        {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        // BANKING RULE: opening balance must not be negative
        if (Double.isNaN(openingBalance) || Double.isInfinite(openingBalance) || openingBalance < 0)
        {
            throw new IllegalArgumentException("Opening balance must not be negative.");
        }
        this.accountNumber = accountNumber.trim().toUpperCase();
        this.holderName = holderName.trim();
        this.balance = openingBalance;
    }

    // ---- Getters ----
    public String getAccountNumber()
    {
        return accountNumber;
    }

    public String getHolderName()
    {
        return holderName;
    }

    public double getBalance()
    {
        return balance;
    }

    // Controlled setter: only subclasses may change the balance 
    protected void setBalance(double newBalance)
    {
        if (Double.isNaN(newBalance) || Double.isInfinite(newBalance))
        {
            throw new IllegalArgumentException("Invalid balance value.");
        }
        this.balance = newBalance;
    }

    // Shared behaviour used by every subclass
    protected void validateAmount(double amount, String action) throws InvalidTransactionException
    {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0)
        {
            throw new InvalidTransactionException(action + " amount must be greater than zero.");
        }
    }

    // ABSTRACT METHODS: each subclass supplies its own implementation
    public abstract void deposit(double amount) throws InvalidTransactionException;

    public abstract void withdraw(double amount)
            throws InvalidTransactionException, InsufficientFundsException;

    public abstract double calculateInterest();

    public abstract String getAccountType();

    protected void printTypeSpecificDetails()
    {
    }

    public void displayDetails()
    {
        System.out.println("-------------------------------------");
        System.out.println("Account type   : " + getAccountType());   // runs the subclass version
        System.out.println("Account number : " + accountNumber);
        System.out.println("Holder name    : " + holderName);
        System.out.println("Balance        : " + formatMoney(balance));
        printTypeSpecificDetails();                                    // runs the subclass version
        System.out.println("-------------------------------------");
    }

    public static String formatMoney(double amount)
    {
        return String.format(Locale.US, "M%,.2f", amount);
    }

    // METHOD OVERRIDING
    @Override
    public String toString()
    {
        return String.format("%-9s | %-15s | %-8s | %s",
                accountNumber, holderName, getAccountType(), formatMoney(balance));
    }
}
