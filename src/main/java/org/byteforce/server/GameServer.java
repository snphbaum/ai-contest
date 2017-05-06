package org.byteforce.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.byteforce.server.DTOs.GameState;


/**
 * @author Philipp Baumgaertel
 */
@Path("/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GameServer
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("game/{gameId}/state")
    GameState getState(@PathParam("gameId") int gameId);



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("game/{gameId}/player/{playerName}/state")
    void getStateForPlayer(@PathParam("gameId") int gameId, @PathParam("playerName") String playerName,
        @Suspended AsyncResponse asyncResponse);



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("game/{gameId}/sequence/{sequence}/state")
    void getStateOnUpdate(@PathParam("gameId") int gameId, @PathParam("sequence") int sequence,
        @Suspended AsyncResponse asyncResponse);



    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("game/{gameId}/state")
    Response storeState(@PathParam("gameId") int gameId, GameState pGameState);
}
