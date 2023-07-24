package com.onlinebank.controllers;

import com.onlinebank.dto.request.PaymentRequest;
import com.onlinebank.dto.response.PaymentResponse;
import com.onlinebank.models.CreditCardModel;
import com.onlinebank.models.TransactionModel;
import com.onlinebank.services.CreditCardService;
import com.onlinebank.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * @author Denis Durbalov
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final TransactionService transactionService;
    private final CreditCardService creditCardService;


    public PaymentController(TransactionService transactionService, CreditCardService creditCardService) {
        this.transactionService = transactionService;
        this.creditCardService = creditCardService;
    }

    @PostMapping
    public ResponseEntity<Object> makePayment(@RequestBody @Valid PaymentRequest paymentRequest) {
        CreditCardModel creditCardModelFrom = creditCardService.getCreditCardById(paymentRequest.getFrom_account_id());
        CreditCardModel creditCardModelTo = creditCardService.getCreditCardById(paymentRequest.getTo_account_id());
        if (creditCardModelFrom == null) {
            return ResponseEntity.badRequest().body("Credit card with id " + paymentRequest.getFrom_account_id() + " not found.");
        } else if (creditCardModelTo == null) {
            return ResponseEntity.badRequest().body("Credit card with id " + paymentRequest.getTo_account_id() + " not found.");
        }
        if (paymentRequest.getAmount() > creditCardModelFrom.getBalance() + creditCardModelFrom.getCreditLimit()) {
            return ResponseEntity.badRequest().body("Insufficient funds in the 'from' account.");
        }

        creditCardModelFrom.setBalance(creditCardModelFrom.getBalance() - paymentRequest.getAmount());
        creditCardModelTo.setBalance(creditCardModelTo.getBalance() + paymentRequest.getAmount());
        creditCardService.saveCreditCard(creditCardModelFrom);
        creditCardService.saveCreditCard(creditCardModelTo);
        TransactionModel transactionModel = paymentRequest.toTransaction(creditCardModelFrom, creditCardModelTo);
        transactionService.saveTransaction(transactionModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PaymentResponse(transactionModel));
    }


    @GetMapping("/{transaction_id}")
    public ResponseEntity<Object> gePayment(@PathVariable("transaction_id") int transactionId) {
        TransactionModel transactionModel = transactionService.getTransactionById(transactionId);
        if (transactionModel != null) {
            return ResponseEntity.ok(transactionModel);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment with id " + transactionId + " not found");
        }
    }
}
