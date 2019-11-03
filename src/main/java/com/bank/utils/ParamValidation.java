package com.bank.utils;

import static com.bank.utils.Constants.INVALID_AMOUNT;
import static com.bank.utils.Constants.INVALID_ID;
import static com.bank.utils.Constants.NEGATIVE_AMOUNT;
import static com.bank.utils.Constants.NULL_OR_EMPTY_ID;

import javax.ws.rs.BadRequestException;

public class ParamValidation {

    public static long validateId(String id) throws BadRequestException{
        long parsedId;
        if (id == null || id.isEmpty()){
            throw new BadRequestException(INVALID_ID + NULL_OR_EMPTY_ID);
        } else {
            try {
                parsedId = Long.parseLong(id);
            } catch (Exception e){
                throw new BadRequestException(INVALID_ID + id);
            }
        }
        if (parsedId < 0){
            throw new BadRequestException(INVALID_ID + id);
        }
        return parsedId;
    }

    public static double validateAmount(String amount) throws BadRequestException{
        double parsedInitialAmount;
        if (amount == null || amount.isEmpty()){
            throw new BadRequestException(INVALID_AMOUNT + NULL_OR_EMPTY_ID);
        } else {
            try {
                parsedInitialAmount = Double.parseDouble(amount);
            } catch (Exception e){
                throw new BadRequestException(INVALID_AMOUNT + amount);
            }
        }
        if (parsedInitialAmount < 0){
            throw new BadRequestException(NEGATIVE_AMOUNT);
        }
        return parsedInitialAmount;
    }
}
