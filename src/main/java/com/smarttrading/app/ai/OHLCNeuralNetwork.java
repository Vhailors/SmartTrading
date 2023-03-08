package com.smarttrading.app.ai;

import com.smarttrading.app.xtb.datasupplier.dto.OHLC;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OHLCNeuralNetwork {

    public double[] createModel(List<OHLC> ohlcList) {

        int inputSize = 4;
        int outputSize = 1;
        int hiddenSize = 10;
        int numEpochs = 1000;
        double learningRate = 0.01;

        List<DataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < ohlcList.size() - 1; i++) {
            OHLC currentOHLC = ohlcList.get(i);
            OHLC nextOHLC = ohlcList.get(i + 1);
            double[] inputArray = new double[inputSize];
            inputArray[0] = currentOHLC.getOpen().doubleValue();
            inputArray[1] = currentOHLC.getHigh().doubleValue();
            inputArray[2] = currentOHLC.getLow().doubleValue();
            inputArray[3] = currentOHLC.getClose().doubleValue();
            double[] outputArray = new double[outputSize];
            outputArray[0] = nextOHLC.getClose().doubleValue();
            DataSet dataSet = new DataSet(Nd4j.create(inputArray), Nd4j.create(outputArray));
            dataSets.add(dataSet);
        }

        SplitTestAndTrain testAndTrain = new ListDataSetIterator<>(dataSets).next().splitTestAndTrain(0.8);
        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        MultiLayerConfiguration builder = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .l2(0.0001)
                .updater(Nesterovs.builder().learningRate(learningRate).momentum(0.9).build())
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputSize)
                        .nOut(hiddenSize)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new OutputLayer.Builder()
                        .nIn(hiddenSize)
                        .nOut(outputSize)
                        .activation(Activation.IDENTITY)
                        .lossFunction(LossFunctions.LossFunction.MSE)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .backpropType(BackpropType.Standard)
                .pretrain(false)
                .backprop(true)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(builder);
        model.init();
        for (int i = 0; i < numEpochs; i++) {
            model.fit(trainingData);
        }

        double[] actuals = testData.getFeatures().getRow(0).data().asDouble();
        double[] expecteds = testData.getLabels().getRow(0).data().asDouble();
        double[] predicted = model.output(testData.getFeatures()).data().asDouble();

        return predicted;
}
}