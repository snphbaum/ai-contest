package org.byteforce.ai;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * @author Philipp Baumgaertel
 */
public class AiPlayer
    implements Player
{

    Random rand;
    ActionFactory actionFactory;
    MultiLayerNetwork model;

    public AiPlayer(ActionFactory pActionFactory, String pModelFileName) {
        actionFactory = pActionFactory;
        rand = new Random();

        // Load the model
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(new File(pModelFileName));
        }
        catch (IOException pE) {
            throw new RuntimeException("Cannot load model");
        }
    }

    @Override
    public Action getAction(final State pState)
    {
        INDArray qVal = model.output(pState.getInputRepresentation());
        int a = Nd4j.argMax(qVal).getInt(0);
        //State new_s = s.move(actionFactory.get(a));
        return actionFactory.get(a);

    }
}
