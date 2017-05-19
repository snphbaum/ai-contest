package org.byteforce.ai;

import java.util.List;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;


/**
 * @author Philipp Baumgaertel
 */
public interface NeuralNetworkFactory
{
    public MultiLayerNetwork getNeuralNetwork();
}
