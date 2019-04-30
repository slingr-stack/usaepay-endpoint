package io.slingr.endpoints.usaepay;

import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.utils.tests.EndpointTests;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.soap.SOAPException;

import static org.junit.Assert.*;

@Ignore("For dev purposes")
public class UsaepayEndpointTest {

    private static EndpointTests test;
    private static UsaepayEndpoint endpoint;

    @BeforeClass
    public static void init() throws Exception {
        test = EndpointTests.start(new io.slingr.endpoints.usaepay.Runner(), "test.properties");
        endpoint = (UsaepayEndpoint) test.getEndpoint();
    }

    @Test
    public void testTransactionsFlow() {
        // runSale
        Json transactionParams = Json.map();
        transactionParams.set("AccountHolder", "Test Joe");
        transactionParams.set("Command", "Sale");
        Json details = Json.map();
        details.set("Amount", 22.34);
        details.set("Description", "My Test Sale");
        details.set("Invoice", "119891");
        transactionParams.set("Details", details);
        Json ccdata = Json.map();
        ccdata.set("CardNumber", "4444555566667779");
        ccdata.set("CardExpiration", "0919");
        ccdata.set("CardCode", "999");
        transactionParams.set("CreditCardData", ccdata);
        Json transactionResult = endpoint.runTransaction(Json.map().set("Parameters", transactionParams));
        assertNotNull(transactionResult);
        Integer transactionId = transactionResult.integer("RefNum");
        assertNotNull(transactionId);

        // getTransaction
        Json transaction = endpoint.getTransaction(Json.map().set("RefNum", transactionId));
        assertNotNull(transaction);
        assertNotNull(transaction.json("Details"));
        assertNotNull(transaction.json("CreditCardData"));

        // getTransactionStatus
        Json transactionStatus = endpoint.getTransactionStatus(Json.map().set("RefNum", transactionId));
        assertEquals("Pending", transactionStatus.string("Status"));

        // voidTransaction
        Json voidTransactionResult = endpoint.voidTransaction(Json.map().set("RefNum", transactionId));
        assertTrue(voidTransactionResult.bool("body"));
    }

    @Test
    public void testCustomerWorkflow() throws SOAPException {
        Json customerJson = Json.map()
                .set("BillingAddress",
                        Json.map()
                                .set("FirstName", "John")
                                .set("LastName", "Doe")
                                .set("Company", "Acme Corp")
                                .set("Street", "1234 main st")
                                .set("Street2", "Suite #123")
                                .set("City", "Los Angeles")
                                .set("State", "CA")
                                .set("Zip", "12345")
                                .set("Country", "US")
                                .set("Email", "support@usaepay.com")
                                .set("Phone", "333-333-3333")
                                .set("Fax", "333-333-3334")

                )
                .set("PaymentMethods", Json.list().push(
                        Json.map()
                                .set("CardNumber", "4444555566667779")
                                .set("CardExpiration", "0918")
                                .set("CardType", "")
                                .set("AvsStreet", "")
                                .set("AvsZip", "")
                                .set("MethodName", "My Visa")
                                .set("SecondarySort", 1))
                )
                .set("CustomerID", "123123")
                .set("Description", "Weekly Bill")
                .set("Enabled", true)
                .set("Notes", "Testing the soap addCustomer Function")
                .set("ReceiptNote", "addCustomer test Created Charge");

        // create customer
        Json result = endpoint.addCustomer(Json.map().set("CustomerData", customerJson));
        Integer customerId = result.integer("body");
        assertNotNull(customerId);

        // find customer
        Json customer = endpoint.getCustomer(Json.map().set("CustNum", customerId));
        assertNotNull(customer);
        assertEquals("John", customer.json("BillingAddress").string("FirstName"));
        assertEquals("Doe", customer.json("BillingAddress").string("LastName"));
        assertEquals("US", customer.json("BillingAddress").string("Country"));
        assertEquals(true, customer.bool("Enabled"));
        assertEquals("Weekly Bill", customer.string("Description"));
        assertEquals("XXXXXXXXXXXX7779", customer.jsons("PaymentMethods").get(0).string("CardNumber"));
        assertEquals("2018-09", customer.jsons("PaymentMethods").get(0).string("CardExpiration"));
        assertEquals("My Visa", customer.jsons("PaymentMethods").get(0).string("MethodName"));

        // list customers
        Json customersQuery = Json.map()
                .set("Search", Json.list().push(Json.map()
                        .set("Field", "CustNum")
                        .set("Type", "eq")
                        .set("Value", customerId)
                    )
                )
                .set("Start", 0)
                .set("Limit", 1);
        Json searchResults = endpoint.searchCustomers(customersQuery);
        assertNotNull(searchResults);
        assertEquals(1, searchResults.integer("CustomersMatched").intValue());

        // get custom payment method
        Json paymentMethodQuery = Json.map()
                .set("CustNum", customerId)
                .set("PaymentMethodID", customer.jsons("PaymentMethods").get(0).string("MethodID"));
        Json paymentMethod = endpoint.getCustomerPaymentMethod(paymentMethodQuery);
        assertNotNull(paymentMethod);

        // update payment method
        Json paymentMethodPayload = Json.map();
        paymentMethodPayload.set("PaymentMethod", paymentMethod);
        Json updatePaymentMethodResponse = endpoint.updateCustomerPaymentMethod(paymentMethodPayload);
        assertTrue(updatePaymentMethodResponse.bool("body"));

        // get custom payment methods
        Json paymentMethodsQuery = Json.map()
                .set("CustNum", customerId);
        Json paymentMethods = endpoint.getCustomerPaymentMethods(paymentMethodsQuery);
        assertNotNull(paymentMethods);
        assertTrue(paymentMethods.isList());

        // update customer
        Json updateCustomerQuery = Json.map()
                .set("CustNum", customerId)
                .set("CustomerData", customerJson);
        Json updateCustomerResult = endpoint.updateCustomer(updateCustomerQuery);
        assertTrue(updateCustomerResult.bool("body"));

        // run customer transaction
        Json customerTransaction = Json.map()
                .set("CustNum", customerId)
                .set("Parameters", Json.map().set("Details", Json.map()
                        .set("Invoice", "123456")
                        .set("Description", "Sample Credit Card Sale")
                        .set("Amount", 1.05)
                ));
        endpoint.runCustomerTransaction(customerTransaction);

        // delete customer
        Json deleteCustomerQuery = Json.map()
                .set("CustNum", customerId);
        Json deleteCustomerResult = endpoint.deleteCustomer(deleteCustomerQuery);
        assertTrue(deleteCustomerResult.bool("body"));
    }


}
