package org.byteforce.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;


/**
 * @author Philipp Baumgaertel
 */
public class MyClient
{
    public static void main(String[] args)
        throws ExecutionException, InterruptedException
    {
        // ResteasyClient client = new ResteasyClientBuilder().build();
        // ResteasyWebTarget target = client.target("http://localhost:9200");
        // ServicesInterface proxy = target.proxy(ServicesInterface.class);
    }
}
