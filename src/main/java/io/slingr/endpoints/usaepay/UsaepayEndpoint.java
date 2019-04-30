package io.slingr.endpoints.usaepay;

import com.usaepay.api.jaxws.*;
import io.slingr.endpoints.Endpoint;
import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.framework.annotations.ApplicationLogger;
import io.slingr.endpoints.framework.annotations.EndpointFunction;
import io.slingr.endpoints.framework.annotations.EndpointProperty;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.exceptions.ErrorCode;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

@SlingrEndpoint(name = "usaepay")
public class UsaepayEndpoint extends Endpoint {
    private static final Logger logger = LoggerFactory.getLogger(UsaepayEndpoint.class);

    @ApplicationLogger
    private AppLogs appLogger;

    @EndpointProperty
    private String sourceKey;

    @EndpointProperty
    private String sandbox;

    @EndpointProperty
    private String sourcePin;

    private boolean testing;
    private UeSoapServerPortType client;
    private UeSecurityToken securityToken;

    private final static String SANDBOX_URL = "sandbox.usaepay.com";
    private final static String PROD_URL = "secure.usaepay.com";

    public UsaepayEndpoint() {
    }

    public UsaepayEndpoint(String sourceKey, String sandbox, String sourcePin) {
        this.sourceKey = sourceKey;
        this.sandbox = sandbox;
        this.sourcePin = sourcePin;
    }

    @Override
    public void endpointStarted() {
        logger.info("Configuring UsaEpay client");

        int timeOut = 45 * 1000; // 45s
        if (sandbox != null && (sandbox.equalsIgnoreCase("yes") || sandbox.equalsIgnoreCase("true"))) {
            testing = true;
        } else {
            testing = false;
        }
        try {
            String clientUrl = testing ? SANDBOX_URL : PROD_URL;
            client = usaepay.getClient(clientUrl, timeOut);
            // Instantiate security token object (need by all soap methods)
            securityToken = usaepay.getToken(
                    sourceKey, // source key
                    sourcePin,  // source pin  (if assigned by merchant)
                    "127.0.0.1"  // IP address of end client (if applicable)
            );
        } catch (Exception e) {
            logger.error("Could not initialize UsaEpay client", e);
            appLogger.error("There was a problem configuring the UsaEpay. Please check your settings.", e);
        }

        logger.info("Done configuring UsaEpay client");
    }

    // Transactions services

    @EndpointFunction(name = "_runTransaction")
    public Json runTransaction(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Parameters"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runTransaction(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runTransactionAPI")
    public Json runTransactionAPI(Json params) {
        FieldValueArray fieldValueArray;
        try {
            fieldValueArray = JsonManipulator.convertToEpayObject(Json.map().set("fieldValue", params.json("Parameters")), FieldValueArray.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runTransactionAPI(securityToken, fieldValueArray);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runSale")
    public Json runSale(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Params"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runSale(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runAuthOnly")
    public Json runAuthOnly(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Params"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runAuthOnly(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runCredit")
    public Json runCredit(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Params"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runCredit(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runCheckSale")
    public Json runCheckSale(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Params"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runCheckSale(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runCheckCredit")
    public Json runCheckCredit(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Params"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runCheckCredit(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runQuickSale")
    public Json runQuickSale(Json params) {
        BigInteger refNum;
        TransactionDetail transactionDetail;
        boolean authOnly;
        try {
            refNum = params.bigInteger("RefNum");
            transactionDetail = JsonManipulator.convertToEpayObject(params.json("Details"), TransactionDetail.class);
            authOnly = Boolean.TRUE.equals(params.bool("AuthOnly"));
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runQuickSale(securityToken, refNum, transactionDetail, authOnly);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_runQuickCredit")
    public Json runQuickCredit(Json params) {
        BigInteger refNum;
        TransactionDetail transactionDetail;
        try {
            refNum = params.bigInteger("RefNum");
            transactionDetail = JsonManipulator.convertToEpayObject(params.json("Details"), TransactionDetail.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runQuickCredit(securityToken, refNum, transactionDetail);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_postAuth")
    public Json postAuth(Json params) {
        TransactionRequestObject transactionRequestObject;
        try {
            transactionRequestObject = JsonManipulator.convertToEpayObject(params.json("Params"), TransactionRequestObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.postAuth(securityToken, transactionRequestObject);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_captureTransaction")
    public Json captureTransaction(Json params) {
        BigInteger refNum;
        double amount;
        try {
            refNum = params.bigInteger("RefNum");
            amount = params.decimal("Amount");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.captureTransaction(securityToken, refNum, amount);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_refundTransaction")
    public Json refundTransaction(Json params) {
        BigInteger refNum;
        double amount;
        try {
            refNum = params.bigInteger("RefNum");
            amount = params.decimal("Amount");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.refundTransaction(securityToken, refNum, amount);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_overrideTransaction")
    public Json overrideTransaction(Json params) {
        BigInteger refNum;
        String reason;
        try {
            refNum = params.bigInteger("RefNum");
            reason = params.string("Reason");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean response = client.overrideTransaction(securityToken, refNum, reason);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_voidTransaction")
    public Json voidTransaction(Json params) {
        BigInteger refNum;
        try {
            refNum = params.bigInteger("RefNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean response = client.voidTransaction(securityToken, refNum);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getTransaction")
    public Json getTransaction(Json params) {
        BigInteger refNum;
        try {
            refNum = params.bigInteger("RefNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionObject transaction = client.getTransaction(securityToken, refNum);
            return JsonManipulator.convertToSlingrObject(transaction);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getTransactionStatus")
    public Json getTransactionStatus(Json params) {
        BigInteger refNum;
        try {
            refNum = params.bigInteger("RefNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.getTransactionStatus(securityToken, refNum);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getTransactionCustom")
    public Json getTransactionCustom(Json params) {
        BigInteger refNum;
        StringArray fields;
        try {
            refNum = params.bigInteger("RefNum");
            fields = JsonManipulator.convertToEpayObject(Json.map().set("string", params.json("Fields")), StringArray.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            FieldValueArray response = client.getTransactionCustom(securityToken, refNum, fields);
            return JsonManipulator.convertToSlingrObject(response.getFieldValue());
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getTransactionProfile")
    public Json getTransactionProfile(Json params) {
        BigInteger refNum;
        try {
            refNum = params.bigInteger("RefNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse transactionProfile = client.getTransactionProfile(securityToken, refNum);
            return JsonManipulator.convertToSlingrObject(transactionProfile);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getCheckTrace")
    public Json getCheckTrace(Json params) {
        BigInteger refNum;
        try {
            refNum = params.bigInteger("RefNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            CheckTrace checkTrace = client.getCheckTrace(securityToken, refNum);
            return JsonManipulator.convertToSlingrObject(checkTrace);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchTransactions")
    public Json searchTransactions(Json params) {
        SearchParamArray query;
        boolean matchAll;
        BigInteger start;
        BigInteger limit;
        String sort;
        try {
            query = JsonManipulator.convertToEpayObject(Json.map().set("searchParam", params.json("Search")), SearchParamArray.class);
            matchAll = Boolean.TRUE.equals(params.bool("MatchAll"));;
            start = params.bigInteger("Start");
            limit = params.bigInteger("Limit");
            sort = params.string("Sort");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionSearchResult transactionSearchResult = client.searchTransactions(securityToken, query, matchAll, start, limit, sort);
            return JsonManipulator.convertToSlingrObject(transactionSearchResult);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchTransactionsCount")
    public Json searchTransactionsCount(Json params) {
        SearchParamArray query;
        boolean matchAll;
        BigInteger start;
        BigInteger limit;
        String sort;
        try {
            query = JsonManipulator.convertToEpayObject(Json.map().set("searchParam", params.json("Search")), SearchParamArray.class);
            matchAll = Boolean.TRUE.equals(params.bool("MatchAll"));
            start = params.bigInteger("Start");
            limit = params.bigInteger("Limit");
            sort = params.string("Sort");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionSearchResult transactionSearchResult = client.searchTransactionsCount(securityToken, query, matchAll, start, limit, sort);
            return JsonManipulator.convertToSlingrObject(transactionSearchResult);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchTransactionsCustom")
    public Json searchTransactionsCustom(Json params) {
        SearchParamArray query;
        boolean matchAll;
        BigInteger start;
        BigInteger limit;
        StringArray fields;
        String format;
        String sort;
        try {
            query = JsonManipulator.convertToEpayObject(Json.map().set("searchParam", params.json("Search")), SearchParamArray.class);
            matchAll = Boolean.TRUE.equals(params.bool("MatchAll"));
            start = params.bigInteger("Start");
            limit = params.bigInteger("Limit");
            fields = JsonManipulator.convertToEpayObject(Json.map().set("string", params.json("FieldList")), StringArray.class);
            format = params.string("Format");
            sort = params.string("Sort");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            String result = client.searchTransactionsCustom(securityToken, query, matchAll, start, limit, fields, format, sort);
            return Json.map().set("body", result);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getTransactionReport")
    public Json getTransactionReport(Json params) {
        String startDate;
        String endDate;
        String report;
        String format;
        try {
            startDate = params.string("StartDate");
            endDate = params.string("EndDate");
            report = params.string("Report");
            format = params.string("Format");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            String response = client.getTransactionReport(securityToken, startDate, endDate, report, format);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_convertTranToCust")
    public Json convertTranToCust(Json params) {
        BigInteger refNum;
        FieldValueArray fieldValueArray;
        try {
            refNum = params.bigInteger("RefNum");
            fieldValueArray = JsonManipulator.convertToEpayObject(Json.map().set("fieldValue", params.json("UpdateData")), FieldValueArray.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            BigInteger response = client.convertTranToCust(securityToken, refNum, fieldValueArray);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getSession")
    public Json getSession(Json params) {
        try {
            TransactionSession response = client.getSession(securityToken);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }


    // Customers services

    @EndpointFunction(name = "_runCustomerTransaction")
    public Json runCustomerTransaction(Json params) {
        BigInteger customerNumberId;
        BigInteger paymentMethodId;
        CustomerTransactionRequest request;
        try {
            customerNumberId = params.bigInteger("CustNum");
            paymentMethodId = params.bigInteger("PaymentMethodID");
            request = JsonManipulator.convertToEpayObject(params.json("Parameters"), CustomerTransactionRequest.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionResponse response = client.runCustomerTransaction(securityToken, customerNumberId, paymentMethodId, request);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_enableCustomer")
    public Json enableCustomer(Json params) {
        BigInteger customerNumberId;
        try {
            customerNumberId = params.bigInteger("CustNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean result = client.enableCustomer(securityToken, customerNumberId);
            return Json.map().set("body", result) ;
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_disableCustomer")
    public Json disableCustomer(Json params) {
        BigInteger customerNumberId;
        try {
            customerNumberId = params.bigInteger("CustNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean result = client.disableCustomer(securityToken, customerNumberId);
            return Json.map().set("body", result) ;
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_deleteCustomer")
    public Json deleteCustomer(Json params) {
        BigInteger customerNumberId;
        try {
            customerNumberId = params.bigInteger("CustNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean result = client.deleteCustomer(securityToken, customerNumberId);
            return Json.map().set("body", result) ;
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchCustomerID")
    public Json searchCustomerID(Json params) {
        String customId;
        try {
            customId = params.string("CustID");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            BigInteger customNumberId =  client.searchCustomerID(securityToken, customId);
            if (customNumberId != null) {
                return Json.map().set("customNumberId", customNumberId);
            }
            return null;
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getCustomer")
    public Json getCustomer(Json params) {
        BigInteger customerNumberId;
        try {
            customerNumberId = params.bigInteger("CustNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            CustomerObject customerObject = client.getCustomer(securityToken, customerNumberId);
            return JsonManipulator.convertToSlingrObject(customerObject);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchCustomers")
    public Json searchCustomers(Json params) {
        SearchParamArray query;
        boolean matchAll;
        BigInteger start;
        BigInteger limit;
        String sort;
        try {
            query = JsonManipulator.convertToEpayObject(Json.map().set("searchParam", params.json("Search")), SearchParamArray.class);
            matchAll = Boolean.TRUE.equals(params.object("MatchAll"));
            start = params.bigInteger("Start");
            limit = params.bigInteger("Limit");
            sort = params.string("Sort");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            CustomerSearchResult customerSearchResult = client.searchCustomers(securityToken, query, matchAll, start, limit, sort);
            return JsonManipulator.convertToSlingrObject(customerSearchResult);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchCustomersCount")
    public Json searchCustomersCount(Json params) {
        SearchParamArray query;
        boolean matchAll;
        BigInteger start;
        BigInteger limit;
        String sort;
        try {
            query = JsonManipulator.convertToEpayObject(Json.map().set("searchParam", params.json("Search")), SearchParamArray.class);
            matchAll = Boolean.TRUE.equals(params.bool("MatchAll"));
            start = params.bigInteger("Start");
            limit = params.bigInteger("Limit");
            sort = params.string("Sort");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            CustomerSearchResult customerSearchResult = client.searchCustomers(securityToken, query, matchAll, start, limit, sort);
            return JsonManipulator.convertToSlingrObject(customerSearchResult);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_searchCustomersCustom")
    public Json searchCustomersCustom(Json params) {
        SearchParamArray query;
        boolean matchAll;
        BigInteger start;
        BigInteger limit;
        StringArray fields;
        String format;
        String sort;
        try {
            query = JsonManipulator.convertToEpayObject(Json.map().set("searchParam", params.json("Search")), SearchParamArray.class);
            matchAll = Boolean.TRUE.equals(params.bool("MatchAll"));
            start = params.bigInteger("Start");
            limit = params.bigInteger("Limit");
            fields = JsonManipulator.convertToEpayObject(Json.map().set("string", params.json("FieldList")), StringArray.class);
            format = params.string("Format");
            sort = params.string("Sort");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            String result = client.searchCustomersCustom(securityToken, query, matchAll, start, limit, fields, format, sort);
            return Json.map().set("body", result);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getCustomerHistory")
    public Json getCustomerHistory(Json params) {
        BigInteger customerNumberId;
        try {
            customerNumberId = params.bigInteger("CustNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            TransactionSearchResult response = client.getCustomerHistory(securityToken, customerNumberId);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_addCustomer")
    public Json addCustomer(Json params) {
        CustomerObject customerData;
        try {
            customerData = JsonManipulator.convertToEpayObject(params.json("CustomerData"), CustomerObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            BigInteger response = client.addCustomer(securityToken, customerData);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_addCustomerPaymentMethod")
    public Json addCustomerPaymentMethod(Json params) {
        BigInteger customerNumberId;
        PaymentMethod paymentMethod;
        boolean makeDefault;
        boolean verify;
        try {
            customerNumberId = params.bigInteger("CustNum");
            paymentMethod = JsonManipulator.convertToEpayObject(params.json("PaymentMethod"), PaymentMethod.class);
            makeDefault = Boolean.TRUE.equals(params.bool("MakeDefault"));
            verify = Boolean.TRUE.equals(params.bool("Verify"));
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            BigInteger response = client.addCustomerPaymentMethod(securityToken, customerNumberId, paymentMethod, makeDefault, verify);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_deleteCustomerPaymentMethod")
    public Json deleteCustomerPaymentMethod(Json params) {
        BigInteger customerNumberId;
        BigInteger paymentMethodId;
        try {
            customerNumberId = params.bigInteger("CustNum");
            paymentMethodId = params.bigInteger("PaymentMethodID");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean response = client.deleteCustomerPaymentMethod(securityToken, customerNumberId, paymentMethodId);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getCustomerPaymentMethod")
    public Json getCustomerPaymentMethod(Json params) {
        BigInteger customerNumberId;
        BigInteger paymentMethodId;
        try {
            customerNumberId = params.bigInteger("CustNum");
            paymentMethodId = params.bigInteger("PaymentMethodID");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            PaymentMethod response = client.getCustomerPaymentMethod(securityToken, customerNumberId, paymentMethodId);
            return JsonManipulator.convertToSlingrObject(response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getCustomerPaymentMethods")
    public Json getCustomerPaymentMethods(Json params) {
        BigInteger customerNumberId;
        try {
            customerNumberId = params.bigInteger("CustNum");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            PaymentMethodArray response = client.getCustomerPaymentMethods(securityToken, customerNumberId);
            if (response != null) {
                return JsonManipulator.convertToSlingrObject(response.getItem());
            }
            return null;
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_updateCustomer")
    public Json updateCustomer(Json params) {
        BigInteger customerNumberId;
        CustomerObject customerData;
        try {
            customerNumberId = params.bigInteger("CustNum");
            customerData = JsonManipulator.convertToEpayObject(params.json("CustomerData"), CustomerObject.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean response = client.updateCustomer(securityToken, customerNumberId, customerData);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_quickUpdateCustomer")
    public Json quickUpdateCustomer(Json params) {
        BigInteger customerNumberId;
        FieldValueArray fieldValueArray;
        try {
            customerNumberId = params.bigInteger("CustNum");
            fieldValueArray = JsonManipulator.convertToEpayObject(Json.map().set("fieldValue", params.json("UpdateData")), FieldValueArray.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean response = client.quickUpdateCustomer(securityToken, customerNumberId, fieldValueArray);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_updateCustomerPaymentMethod")
    public Json updateCustomerPaymentMethod(Json params) {
        PaymentMethod paymentMethod;
        boolean verify;
        try {
            paymentMethod = JsonManipulator.convertToEpayObject(params.json("PaymentMethod"), PaymentMethod.class);
            verify = Boolean.TRUE.equals(params.bool("Verify"));
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            boolean response = client.updateCustomerPaymentMethod(securityToken, paymentMethod, verify);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_copyCustomer")
    public Json copyCustomer(Json params) {
        BigInteger customerNumberId;
        UeSecurityToken toToken;
        try {
            customerNumberId = params.bigInteger("CustNum");
            toToken = JsonManipulator.convertToEpayObject(params.json("ToToken"), UeSecurityToken.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            BigInteger response = client.copyCustomer(securityToken, customerNumberId, toToken);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_moveCustomer")
    public Json moveCustomer(Json params) {
        BigInteger customerNumberId;
        UeSecurityToken toToken;
        try {
            customerNumberId = params.bigInteger("CustNum");
            toToken = JsonManipulator.convertToEpayObject(params.json("ToToken"), UeSecurityToken.class);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            BigInteger response = client.moveCustomer(securityToken, customerNumberId, toToken);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }

    @EndpointFunction(name = "_getCustomerReport")
    public Json getCustomerReport(Json params) {
        String report;
        FieldValueArray fieldValueArray;
        String format;
        try {
            report = params.string("Report");
            fieldValueArray = JsonManipulator.convertToEpayObject(Json.map().set("fieldValue", params.json("Options")), FieldValueArray.class);
            format = params.string("Format");
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing arguments [%s]", params.toString()), e);
        }
        try {
            String response = client.getCustomerReport(securityToken, report, fieldValueArray, format);
            return Json.map().set("body", response);
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.API, "Error executing customer operation", e);
        }
    }
}
