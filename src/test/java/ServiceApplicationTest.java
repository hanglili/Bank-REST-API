import static org.assertj.core.api.Assertions.assertThat;

import com.bank.ServiceApplication;
import com.bank.ServiceConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;

public class ServiceApplicationTest {

    @ClassRule
    public static final DropwizardAppRule<ServiceConfiguration> RULE =
            new DropwizardAppRule<>(ServiceApplication.class,
                    ResourceHelpers.resourceFilePath("configuration.yml"));

    @Test
    public void appStartsUp() {
        assertThat(RULE.getConfiguration()).isNotNull();
        assertThat(RULE.getEnvironment()).isNotNull();

        Client client = new JerseyClientBuilder(RULE.getEnvironment())
                .using(RULE.getConfiguration().getJerseyClientConfiguration()).build("client");

        Response response = client.target("http://localhost:" + RULE.getLocalPort() + "/api").request().get();
        assertThat(response.getStatus()).isEqualTo(404);
    }

}
