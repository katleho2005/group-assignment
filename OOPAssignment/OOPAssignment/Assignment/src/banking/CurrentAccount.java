package banking;


/**
 * INHERITANCE: CurrentAccount extends BankAccount.
 * INTERFACE IMPLEMENTATION: CurrentAccount implements Loanable.
 */
public class CurrentAccount extends BankAccount implements Loanable 
{

    public static final double OVERDRAFT_LIMIT = 2000.00;
    public static final double LOAN_LIMIT = 50000.00;

    public CurrentAccount(String accountNumber, String holderName, double openingBalance) 
    {
        super(accountNumber, holderName, openingBalance);
    }

   

    // METHOD OVERRIDING: Current may overdraw up to the overdraft limit
    @Override
    public void deposit(double amount) throws InvalidTransactionException 
    {
        validateAmount(amount, "Deposit");
        setBalance(getBalance() + amount);
    }

    @Override
    public void withdraw(double amount)
            throws InvalidTransactionException, InsufficientFundsException 
    {
        validateAmount(amount, "Withdrawal");
        if (getBalance() - amount < -OVERDRAFT_LIMIT) 
            {
            throw new InsufficientFundsException(
                    "Insufficient funds: this withdrawal would exceed the overdraft limit of "
                            + formatMoney(OVERDRAFT_LIMIT) + ".");
        }
        setBalance(getBalance() - amount);
    }

    
    @Override
    public double calculateInterest() 
    {
        return 0.0;
    }

    @Override
    public String getAccountType() 
    {
        return "Current";
    }

    @Override
    protected void printTypeSpecificDetails()
    {
        System.out.println("Overdraft limit: " + formatMoney(OVERDRAFT_LIMIT));
        System.out.println("Interest rate  : none");
        System.out.println("Loan limit     : " + formatMoney(LOAN_LIMIT));
    }


    @Override
    public double getLoanLimit() 
    {
        return LOAN_LIMIT;
    }

    @Override
    public void requestLoan(double amount) throws InvalidTransactionException 
    {
        validateAmount(amount, "Loan");
        if (amount > getLoanLimit()) {
            throw new InvalidTransactionException("Loan rejected: the requested amount exceeds the loan limit of "
                    + formatMoney(getLoanLimit()) + " for this current account.");
        }
        setBalance(getBalance() + amount);
    }
}
