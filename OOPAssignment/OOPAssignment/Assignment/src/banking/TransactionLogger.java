package banking;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** FILE HANDLING: appends timestamped records to transactions.txt. FINAL: not meant to be extended. */
public final class TransactionLogger 
{

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String fileName;

    public TransactionLogger(String fileName) 
    {
        this.fileName = fileName;
    }

    public void log(String accountNumber, String type, Double amount, Double balance, String result) {
        String line = String.format("%s | Account: %s | %s | Amount: %s | Balance: %s | %s",
                LocalDateTime.now().format(TIME_FORMAT),
                accountNumber,
                type,
                amount == null ? "N/A" : String.format(Locale.US, "M%.2f", amount),
                balance == null ? "N/A" : String.format(Locale.US, "M%.2f", balance),
                result);


        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true)))
         {
            out.println(line);
        } catch (IOException e) 
        {
            System.out.println("Warning: could not write to " + fileName + " (" + e.getMessage() + ")");
        }
    }
}
