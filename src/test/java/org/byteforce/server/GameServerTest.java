package org.byteforce.server;

import java.io.File;
import java.net.URL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.byteforce.server.DTOs.GameState;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Philipp Baumgaertel
 */
@RunWith(Arquillian.class)
public class GameServerTest
{
    @ArquillianResource
    URL baseURL;

    @Test
    @RunAsClient
    public void testPutAndGetState()
        throws Exception
    {
        Client client = ResteasyClientBuilder.newClient();
        GameState input = new GameState();
        input.setGameId(123);
        Response response = client.target(baseURL.toURI()).path("rest/server/game/123/state").request().
            put(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));
        response.close();
        System.out.println(baseURL.toURI());
        GameState result = client.target(baseURL.toURI()).path("rest/server/game/123/state").request().get(
            GameState.class);
        client.close();
        Assertions.assertThat(result.getGameId()).isEqualTo(123);
    }

    @Test
    @RunAsClient
    public void testPutAndGetStateViaProxy()
        throws Exception
    {
        // Use the interface directly
        // http://www.baeldung.com/resteasy-client-tutorial
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(baseURL.toURI()).path("rest");
        GameServer proxy = target.proxy(GameServer.class);
        GameState input = new GameState();
        input.setGameId(123);
        Response response = proxy.storeState(123,input);
        response.close();
        GameState result = proxy.getState(123);

        client.close();
        Assertions.assertThat(result.getGameId()).isEqualTo(123);
    }



    @Deployment
    @TargetsContainer("jetty")
    public static WebArchive createDeployment()
    {
        //@formatter:off
        File[] files = Maven.resolver()
            .addDependencies(
                MavenDependencies.createDependency("javax.servlet:javax.servlet-api:3.1.0", ScopeType.COMPILE, false),
                MavenDependencies.createDependency("javax.ws.rs:javax.ws.rs-api:2.0.1", ScopeType.COMPILE, false),
                MavenDependencies.createDependency("org.jboss.resteasy:resteasy-jaxrs:3.1.2.Final", ScopeType.COMPILE, false),
                MavenDependencies.createDependency("org.jboss.resteasy:async-http-servlet-3.0:3.1.0.Beta2", ScopeType.COMPILE, false),
                MavenDependencies.createDependency("org.jboss.resteasy:resteasy-jackson2-provider:3.1.2.Final", ScopeType.COMPILE, false),
                MavenDependencies.createDependency("org.hibernate:hibernate-entitymanager:5.2.10.Final", ScopeType.COMPILE, false),
                MavenDependencies.createDependency("org.apache.commons:commons-lang3:3.5", ScopeType.COMPILE, false)
                )
            //.loadPomFromFile("pom.xml").importRuntimeDependencies()
            .resolve()
            .withTransitivity()
            // .using(new CombinedStrategy(new RejectDependenciesStrategy("org.nd4j:nd4j-native-platform:0.8.0",
            //    "org.deeplearning4j:deeplearning4j-core:0.8.0", "org.bytedeco:javacpp:1.3.2"),
            //    TransitiveStrategy.INSTANCE))
            .asFile();

        return ShrinkWrap.create(WebArchive.class)
            //.addPackages(true, Filters.exclude(".*Test.*"),
            //    GameServer.class.getPackage(), GameState.class.getPackage())
            .addPackages(true, Filters.exclude(".*Test.*"), "org.byteforce")
            //complains about the same realm registered multiple times
            //.addAsWebInfResource(new File("src/test/resources", "jboss-web.xml"))
            .addAsWebInfResource(new File("src/test/resources", "web.xml"))
            .addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml")
            .addAsLibraries(files);
        //@formatter:on
    }
}