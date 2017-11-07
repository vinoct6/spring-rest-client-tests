package com.example.customerclient;

import org.assertj.core.api.BDDAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = CustomerClientApplication.class)
@RunWith(SpringRunner.class)
public class CustomerServiceRestClientServerTest {

    @Autowired
    private CustomerClient client;

    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private RestTemplate restTemplate;

    private Resource customers = new ClassPathResource("customers.json");
    private Resource customerById = new ClassPathResource("customers_by_id.json");

    @Before
    public void before() {
        this.mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();

    }

    @Test
    public void customersShouldReturnAllCustomers() {

        mockRestServiceServer.expect(ExpectedCount.manyTimes(), requestTo("http://localhost:8080/customers"))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(customers, MediaType.APPLICATION_JSON_UTF8));


        Collection<Customer> customers = client.getCustomers();
        BDDAssertions.then(customers.size()).isEqualTo(2);
        mockRestServiceServer.verify();
    }
    }
