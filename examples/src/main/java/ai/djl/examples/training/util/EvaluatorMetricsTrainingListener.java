/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.examples.training.util;

import ai.djl.Model;
import ai.djl.metric.Metrics;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingListener;
import ai.djl.training.evaluator.Evaluator;

public class EvaluatorMetricsTrainingListener implements TrainingListener {

    @Override
    public void onEpoch(Trainer trainer) {}

    @Override
    public void onTrainingBatch(Trainer trainer) {
        Metrics metrics = trainer.getMetrics();
        for (Evaluator evaluator : trainer.getTrainingEvaluators()) {
            metrics.addMetric("train_" + evaluator.getName(), evaluator.getValue());
        }
    }

    @Override
    public void onValidationBatch(Trainer trainer) {
        Metrics metrics = trainer.getMetrics();
        for (Evaluator evaluator : trainer.getValidationEvaluators()) {
            metrics.addMetric("validate_" + evaluator.getName(), evaluator.getValue());
        }
    }

    @Override
    public void onTrainingBegin(Trainer trainer) {}

    @Override
    public void onTrainingEnd(Trainer trainer) {
        Model model = trainer.getModel();
        Metrics metrics = trainer.getMetrics();
        for (Evaluator evaluator : trainer.getValidationEvaluators()) {
            float value =
                    metrics.latestMetric("validate_" + evaluator.getName()).getValue().floatValue();
            model.setProperty(evaluator.getName(), String.format("%.5f", value));
        }
    }
}
