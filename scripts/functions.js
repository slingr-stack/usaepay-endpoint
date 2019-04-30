/////////////////////
// Public API
/////////////////////

// transactions

endpoint.runTransaction = function (params) {
    return endpoint._runTransaction(params);
};

endpoint.runTransactionAPI = function (params) {
    return endpoint._runTransactionAPI(params);
};

endpoint.runSale = function (params) {
    return endpoint._runSale(params);
};

endpoint.runAuthOnly = function (params) {
    return endpoint._runAuthOnly(params);
};

endpoint.runCredit = function (params) {
    return endpoint._runCredit(params);
};

endpoint.runCheckSale = function (params) {
    return endpoint._runCheckSale(params);
};

endpoint.runCheckCredit = function (params) {
    return endpoint._runCheckCredit(params);
};

endpoint.runQuickSale = function (params) {
    return endpoint._runQuickSale(params);
};

endpoint.runQuickCredit = function (params) {
    return endpoint._runQuickCredit(params);
};

endpoint.postAuth = function (params) {
    return endpoint._postAuth(params);
};

endpoint.captureTransaction = function (params) {
    return endpoint._captureTransaction(params);
};

endpoint.refundTransaction = function (params) {
    return endpoint._refundTransaction(params);
};

endpoint.overrideTransaction = function (params) {
    return endpoint._overrideTransaction(params);
};

endpoint.voidTransaction = function (params) {
    return endpoint._voidTransaction(params);
};

endpoint.getTransaction = function (params) {
    return endpoint._getTransaction(params);
};

endpoint.getTransactionStatus = function (params) {
    return endpoint._getTransactionStatus(params);
};

endpoint.getTransactionCustom = function (params) {
    return endpoint._getTransactionCustom(params);
};

endpoint.getTransactionProfile = function (params) {
    return endpoint._getTransactionProfile(params);
};

endpoint.getCheckTrace = function (params) {
    return endpoint._getCheckTrace(params);
};

endpoint.searchTransactions = function (params) {
    return endpoint._searchTransactions(params);
};

endpoint.searchTransactionsCount = function (params) {
    return endpoint._searchTransactionsCount(params);
};

endpoint.searchTransactionsCustom = function (params) {
    return endpoint._searchTransactionsCustom(params);
};

endpoint.getTransactionReport = function (params) {
    return endpoint._getTransactionReport(params);
};

endpoint.convertTranToCust = function (params) {
    return endpoint._convertTranToCust(params);
};

endpoint.getSession = function (params) {
    return endpoint._getSession(params);
};

// customers

endpoint.runCustomerTransaction = function (params) {
    return endpoint._runCustomerTransaction(params);
};

endpoint.enableCustomer = function (params) {
    return endpoint._enableCustomer(params);
};

endpoint.disableCustomer = function (params) {
    return endpoint._disableCustomer(params);
};

endpoint.deleteCustomer = function (params) {
    return endpoint._deleteCustomer(params);
};

endpoint.searchCustomerID = function (params) {
    return endpoint._searchCustomerID(params);
};

endpoint.getCustomer = function (params) {
    return endpoint._getCustomer(params);
};

endpoint.searchCustomers = function (params) {
    return endpoint._searchCustomers(params);
};

endpoint.searchCustomersCount = function (params) {
    return endpoint._searchCustomersCount(params);
};

endpoint.searchCustomersCustom = function (params) {
    return endpoint._searchCustomersCustom(params);
};

endpoint.getCustomerHistory = function (params) {
    return endpoint._getCustomerHistory(params);
};

endpoint.addCustomer = function (params) {
    return endpoint._addCustomer(params);
};

endpoint.addCustomerPaymentMethod = function (params) {
    return endpoint._addCustomerPaymentMethod(params);
};

endpoint.deleteCustomerPaymentMethod = function (params) {
    return endpoint._deleteCustomerPaymentMethod(params);
};

endpoint.getCustomerPaymentMethod = function (params) {
    return endpoint._getCustomerPaymentMethod(params);
};

endpoint.getCustomerPaymentMethods = function (params) {
    return endpoint._getCustomerPaymentMethods(params);
};

endpoint.updateCustomer = function (params) {
    return endpoint._updateCustomer(params);
};

endpoint.updateCustomer = function (params) {
    return endpoint._updateCustomer(params);
};

endpoint.quickUpdateCustomer = function (params) {
    return endpoint._quickUpdateCustomer(params);
};

endpoint.updateCustomerPaymentMethod = function (params) {
    return endpoint._updateCustomerPaymentMethod(params);
};

endpoint.copyCustomer = function (params) {
    return endpoint._copyCustomer(params);
};

endpoint.moveCustomer = function (params) {
    return endpoint._moveCustomer(params);
};

endpoint.getCustomerReport = function (params) {
    return endpoint._getCustomerReport(params);
};


