package io.slingr.endpoints.usaepay;

import com.usaepay.api.jaxws.*;
import io.slingr.endpoints.utils.Json;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by smoyano on 06/12/17.
 */
public class AutomaticMappingTest {

    @Test
    public void testConvertJsonToCustomer() {
        Json customerJson = Json.map()
                .set("billingAddress",
                        Json.map()
                                .set("firstName", "John")
                                .set("lastName", "Doe")
                                .set("company", "Acme Corp")
                                .set("street", "1234 main st")
                                .set("street2", "Suite #123")
                                .set("city", "Los Angeles")
                                .set("state", "CA")
                                .set("zip", "12345")
                                .set("country", "US")
                                .set("email", "support@usaepay.com")
                                .set("phone", "333-333-3333")
                                .set("fax", "333-333-3334")

                )
                .set("paymentMethods", Json.list().push(
                        Json.map()
                                .set("cardNumber", "4444555566667779")
                                .set("cardExpiration", "0918")
                                .set("cardType", "")
                                .set("avsStreet", "")
                                .set("avsZip", "")
                                .set("methodName", "My Visa")
                                .set("secondarySort", 1))
                )
                .set("customerID", "123123")
                .set("description", "Weekly Bill")
                .set("enabled", true)
                .set("notes", "Testing the soap addCustomer Function")
                .set("receiptNote", "addCustomer test Created Charge");

        CustomerObject customerObject = JsonManipulator.convertToEpayObject(customerJson, CustomerObject.class);
        assertEquals("John", customerObject.getBillingAddress().getFirstName());
        assertEquals("Doe", customerObject.getBillingAddress().getLastName());
        assertEquals("Acme Corp", customerObject.getBillingAddress().getCompany());
        assertEquals("4444555566667779", customerObject.getPaymentMethods().getItem().get(0).getCardNumber());
        assertEquals("0918", customerObject.getPaymentMethods().getItem().get(0).getCardExpiration());
        assertEquals(1, customerObject.getPaymentMethods().getItem().get(0).getSecondarySort().intValue());
        assertEquals("123123", customerObject.getCustomerID());
        assertEquals(true, customerObject.isEnabled());

    }

    @Test
    public void testConvertCustomerToJson() {
        CustomerObject customer = new CustomerObject();
        Address address = new Address();
        address.setFirstName("John");
        address.setLastName("Doe");
        address.setCompany("Acme INC");
        address.setStreet("343 Main Street");
        address.setStreet2("Suite 222");
        address.setCity("Somewhere");
        address.setState("CA");
        address.setZip("91920");
        address.setCountry("US");
        address.setEmail("joe@example.com");
        address.setFax("595-343-4454");
        address.setPhone("333-444-5555");
        customer.setBillingAddress(address);
        customer.setEnabled(true);
        customer.setAmount(5.00);
        customer.setTax(0.50);
        customer.setNext("2015-09-01");
        customer.setSchedule("Monthly");
        customer.setOrderID("100090");
        customer.setDescription("Monthly Member Fee");
        PaymentMethodArray paymethods = new PaymentMethodArray();
        PaymentMethod paymethod = new PaymentMethod();
        paymethod.setCardNumber("4111111111111111");
        paymethod.setCardExpiration("0919");
        paymethod.setMethodName("My Visa");
        paymethods.getItem().add(paymethod);
        customer.setPaymentMethods(paymethods);

        Json responseJson = JsonManipulator.convertToSlingrObject(customer);
        assertEquals("John", responseJson.json("BillingAddress").string("FirstName"));
        assertEquals("Doe", responseJson.json("BillingAddress").string("LastName"));
        assertEquals("US", responseJson.json("BillingAddress").string("Country"));
        assertEquals(true, responseJson.bool("Enabled"));
        assertEquals(new Double(5.00), responseJson.decimal("Amount"));
        assertEquals("Monthly Member Fee", responseJson.string("Description"));
        assertEquals("4111111111111111", responseJson.jsons("PaymentMethods").get(0).string("CardNumber"));
        assertEquals("0919", responseJson.jsons("PaymentMethods").get(0).string("CardExpiration"));
        assertEquals("My Visa", responseJson.jsons("PaymentMethods").get(0).string("MethodName"));
    }

    @Test(expected = RuntimeException.class)
    public void testConvertJsonToCustomerUnrecognizedField() {
        Json customerJson = Json.map()
                .set("billingAddress",
                        Json.map()
                                .set("firstName", "John")
                                .set("lastName", "Doe")
                                .set("company", "Acme Corp")
                                .set("street", "1234 main st")
                                .set("street2", "Suite #123")
                                .set("city", "Los Angeles")
                                .set("state", "CA")
                                .set("zip", "12345")
                                .set("country", "US")
                                .set("email", "support@usaepay.com")
                                .set("phone", "333-333-3333")
                                .set("fax", "333-333-3334")

                )
                .set("paymentMethods", Json.list().push(
                        Json.map()
                                .set("cardNumber", "4444555566667779")
                                .set("cardExpiration", "0918")
                                .set("cardType", "")
                                .set("avsStreet", "")
                                .set("avsZip", "")
                                .set("methodName", "My Visa")
                                .set("secondarySort", 1)
                                .set("wrongField", "test"))
                )
                .set("customerID", "123123")
                .set("description", "Weekly Bill")
                .set("enabled", true)
                .set("notes", "Testing the soap addCustomer Function")
                .set("receiptNote", "addCustomer test Created Charge");

        CustomerObject customerObject = JsonManipulator.convertToEpayObject(customerJson, CustomerObject.class);
        assertEquals("John", customerObject.getBillingAddress().getFirstName());
        assertEquals("Doe", customerObject.getBillingAddress().getLastName());
        assertEquals("Acme Corp", customerObject.getBillingAddress().getCompany());
        assertEquals("4444555566667779", customerObject.getPaymentMethods().getItem().get(0).getCardNumber());
        assertEquals("0918", customerObject.getPaymentMethods().getItem().get(0).getCardExpiration());
        assertEquals(1, customerObject.getPaymentMethods().getItem().get(0).getSecondarySort().intValue());
        assertEquals("123123", customerObject.getCustomerID());
        assertEquals(true, customerObject.isEnabled());

    }


}
