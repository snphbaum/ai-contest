package org.byteforce.server;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.Asynchronous;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;




@Path("/target")
public class TestTargetEA
{
    public static class Task
    {
        public String taskId;

        public String details1;

        public String details2;
    }


    private final Map<String, Task> taskIdToTaskMap = new ConcurrentHashMap<>();



    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("task")
    public Response storeTask(final Task task)
    {
        taskIdToTaskMap.put(task.taskId, task);


        final String urlPrameter = "taskId=" + encodeUrl(task.taskId) + "&details1=" + encodeUrl(task.details1);


        return Response.created(URI.create("http://localhost:8080/restredirect/target.html?" + urlPrameter)).build();
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("task/{taskId}")
    public Task retrieveTask(@PathParam("taskId") String taskId)
    {
        final Task task = taskIdToTaskMap.get(taskId);
        if (task == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return task;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Asynchronous
    @Path("test")
    public void asyncRestMethod(@Suspended final AsyncResponse asyncResponse) {
        TestDTO myDTO = heavyLifting();
        asyncResponse.resume(myDTO);

        /**
         * Try giving the async response to an subject as an observer
         * Alternative: work with a manually created Thread
         */

    }

    private TestDTO heavyLifting() {

        return new TestDTO("Result", 1);

    }

    private static String encodeUrl(final String s)
    {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}