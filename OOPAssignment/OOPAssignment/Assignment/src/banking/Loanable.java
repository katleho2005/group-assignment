package banking;

/** INTERFACE: contract for any account that can request a bank loan. */
public interface Loanable
{
    double getLoanLimit();

    void requestLoan(double amount) throws InvalidTransactionException;
}
