package com.tj.util.enums.otc;

/**
 * Created by ldh on 2018-03-08.
 */
public enum CoinChangeType {
    subSellerOnCreateTrade,
    rollBackSellerOnCancel,
    rollBackSellerOnDelAdv,
    addBuyerOnAppealWin,
    rollBackSellerOnAppealWin,
    subOnPubSellAdv,
    addBuyerOnTradeSuc,
    subOnInGold,
    addExchangeOnInGold,
    addOnOutGold,
    subExchangeOnOutGold
}
