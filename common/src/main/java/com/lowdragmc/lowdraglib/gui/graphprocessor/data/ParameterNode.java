package com.lowdragmc.lowdraglib.gui.graphprocessor.data;

import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortBehavior;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.OutputPort;

import java.util.Collections;
import java.util.List;

public class ParameterNode extends BaseNode {
    @InputPort
    public Object input;
    @OutputPort
    public Object output;

    @Override
    public String getName() {
        return "Parameter";
    }

    // We serialize the GUID of the exposed parameter in the graph so we can retrieve the true ExposedParameter from the graph
    public String parameterGUID;

//    public ExposedParameter parameter { get; private set; }
//
//    public event Action onParameterChanged;

    public ParameterAccessor accessor;

//    @Override
//    protected void enable() {
//        // load the parameter
//        LoadExposedParameter();
//
//        graph.onExposedParameterModified += OnParamChanged;
//        if (onParameterChanged != null)
//            onParameterChanged?.Invoke();
//    }
//
//    protected void LoadExposedParameter() {
//        parameter = graph.GetExposedParameterFromGUID(parameterGUID);
//
//        if (parameter == null) {
//            Debug.Log("Property \"" + parameterGUID + "\" Can't be found !");
//
//            // Delete this node as the property can't be found
//            graph.removeNode(this);
//            return;
//        }
//
//        output = parameter.value;
//    }
//
//    protected void OnParamChanged(ExposedParameter modifiedParam)
//    {
//        if (parameter == modifiedParam)
//        {
//            onParameterChanged?.Invoke();
//        }
//    }

    @CustomPortBehavior(field = "output")
    protected List<PortData> GetOutputPort(List<PortEdge> edges) {
        if (accessor == ParameterAccessor.Get) {
            return List.of(new PortData()
                    .identifier("output")
                    .displayName("Value")
                    .displayType(Object.class)
                    .acceptMultipleEdges(true));
        }
        return Collections.emptyList();
    }


    @CustomPortBehavior(field = "input")
    protected List<PortData> GetInputPort(List<PortEdge> edges) {
        if (accessor == ParameterAccessor.Set) {
            return List.of(new PortData()
                    .identifier("input")
                    .displayName("Value")
                    .displayType(Object.class));
        }
        return Collections.emptyList();
    }

//    @Override
//    protected void process() {
//        ClearMessages();
//        if (parameter == null)
//        {
//            AddMessage($"Parameter not found: {parameterGUID}", NodeMessageType.Error);
//            return;
//        }
//
//        if (accessor == ParameterAccessor.Get)
//            output = parameter.value;
//        else
//            graph.UpdateExposedParameter(parameter.guid, input);
//    }

    public enum ParameterAccessor {
        Get,
        Set
    }
}


