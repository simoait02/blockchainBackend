package com.aseds.aithssainesbaiti.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @JsonProperty("sender")
    private int senderId;
    @JsonProperty("recipient")
    private int recipientId;
    @JsonProperty("amount")
    private double amount;

    public Transaction(int senderId, int recipientId, double amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public double getAmount() {
        return amount;
    }


    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Block to JSON", e);
        }
    }


}
