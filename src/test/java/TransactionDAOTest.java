import static org.testng.Assert.assertThrows;

import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;
import com.bank.dao.TransactionDAO;
import com.bank.dao.TransactionDAOImpl;
import com.bank.exceptions.DuplicateAccountIdException;
import com.bank.exceptions.InsufficientBalanceException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.exceptions.NoSuchTransactionException;
import com.bank.exceptions.TransactionOnSameAccountException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionDAOTest {

    private static TransactionDAO transactionDAO;
    private static AccountDAO accountDAO;
    private static Account senderAccount, recipientAccount;
    private static String name1 = "Bob";
    private static String name2 = "Alice";
    private static long transactionId = 1;

    @BeforeClass
    public static void setUp(){
        transactionDAO = TransactionDAOImpl.getInstance();
        accountDAO = AccountDAOImpl.getInstance();
    }

    @AfterClass
    public static void tearDown(){
        transactionDAO = null;
        accountDAO = null;
    }

    @Test
    public void testProcessTransaction()
            throws InsufficientBalanceException, NoSuchAccountException, DuplicateAccountIdException, TransactionOnSameAccountException {
        long senderAccountId = 2000L;
        long recipientAccountId = 2001L;
        double initialBalance = 1000D;
        double transactionAmount = 500D;

        accountDAO.addAccount(senderAccountId, name1, initialBalance);
        accountDAO.addAccount(recipientAccountId, name2, initialBalance);
        Transaction transaction = new Transaction(transactionId++, senderAccountId,
                recipientAccountId,transactionAmount);

        transactionDAO.processTransaction(transaction);
        Assert.assertEquals((initialBalance-transactionAmount),
                accountDAO.getAccount(senderAccountId).getBalance(), 0);
        Assert.assertEquals((initialBalance+transactionAmount),
                accountDAO.getAccount(recipientAccountId).getBalance(), 0);
    }

    @Test
    public void testProcessTransaction_NoSuchAccountException_1() throws DuplicateAccountIdException {
        long senderAccountId = 2002L;
        long recipientAccountId = 2003L;

        accountDAO.addAccount(senderAccountId, name1, 1000D);

        Transaction transaction = new Transaction(transactionId++, senderAccountId, recipientAccountId,500D);
        assertThrows(NoSuchAccountException.class, () -> transactionDAO.processTransaction(transaction));
    }

    @Test
    public void testProcessTransaction_NoSuchAccountException_2() throws DuplicateAccountIdException {
        long senderAccountId = 2004L;
        long recipientAccountId = 2005L;

        accountDAO.addAccount(recipientAccountId, name1, 1000D);

        Transaction transaction = new Transaction(transactionId++, senderAccountId, recipientAccountId,500D);
        assertThrows(NoSuchAccountException.class, () -> transactionDAO.processTransaction(transaction));
    }

    @Test
    public void testProcessTransaction_InsufficientBalanceException() throws DuplicateAccountIdException, NoSuchAccountException {
        long senderAccountId = 2007L;
        long recipientAccountId = 2006L;
        double initialBalance = 1000D;
        double transactionAmount = 2000D;

        senderAccount = accountDAO.addAccount(senderAccountId, name1, initialBalance);
        recipientAccount = accountDAO.addAccount(recipientAccountId, name2, initialBalance);
        Transaction transaction = new Transaction(transactionId++, senderAccountId, recipientAccountId,transactionAmount);
        assertThrows(InsufficientBalanceException.class, () -> transactionDAO.processTransaction(transaction));
    }

    @Test
    public void testGetTransaction()
            throws DuplicateAccountIdException, NoSuchAccountException, NoSuchTransactionException, InsufficientBalanceException, TransactionOnSameAccountException {
        long senderAccountId = 3000L;
        long recipientAccountId = 4000L;
        double initialBalance = 2000D;
        double transactionAmount = 500D;

        accountDAO.addAccount(senderAccountId, name1, initialBalance);
        accountDAO.addAccount(recipientAccountId, name2, initialBalance);
        Transaction transaction = new Transaction(transactionId++, senderAccountId,
                recipientAccountId,transactionAmount);
        transactionDAO.processTransaction(transaction);

        Transaction getTransaction = transactionDAO.get(transaction.getId());
        Assert.assertEquals(getTransaction.getId(), transaction.getId());
        Assert.assertEquals(getTransaction.getDestinationAccountId(), transaction.getDestinationAccountId());
        Assert.assertEquals(getTransaction.getSourceAccountId(), transaction.getSourceAccountId());
        Assert.assertEquals(getTransaction.getAmount(), transaction.getAmount(), 0);
    }

    @Test
    public void testGetTransaction_NoSuchTransactionException(){
        long transactionId = 123456L;
        assertThrows(NoSuchTransactionException.class, () ->
                transactionDAO.get(transactionId));
    }

    @Test
    public void testGetTransactionsForOneAccount()
            throws DuplicateAccountIdException, NoSuchAccountException, NoSuchTransactionException, InsufficientBalanceException, TransactionOnSameAccountException {
        long senderAccountId = 5000L;
        long recipientAccountId = 6000L;
        double initialBalance = 2000D;
        double transactionAmount = 500D;

        accountDAO.addAccount(senderAccountId, name1, initialBalance);
        accountDAO.addAccount(recipientAccountId, name2, initialBalance);
        Transaction transaction = new Transaction(transactionId++, senderAccountId,
                recipientAccountId,transactionAmount);
        transactionDAO.processTransaction(transaction);

        List<Transaction> getTransactions = transactionDAO.getAllTransactionsForAccount(senderAccountId);
        Assert.assertEquals(getTransactions.size(), 1);
        Transaction getTransaction = getTransactions.get(0);
        Assert.assertEquals(getTransaction.getId(), transaction.getId());
        Assert.assertEquals(getTransaction.getDestinationAccountId(), transaction.getDestinationAccountId());
        Assert.assertEquals(getTransaction.getSourceAccountId(), transaction.getSourceAccountId());
        Assert.assertEquals(getTransaction.getAmount(), transaction.getAmount(), 0);
    }

    @Test
    public void testGetTransactionsForOneAccount_NoSuchAccountException(){
        long accountId = 123456789L;
        assertThrows(NoSuchAccountException.class, () ->
                transactionDAO.getAllTransactionsForAccount(accountId));
    }

}
