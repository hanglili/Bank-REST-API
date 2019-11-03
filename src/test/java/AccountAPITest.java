import static com.bank.utils.Constants.*;
import static com.bank.utils.Constants.ACCOUNT_ID_ALREADY_EXISTS;
import static com.bank.utils.Constants.ACCOUNT_OPERATIONS_PATH;
import static com.bank.utils.Constants.INVALID_ID;
import static com.bank.utils.Constants.NEGATIVE_AMOUNT;
import static com.bank.utils.Constants.NO_SUCH_ACCOUNT;
import static com.bank.utils.Constants.NULL_OR_EMPTY_ID;

import com.bank.ServiceApplication;
import com.bank.ServiceConfiguration;
import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;
import com.bank.model.Account;
import com.google.gson.Gson;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.*;

public class AccountAPITest {

    private static AccountDAO accountDAO;
    private static Gson gson;
    private static String name = "Bob";

    @ClassRule
    public static final DropwizardAppRule<ServiceConfiguration> RULE =
            new DropwizardAppRule<>(ServiceApplication.class,
                    ResourceHelpers.resourceFilePath("configuration.yml"));

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
    public void testCreateAccount() throws IOException {
        HttpUriRequest request = new HttpPost(ACCOUNT_OPERATIONS_PATH + "create"
                + "?id=14"
                + "&name=" + name
                + "&balance=" + 10.0);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testCreateAccount_DuplicateAccountId() throws IOException {
        Long accountId = 15L;
        HttpUriRequest request = new HttpPost(ACCOUNT_OPERATIONS_PATH + "create"
                + "?id=" + accountId
                + "&name=" + name
                + "&balance=" + 10.0);
        HttpClientBuilder.create().build().execute(request);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(ACCOUNT_ID_ALREADY_EXISTS + accountId.toString(),
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testCreateAccount_NegativeInitialAmount() throws IOException {
        HttpUriRequest request = new HttpPost(ACCOUNT_OPERATIONS_PATH + "create"
                + "?id=16"
                + "&name=" + name
                + "&balance=" + -15.0);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(NEGATIVE_AMOUNT, EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testCreateAccount_InvalidID() throws IOException {
        Long defectiveId = -1L;
        HttpUriRequest request = new HttpPost(ACCOUNT_OPERATIONS_PATH + "create"
                + "?id=-1"
                + "&name=" + name
                + "&balance=" + 10.0);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(INVALID_ID + defectiveId.toString(),
                EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testCreateAccount_InvalidID_2() throws IOException {
        HttpUriRequest request = new HttpPost(ACCOUNT_OPERATIONS_PATH + "create"
                + "?id="
                + "&name=" + name
                + "&balance=" + 10.0);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST,response.getStatusLine().getStatusCode());
        Assert.assertEquals(INVALID_ID + NULL_OR_EMPTY_ID, EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testGetAccount() throws IOException {
        Long accountId = 17L;
        Double initialAmount = 1000D;
        Account account = new Account(accountId, name, initialAmount);

        HttpUriRequest request = new HttpPost(ACCOUNT_OPERATIONS_PATH + "create"
                + "?id=" + accountId
                + "&name=" + name
                + "&balance=" + initialAmount);
        HttpClientBuilder.create().build().execute(request);
        HttpUriRequest request2 = new HttpGet(ACCOUNT_OPERATIONS_PATH + accountId.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request2);

        Assert.assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());

        Account receivedAccount = gson.fromJson(EntityUtils.toString(response.getEntity()),Account.class);
        Assert.assertEquals(account.getId(), receivedAccount.getId());
        Assert.assertEquals(account.getName(), receivedAccount.getName());
        Assert.assertEquals(account.getBalance(), receivedAccount.getBalance(), 0);
    }

    @Test
    public void testGetAccount_NoSuchAccount() throws IOException {
        Long accountId = 18L;
        HttpUriRequest request = new HttpGet(ACCOUNT_OPERATIONS_PATH + accountId.toString());
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatusLine().getStatusCode());
        Assert.assertEquals(NO_SUCH_ACCOUNT + accountId.toString(),EntityUtils.toString(response.getEntity()));
    }

}
