package org.byteforce.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ws.rs.client.Client;

import org.byteforce.server.TestDTO;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;


/**
 * @author Philipp Baumgaertel
 */
public class MyClient
{
    public static void main(String[] args)
        throws ExecutionException, InterruptedException
    {
        Client client = ResteasyClientBuilder.newClient();
        Future<TestDTO> futureResponse = client.target("http://localhost:8080/restredirect/rest/").path("target/test").request().async().get(TestDTO.class);
        System.out.println(futureResponse.get());


        // ResteasyClient client = new ResteasyClientBuilder().build();
        // ResteasyWebTarget target = client.target("http://localhost:9200");
        // ServicesInterface proxy = target.proxy(ServicesInterface.class);
    }
}
