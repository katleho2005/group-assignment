package banking;

/**
 * INHERITANCE: SavingsAccount extends BankAccount.
 * INTERFACE IMPLEMENTATION: SavingsAccount implements Loanable.
 */
public class SavingsAccount extends BankAccount implements Loanable
{
    public static final double MINIMUM_BALANCE = 100.00;
    public static final double INTEREST_RATE = 0.05;     // 5% annual
    public static final double LOAN_LIMIT = 5000.00;

    public SavingsAccount(String accountNumber, String holderName, double openingBalance)
    {
        super(accountNumber, holderName, openingBalance);
    }

    // METHOD OVERRIDING: Savings must keep a minimum balance after withdrawal
    @Override
    public void deposit(double amount) throws InvalidTransactionException
    {
        validateAmount(amount, "Deposit");
        setBalance(getBalance() + amount);
    }

    @Override
    public void withdraw(double amount) throws InvalidTransactionException, InsufficientFundsException
    {
        validateAmount(amount, "Withdrawal");
        if (getBalance() - amount < MINIMUM_BALANCE)
        {
            throw new InsufficientFundsException(
                    "Insufficient funds: a savings account must keep a minimum balance of "
                            + formatMoney(MINIMUM_BALANCE) + " after withdrawal.");
        }
        setBalance(getBalance() - amount);
    }

    // METHOD OVERRIDING: Savings earns 5% interest (Current earns none)
    @Override
    public double calculateInterest()
    {
        double interest = getBalance() > 0 ? getBalance() * INTEREST_RATE : 0.0;
        setBalance(getBalance() + interest);
        return interest;
    }

    @Override
    public String getAccountType()
    {
        return "Savings";
    }

    @Override
    protected void printTypeSpecificDetails()
    {
        System.out.println("Minimum balance: " + formatMoney(MINIMUM_BALANCE));
        System.out.println("Interest rate  : " + (int) (INTEREST_RATE * 100) + "% per year");
        System.out.println("Loan limit     : " + formatMoney(LOAN_LIMIT));
    }

    // ---- Loanable (interface implementation) ----
    @Override
    public double getLoanLimit()
    {
        return LOAN_LIMIT;
    }

    @Override
    public void requestLoan(double amount) throws InvalidTransactionException
    {
        validateAmount(amount, "Loan");
        if (amount > getLoanLimit())
        {
            throw new InvalidTransactionException("Loan rejected: the requested amount exceeds the loan limit of "
                    + formatMoney(getLoanLimit()) + " for a savings account.");
        }
        setBalance(getBalance() + amount);
    }
}
