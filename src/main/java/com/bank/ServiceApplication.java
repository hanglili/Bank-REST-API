package com.bank;

import com.bank.api.AccountAPI;
import com.bank.api.TransactionAPI;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://www.dropwizard.io/1.0.6/docs/manual/core.html#application
 */
public class ServiceApplication extends Application<ServiceConfiguration> {

    public static final String LOCAL_ENDPOINT = "http://localhost:8080/api";
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceApplication.class);

    public static void main(String[] args) throws Exception {
        new ServiceApplication().run(args);
    }

    @Override
    public void run(ServiceConfiguration configuration, Environment environment) throws Exception {
        LOGGER.info("Registering REST resources");
        environment.jersey().register(new AccountAPI());
        environment.jersey().register(new TransactionAPI());
    }

//    @Override
//    public void run(ServiceConfiguration config, Environment env) throws Exception {
//
//        // Client autotest code
////        AutoTestResource autoTestResource = new AutoTestResource(moneyTransferClient);
//
//        //register your API resource here
//        env.jersey().register(accountAPI);
//        env.jersey().register(transactionAPI);
////        env.jersey().register(autoTestResource);
//    }
}
