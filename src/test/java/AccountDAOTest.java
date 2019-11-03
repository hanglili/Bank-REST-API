import static org.testng.Assert.assertThrows;

import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;
import com.bank.exceptions.DuplicateAccountIdException;
import com.bank.exceptions.InsufficientBalanceException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.model.Account;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccountDAOTest {

    private static AccountDAO accountDAO;
    private static String name = "Bob";

    @BeforeClass
    public static void setUp(){
        accountDAO = AccountDAOImpl.getInstance();
    }

    @AfterClass
    public static void tearDown(){
        accountDAO = null;
    }

    @Test
    public void testCreateAndGetAccount() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 7L;
        double balance = 1000D;
        Account account = accountDAO.addAccount(accountId, name, balance);
        Assert.assertEquals(account, accountDAO.getAccount(accountId));
    }

    @Test
    public void testCreateAccount_DuplicateAccountIdException() throws DuplicateAccountIdException {
        long accountId = 8L;
        accountDAO.addAccount(accountId, name,0D);
        assertThrows(DuplicateAccountIdException.class, () -> accountDAO
                .addAccount(accountId, name, 0D));
    }

    @Test
    public void testGetAccount_NoSuchAccountException(){
        long accountId = 12345L;
        assertThrows(NoSuchAccountException.class, () -> accountDAO.getAccount(accountId));
    }

    @Test
    public void testWithdraw() throws DuplicateAccountIdException, InsufficientBalanceException, NoSuchAccountException {
        long accountId = 9L;
        double balance = 1000D;
        double withdrawnAmount = 200D;
        accountDAO.addAccount(accountId,name, balance);
        accountDAO.withdraw(accountId,withdrawnAmount);
        Assert.assertEquals((balance-withdrawnAmount),
                accountDAO.getAccount(accountId).getBalance(),0);
    }

    @Test
    public void testWithdraw_NoSuchAccountException() {
        long accountId = 10L;
        double withdrawnAmount = 200D;
        assertThrows(NoSuchAccountException.class, () -> accountDAO
                .withdraw(accountId,withdrawnAmount));
    }

    @Test
    public void testWithdraw_InsufficientBalanceException() throws DuplicateAccountIdException {
        long accountId = 11L;
        double balance = 1000D;
        double withdrawnAmount = 2000D;
        accountDAO.addAccount(accountId,name, balance);
        assertThrows(InsufficientBalanceException.class, () -> accountDAO
                .withdraw(accountId,withdrawnAmount));
    }

    @Test
    public void testDeposit() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 12L;
        double balance = 1000D;
        double depositedAmount = 200D;
        accountDAO.addAccount(accountId, name, balance);
        accountDAO.deposit(accountId,depositedAmount);
        Assert.assertEquals((balance+depositedAmount),
                accountDAO.getAccount(accountId).getBalance(),0);
    }

    @Test
    public void testDeposit_NoSuchAccountException(){
        long accountId = 13L;
        double depositedAmount = 200D;
        assertThrows(NoSuchAccountException.class, () -> accountDAO
                .deposit(accountId,depositedAmount));
    }

    @Test
    public void testGetCurrentBalance() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 20L;
        double initialBalance = 1000D;
        accountDAO.addAccount(accountId,name, initialBalance);
        Assert.assertEquals(initialBalance,
                accountDAO.getAccount(accountId).getBalance(),0);
    }

    @Test
    public void testGetCurrentBalance_NoSuchAccountException(){
        long accountId = 21L;
        assertThrows(NoSuchAccountException.class, () ->
                accountDAO.getAccount(accountId).getBalance());
    }

}
