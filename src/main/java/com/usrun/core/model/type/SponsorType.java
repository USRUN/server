/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.model.type;

import com.fasterxml.jackson.annotation.JsonValue;
import com.usrun.core.model.Sponsor;
import java.util.HashMap;

/**
 *
 * @author huyna3
 */
public enum SponsorType {
    POWERED(1),
    GOLD_SPONSOR(2),
    SILVER_SPONSOR(3),
    BRONZE_SPONSOR(4),
    COLLABORATED(5);

    private int value;

    SponsorType(int value) {
        this.value = value;
    }

    public static SponsorType getSponsor(int value) {
        SponsorType result = null;
        switch (value) {
            case 1:
                result = SponsorType.POWERED;
                break;
            case 2:
                result = SponsorType.GOLD_SPONSOR;
                break;
            case 3:
                result = SponsorType.SILVER_SPONSOR;
                break;
            case 4:
                result = SponsorType.BRONZE_SPONSOR;
                break;
            case 5:
                result = SponsorType.COLLABORATED;
                break;
        }
        return result;
    }

    @JsonValue
    public int toValue() {
        return this.value;
    }
}
