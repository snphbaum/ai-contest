package org.byteforce.ai;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;


/**
 * @author Philipp Baumgaertel
 */
public interface NeuralNetworkFactory
{
    public MultiLayerNetwork getNeuralNetwork(int pInputLength, int pOutputLength);
}
