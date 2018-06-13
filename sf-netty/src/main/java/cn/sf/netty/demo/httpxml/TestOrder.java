package cn.sf.netty.demo.httpxml;

import cn.sf.netty.demo.httpxml.model.Address;
import cn.sf.netty.demo.httpxml.model.Customer;
import cn.sf.netty.demo.httpxml.model.Order;
import cn.sf.netty.demo.httpxml.model.Shipping;
import org.jibx.runtime.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

public class TestOrder {
    private IBindingFactory factory = null;
    private StringWriter writer = null;
    private StringReader reader = null;
    private final static String CHARSET_NAME = "UTF-8";

    private String encode2Xml(Order order) throws JiBXException, IOException {
        factory = BindingDirectory.getFactory(Order.class);
        writer = new StringWriter();
        IMarshallingContext mctx = factory.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(order, CHARSET_NAME, null, writer);
        String xmlStr = writer.toString();
        writer.close();
        System.out.println(xmlStr.toString());
        return xmlStr;
    }

    private Order decode2Order(String xmlBody) throws JiBXException {
        reader = new StringReader(xmlBody);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Order order = (Order) uctx.unmarshalDocument(reader);
        return order;
    }

    public static void main(String[] args) throws JiBXException, IOException {
        TestOrder test = new TestOrder();
        Order order = new Order();
        order.setOrderNumber(123);
        Customer customer = new Customer();
        customer.setFirstName("ali");
        customer.setMiddleNames(Arrays.asList("baba"));
        customer.setLastName("taobao");
        order.setCustomer(customer);
        Address address = new Address();
        address.setCity("南京市");
        address.setCountry("中国");
        address.setPostCode("123321");
        address.setState("江苏省");
        address.setStreet1("龙眠大道");
        address.setStreet2("INTERNATIONAL_MAIL");
        order.setBillTo(address);
        order.setShipTo(address);
        order.setShipping(Shipping.INTERNATIONAL_MAIL);
        order.setTotal(33f);
        String body = test.encode2Xml(order);
        Order order2 = test.decode2Order(body);
        System.out.println(order2);
    }
}