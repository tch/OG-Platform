/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import static com.opengamma.livedata.normalization.MarketDataRequirementNames.*;

import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;

import com.opengamma.livedata.server.FieldHistoryStore;

/**
 * Calculates a best estimate of the current value of a security.
 */
public class MarketValueCalculator implements NormalizationRule {
  
  private static final double TOLERANCE = 0.00001;
  private static final double MAX_ACCEPTABLE_SPREAD_TO_USE_MIDPOINT = 0.05;

  @Override
  public MutableFudgeFieldContainer apply(
      MutableFudgeFieldContainer msg,
      FieldHistoryStore fieldHistory) {
    
    FudgeFieldContainer lkv = fieldHistory.getLastKnownValues();
    
    Double bid = msg.getDouble(BID);
    if (bid == null) {
      bid = lkv.getDouble(BID);
    }
    
    Double ask = msg.getDouble(ASK);
    if (ask == null) {
      ask = lkv.getDouble(ASK);
    }
    
    // If we have seen bid & ask in the past, use bid & ask midpoint.
    if (bid != null && ask != null) {
      
      // Too big of a spread for midpoint to be meaningful?
      if (Math.abs(bid) > TOLERANCE && (Math.abs(ask - bid) / Math.abs(bid) > MAX_ACCEPTABLE_SPREAD_TO_USE_MIDPOINT)) {
        // Try to resort to last, though if this fails use midpoint anyway.
        Double last = lkv.getDouble(LAST);
        if (last == null) {
          last = msg.getDouble(LAST);
        }
        
        if (last != null) {
          // Ok, last was found. But let's make sure that it's within the bid/ask boundaries.
          if (last < bid) {
            msg.add(MarketDataRequirementNames.MARKET_VALUE, bid);
          } else if (last > ask) {
            msg.add(MarketDataRequirementNames.MARKET_VALUE, ask);
          } else {
            msg.add(MarketDataRequirementNames.MARKET_VALUE, last);
          }
          return msg;
        }
      }
      
      double marketValue = (bid + ask) / 2.0;
      msg.add(MarketDataRequirementNames.MARKET_VALUE, marketValue);
      return msg;
    }
    
    // Since we've not seen a bid & ask for this market data in the past,
    // try using LAST.
    Double last = msg.getDouble(LAST);
    if (last == null) {
      return lastKnownMarketValue(msg, fieldHistory);
    }
    
    msg.add(MarketDataRequirementNames.MARKET_VALUE, last);
    return msg;
  }
  
  /**
   * Tries to populate MARKET_VALUE from the history.
   */
  private MutableFudgeFieldContainer lastKnownMarketValue(
      MutableFudgeFieldContainer msg,
      FieldHistoryStore fieldHistory) {
    
    FudgeFieldContainer lkv = fieldHistory.getLastKnownValues();
    
    Double lastKnownMarketValue = lkv.getDouble(MarketDataRequirementNames.MARKET_VALUE);
    if (lastKnownMarketValue == null) {
      return msg;      
    }
    
    msg.add(MarketDataRequirementNames.MARKET_VALUE, lastKnownMarketValue);
    return msg;
  }

}