package com.lowdragmc.lowdraglib.gui.graphprocessor.processor;

import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseGraph;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.BaseNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.ITriggerableNode;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.StartNode;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This processor will run nodes from start nodes and precess node alone the trigger links.
 * It will process nodes on if it is requiring.
 */
public class TriggerProcessor extends GraphProcessor {
    protected List<StartNode> startNodeList = Collections.emptyList();
    protected Map<BaseNode, List<BaseNode>> nonConditionalDependenciesCache = new HashMap<>();

    public TriggerProcessor(BaseGraph graph) {
        super(graph);
    }

    @Override
    public void updateComputeOrder() {
        // Gather start nodes:
        startNodeList = graph.nodes.stream().filter(StartNode.class::isInstance).map(StartNode.class::cast).toList();
        // In case there is no start node, we process the graph like usual
        if (startNodeList.isEmpty()) {
            super.updateComputeOrder();
        } else {
            // Prepare the cache of non-conditional node execution
            nonConditionalDependenciesCache.clear();
        }
    }

    @Override
    public @NotNull Iterator<BaseNode> iterator() {
        if (startNodeList.isEmpty()) {
            //we process the graph like usual
            return super.iterator();
        }
        return new InternalIterator();
    }

    private class InternalIterator implements java.util.Iterator<BaseNode> {
        /**
         * Run the graph with the given stack
         * <br>
         * - left: is common node to execute
         * <br>
         * - right: is a pair of triggerable nodes
         *    * left: the trigger node
         *    * right: the triggered source
         */
        private final Stack<Either<BaseNode, Pair<ITriggerableNode, ITriggerableNode>>> nodeToExecute = new Stack<>();
        private final HashSet<ITriggerableNode> nodeDependenciesGathered = new HashSet<>();

        private InternalIterator() {
            // make low priority nodes to be executed first
            startNodeList.stream().sorted((n1, n2) -> n2.getComputeOrder() - n1.getComputeOrder())
                    .forEach(n -> nodeToExecute.push(Either.right(Pair.of(n, null))));
        }

        @Override
        public boolean hasNext() {
            return !nodeToExecute.isEmpty();
        }

        @Override
        public BaseNode next() {
            while (!nodeToExecute.isEmpty()) {
                // TODO: maxExecutionTimeMS
                var either = nodeToExecute.pop();
                if (either.left().isPresent()) {
                    // if its common node, we execute it as usual
                    var node = either.left().get();
                    node.onProcess();
                    return node;
                } else if (either.right().isPresent()) {
                    // if its triggerable node, we execute it with the trigger source
                    var triggerNode = either.right().get().left();
                    var triggerSource = either.right().get().right();
                    // make sure its dependencies are all already executed
                    if (nodeDependenciesGathered.contains(triggerNode)) {
                        triggerNode.onTrigger(triggerSource);
                        // select the next nodes to execute
                        triggerNode.getNextTriggerNodes().stream().sorted((n1, n2) -> n2.getComputeOrder() - n1.getComputeOrder())
                                .forEach(n -> nodeToExecute.push(Either.right(Pair.of(n, triggerNode))));
                        return triggerNode.self();
                    } else {
                        // lets execute the trigger node later
                        nodeToExecute.push(either);
                        nodeDependenciesGathered.add(triggerNode);
                        for (var nonConditionalNode : gatherNonConditionalDependencies(triggerNode.self())) {
                            nodeToExecute.push(Either.left(nonConditionalNode));
                        }
                    }
                }
            }
            return null;
        }
    }


    private List<BaseNode> gatherNonConditionalDependencies(BaseNode node) {
        if (nonConditionalDependenciesCache.containsKey(node)) {
            return nonConditionalDependenciesCache.get(node);
        }
        var nodes = new ArrayList<BaseNode>();
        var dependencies = new Stack<BaseNode>();
        dependencies.push(node);
        while (!dependencies.isEmpty()) {
            var dependency = dependencies.pop();
            dependency.getInputNodes().stream().filter(n -> !(n instanceof ITriggerableNode)).forEach(dependencies::push);
            if (dependency != node){
                nodes.add(dependency);
            }
        }
        nonConditionalDependenciesCache.put(node, nodes);
        return nodes;
    }

}

