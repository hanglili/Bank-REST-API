package com.bank.api;

import com.bank.dao.TransactionDAO;
import com.bank.dao.TransactionDAOImpl;
import com.bank.exceptions.NoSuchAccountException;
import com.bank.exceptions.NoSuchTransactionException;
import com.bank.model.Transaction;
import com.bank.utils.ParamValidation;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import javax.validation.constraints.NotNull;
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

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionAPI {

    private final static Logger logger = LoggerFactory.getLogger(TransactionAPI.class);

    private final TransactionDAO transactionDAO = TransactionDAOImpl.getInstance();
    private Gson gson = new Gson();

    @POST
    @Timed
    @Path("/transactions/create")
    public Response transfer(@QueryParam("id") String id, @QueryParam("from") String senderId,
            @QueryParam("to") String recipientId, @QueryParam("amount") String amount)  {
        try {
            logger.info("Request to transfer " + amount + " from accountId: " + senderId +
                    " to accountId: " + recipientId);
            transactionDAO.processTransaction(new Transaction(
                    ParamValidation.validateId(id),
                    ParamValidation.validateId(senderId),
                    ParamValidation.validateId(recipientId),
                    ParamValidation.validateAmount(amount)));
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Timed
    @Path("/transactions/{id}")
    public Response getTransaction(@PathParam("id") @NotNull String transactionId) {
        try {
            logger.info("Request to get transaction with id: " + transactionId);
            Transaction transaction = transactionDAO.get(
                    ParamValidation.validateId(transactionId));
            return Response.status(Response.Status.OK).entity(gson.toJson(transaction)).build();
        } catch (NoSuchTransactionException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @GET
    @Timed
    @Path("/transactions/account")
    public Response getAllTransferForAccount(@QueryParam("id") String accountId) {
        logger.info("Request to get all the transactions related to an account with id: " + accountId);
        try {
            return Response.status(Response.Status.OK)
                    .entity(gson.toJson(
                            transactionDAO.getAllTransactionsForAccount(
                                    ParamValidation.validateId(accountId))))
                    .build();
        } catch (NoSuchAccountException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

}
