package com.example.customerclient;


import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes = CustomerClientApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
public class CustomerClientWireMockTest {

    @Autowired
    private CustomerClient client;


    private Resource customers = new ClassPathResource("customers.json");
    private Resource customerById = new ClassPathResource("customers_by_id.json");

    @Test
    public void customersShouldReturnAllCustomers() {

        WireMock.stubFor(WireMock.get(WireMock.urlMatching("/customers"))
                         .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withStatus(HttpStatus.OK.value()).withBody(asJson(customers))));

        Collection<Customer> customers = client.getCustomers();
        BDDAssertions.then(customers.size()).isEqualTo(2);
    }

    private String asJson(Resource r) {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(r.getInputStream()))){
            Stream<String> lines = bufferedReader.lines();
            return lines.collect(Collectors.joining());

        } catch (Exception e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }

        return null;
    }
}
