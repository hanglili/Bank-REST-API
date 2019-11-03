package com.bank.api;

import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;
import com.bank.exceptions.DuplicateAccountIdException;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.model.Account;
import com.bank.utils.ParamValidation;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://www.dropwizard.io/1.0.6/docs/manual/core.html#resources
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountAPI {

    private final static Logger logger = LoggerFactory.getLogger(AccountAPI.class);

    private final AccountDAO accountDAO = AccountDAOImpl.getInstance();
    private Gson gson = new Gson();

    @GET
    @Timed
    @Path("ping")
    public String ping() {
        logger.info("Pinged");
        return "Pinged";
    }

    @GET
    @Timed
    @Path("/accounts/{id}")
    public Response getAccount(@PathParam("id") String accountId) {
        logger.info("Request to get the account with id: "+ accountId);
        try {
            Account account = accountDAO.getAccount(
                    ParamValidation.validateId(accountId));
            return Response.status(Response.Status.OK).entity(gson.toJson(account)).build();
        } catch (NoSuchAccountException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Timed
    @Path("/accounts/create")
    public Response addAccount(@QueryParam("id") String id, @QueryParam("name") String name,
            @QueryParam("balance") String initialBalance) {
        logger.info("Request to add a new account with id: " + id + ", name: " + name
                + ", balance: " + initialBalance);
        try {
            accountDAO.addAccount(ParamValidation.validateId(id),
                    name,
                    ParamValidation.validateAmount(initialBalance));
            return Response.status(Response.Status.OK).build();
        } catch (BadRequestException | DuplicateAccountIdException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
