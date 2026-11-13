package banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Manages the accounts. FINAL: this class is not intended to be extended. */
public final class Bank 
{

    private final List<BankAccount> accounts = new ArrayList<>();   
    private final TransactionLogger logger;

    public Bank(TransactionLogger logger) 
    {
        this.logger = logger;
    }

    public void addAccount(BankAccount account) 
    {
        accounts.add(account);
    }

    public List<BankAccount> getAccounts() 
    {
        return Collections.unmodifiableList(accounts);   
    }

    public BankAccount findAccount(String accountNumber) throws InvalidTransactionException 
    {
        String wanted = accountNumber == null ? "" : accountNumber.trim();
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(wanted)) 
            {
                return account;
            }
        }
        throw new InvalidTransactionException("Account '" + wanted + "' was not found.");
    }

    private BankAccount lookup(String accountNumber, String type, double amount)
            throws InvalidTransactionException {
        try {
            return findAccount(accountNumber);
        } catch (InvalidTransactionException e) {
            logger.log(accountNumber == null ? "UNKNOWN" : accountNumber.trim().toUpperCase(), type, amount, null, "REJECTED: " + e.getMessage());
            throw e;
        }
    }

    public void deposit(String accountNumber, double amount) throws InvalidTransactionException {
        BankAccount account = lookup(accountNumber, "DEPOSIT", amount);
        try {
            account.deposit(amount);   // POLYMORPHISM: runs the Savings or Current version
            logger.log(account.getAccountNumber(), "DEPOSIT", amount, account.getBalance(), "SUCCESS");
        } catch (InvalidTransactionException e) 
        {
            logger.log(account.getAccountNumber(), "DEPOSIT", amount, account.getBalance(),
                    "REJECTED: " + e.getMessage());
            throw e;
        }
    }

    public void withdraw(String accountNumber, double amount)
            throws InvalidTransactionException, InsufficientFundsException
             {
        BankAccount account = lookup(accountNumber, "WITHDRAW", amount);
        try {
            account.withdraw(amount);   // POLYMORPHISM
            logger.log(account.getAccountNumber(), "WITHDRAW", amount, account.getBalance(), "SUCCESS");
        } catch (InvalidTransactionException | InsufficientFundsException e) {
            logger.log(account.getAccountNumber(), "WITHDRAW", amount, account.getBalance(),
                    "REJECTED: " + e.getMessage());
            throw e;
        }
    }

    public void transfer(String fromNumber, String toNumber, double amount)
            throws InvalidTransactionException, InsufficientFundsException 
            {
        BankAccount from = lookup(fromNumber, "TRANSFER_OUT", amount);
        BankAccount to = lookup(toNumber, "TRANSFER_IN", amount);
        try {
            if (from == to) {
                throw new InvalidTransactionException("Cannot transfer to the same account.");
            }
            from.withdraw(amount);   
            to.deposit(amount);
            logger.log(from.getAccountNumber(), "TRANSFER_OUT", amount, from.getBalance(),
                    "SUCCESS: sent to " + to.getAccountNumber());
            logger.log(to.getAccountNumber(), "TRANSFER_IN", amount, to.getBalance(),
                    "SUCCESS: received from " + from.getAccountNumber());
        } catch (InvalidTransactionException | InsufficientFundsException e) {
            logger.log(from.getAccountNumber(), "TRANSFER_OUT", amount, from.getBalance(),
                    "REJECTED: " + e.getMessage());
            throw e;
        }
    }

    
    public double calculateInterest(String accountNumber) throws InvalidTransactionException {
        BankAccount account = lookup(accountNumber, "INTEREST", 0);
        double interest = account.calculateInterest();   // POLYMORPHISM
        logger.log(account.getAccountNumber(), "INTEREST", interest, account.getBalance(), "SUCCESS");
        return interest;
    }

    public void requestLoan(String accountNumber, double amount) throws InvalidTransactionException {
        BankAccount account = lookup(accountNumber, "LOAN", amount);
        try {
            if (!(account instanceof Loanable)) 
            {
                throw new InvalidTransactionException("This account type cannot request a loan.");
            }
            Loanable loanable = (Loanable) account;   // INTERFACE reference
            loanable.requestLoan(amount);
            logger.log(account.getAccountNumber(), "LOAN", amount, account.getBalance(), "SUCCESS");
        } catch (InvalidTransactionException e) {
            logger.log(account.getAccountNumber(), "LOAN", amount, account.getBalance(),
                    "REJECTED: " + e.getMessage());
            throw e;
        }
    }
}
