import static com.bank.utils.Constants.*;
import static com.bank.utils.Constants.INSUFFICIENT_BALANCE;
import static com.bank.utils.Constants.INVALID_ID;
import static com.bank.utils.Constants.NEGATIVE_AMOUNT;
import static com.bank.utils.Constants.NO_SUCH_TRANSACTION;
import static com.bank.utils.Constants.TRANSACTION_OPERATIONS_PATH;

import com.bank.ServiceApplication;
import com.bank.ServiceConfiguration;
import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;
import com.bank.exceptions.DuplicateAccountIdException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.*;

public class TransactionAPITest {

    private static AccountDAO accountDAO;
    private static String name1 = "Bob";
    private static String name2 = "Alice";
    private static Gson gson;

    @ClassRule
    public static final DropwizardAppRule<ServiceConfiguration> RULE =
            new DropwizardAppRule<>(ServiceApplication.class,
                    ResourceHelpers.resourceFilePath("configuration.yml"));
//    @ClassRule
//    public static final ServerExternalResource externalResource = new ServerExternalResource();

    @BeforeClass
    public static void setUp() throws Exception {
        accountDAO = AccountDAOImpl.getInstance();
        gson  = new Gson();
    }
    @AfterClass
    public static void tearDown() throws Exception {
        accountDAO = null;
        gson = null;
    }

    @Test
    public void testTransaction() throws IOException, DuplicateAccountIdException, NoSuchAccountException {
        Long accountId1 = 19L;
        Double initialAmount1 = 1000D;
        Long accountId2 = 28L;
        Double initialAmount2 = 1000D;
        Double transferAmount = 500D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);
        accountDAO.addAccount(accountId2, name2, initialAmount2);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Assert.assertEquals((initialAmount1 - transferAmount),
                accountDAO.getAccount(accountId1).getBalance(), 0);
        Assert.assertEquals((initialAmount2 + transferAmount),
                accountDAO.getAccount(accountId2).getBalance(), 0);
    }

    @Test
    public void testTransaction_InsufficientBalance() throws DuplicateAccountIdException, IOException {
        Long accountId1 = 21L;
        Double initialAmount1 = 1000D;
        Long accountId2 = 22L;
        Double initialAmount2 = 1000D;
        Double transferAmount = 2000D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);
        accountDAO.addAccount(accountId2, name2, initialAmount2);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(INSUFFICIENT_BALANCE + accountId1.toString(),EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransaction_InvalidTransferAmount() throws DuplicateAccountIdException, IOException {
        Long accountId1 = 23L;
        Double initialAmount1 = 1000D;
        Long accountId2 = 24L;
        Double initialAmount2 = 1000D;
        Double transferAmount = -100D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);
        accountDAO.addAccount(accountId2, name2, initialAmount2);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(NEGATIVE_AMOUNT,EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransaction_NoSuchAccount() throws DuplicateAccountIdException, IOException {
        Long accountId1 = 25L;
        Double initialAmount1 = 1000D;
        Long accountId2 = 26L;
        Double transferAmount = -100D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(NEGATIVE_AMOUNT,EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransaction_InvalidID() throws DuplicateAccountIdException, IOException {
        Long accountId1 = 27L;
        Double initialAmount1 = 1000D;
        Long accountId2 = -10L;
        Double transferAmount = -100D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(INVALID_ID + accountId2,EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testGetTransaction() throws IOException, DuplicateAccountIdException {
        Long accountId1 = 40L;
        Double initialAmount1 = 1000D;
        Long accountId2 = 41L;
        Double initialAmount2 = 1000D;
        Double transferAmount = 100D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);
        accountDAO.addAccount(accountId2, name2, initialAmount2);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
        Long transactionId = gson.fromJson(EntityUtils.toString(response.getEntity()), Long.class);

        HttpUriRequest getRequest = new HttpGet(TRANSACTION_OPERATIONS_PATH + transactionId);
        HttpResponse getResponse = HttpClientBuilder.create().build().execute(getRequest);

        Assert.assertEquals(HttpStatus.SC_OK, getResponse.getStatusLine().getStatusCode());

        Transaction transaction = gson.fromJson(EntityUtils.toString(getResponse.getEntity()), Transaction.class);
        Assert.assertEquals(transactionId.longValue(), transaction.getId());
        Assert.assertEquals(accountId1.longValue(), transaction.getSourceAccountId());
        Assert.assertEquals(accountId2.longValue(), transaction.getDestinationAccountId());
        Assert.assertEquals(transferAmount, transaction.getAmount(), 0);
    }

    @Test
    public void testGetTransaction_NoSuchTransaction() throws IOException {
        long transactionId = 123456789L;
        HttpUriRequest request = new HttpGet(TRANSACTION_OPERATIONS_PATH + transactionId);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
        Assert.assertEquals(NO_SUCH_TRANSACTION + transactionId, EntityUtils.toString(response.getEntity()));
    }


    @Test
    public void testGetTransactionForOneAccount() throws IOException, DuplicateAccountIdException {
        Long accountId1 = 400L;
        Double initialAmount1 = 1000D;
        Long accountId2 = 401L;
        Double initialAmount2 = 1000D;
        Double transferAmount = 100D;

        accountDAO.addAccount(accountId1, name1, initialAmount1);
        accountDAO.addAccount(accountId2, name2, initialAmount2);

        HttpUriRequest request = new HttpPost(TRANSACTION_OPERATIONS_PATH + "create" +
                "?from=" + accountId1.toString() +
                "&to=" + accountId2.toString() +
                "&amount=" + transferAmount.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
        Long transactionId = gson.fromJson(EntityUtils.toString(response.getEntity()), Long.class);

        HttpUriRequest getRequest = new HttpGet(TRANSACTION_OPERATIONS_PATH + "account"
            + "?id=" + 400);
        HttpResponse getResponse = HttpClientBuilder.create().build().execute(getRequest);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        List<Transaction> transactions = gson.fromJson(
                EntityUtils.toString(getResponse.getEntity()),
                new TypeToken<List<Transaction>>(){}.getType());
        Assert.assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        Assert.assertEquals(transactionId.longValue(), transaction.getId());
        Assert.assertEquals(accountId1.longValue(), transaction.getSourceAccountId());
        Assert.assertEquals(accountId2.longValue(), transaction.getDestinationAccountId());
        Assert.assertEquals(transferAmount, transaction.getAmount(), 0);
    }

}