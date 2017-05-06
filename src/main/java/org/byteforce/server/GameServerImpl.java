package org.byteforce.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

import org.byteforce.server.DTOs.GameState;


/**
 * @author Philipp Baumgaertel
 */
public class GameServerImpl
    implements GameServer
{

    // @Inject
    // GameStorage gameStorage;


    @Override
    public GameState getState(int gameId)
    {
        GameState result = GameStorage.INSTANCE.getStateOfGame(gameId);

        if (result == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return result;
    }



    @Override
    public void getStateForPlayer(int gameId, String playerName,
        @Suspended final AsyncResponse asyncResponse)
    {
        GameState result = GameStorage.INSTANCE.getStateOfGame(gameId);
        if (result == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (result.getNextPlayer() != playerName) {
            GameStorage.INSTANCE.putObserver(playerName, asyncResponse);
        }
    }



    @Override
    public void getStateOnUpdate(int gameId, int sequence,
        @Suspended final AsyncResponse asyncResponse)
    {
        //TODO set timeout for response: response.setTimeout(5, TimeUnit.SECONDS);
        GameState result = GameStorage.INSTANCE.getStateOfGame(gameId);
        if (result == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (result.getSequenceNumber() <= sequence) {
            GameStorage.INSTANCE.putObserver(sequence, asyncResponse);
        }
    }



    @Override
    public Response storeState(int gameId, final GameState pGameState)
    {
        //TODO validate state of game
        GameStorage.INSTANCE.putStateOfGame(gameId, pGameState);

        return Response.ok().build();
    }
}
