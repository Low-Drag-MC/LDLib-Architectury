package com.lowdragmc.lowdraglib.gui.graphprocessor.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BaseGraphProcessor {
    protected BaseGraph graph;

    /**
     * Manage graph scheduling and processing
     */
    public BaseGraphProcessor(BaseGraph graph) {
        this.graph = graph;
        updateComputeOrder();
    }

    public abstract void updateComputeOrder();

    /**
     * Schedule the graph into the job system
     */
    public abstract void run();

    public static class Process extends BaseGraphProcessor {
        protected List<BaseNode> processList = Collections.emptyList();

        public Process(BaseGraph graph) {
            super(graph);
        }

        @Override
        public void updateComputeOrder() {
            processList = graph.nodes.stream().sorted(Comparator.comparing(n -> n.computeOrder)).toList();
        }

        @Override
        public void run() {
            processList.forEach(BaseNode::onProcess);
        }

    }
}
