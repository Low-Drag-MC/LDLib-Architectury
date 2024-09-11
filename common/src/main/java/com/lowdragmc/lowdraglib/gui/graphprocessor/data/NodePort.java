package com.lowdragmc.lowdraglib.gui.graphprocessor.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.util.concurrent.Runnables;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortInput;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.CustomPortOutput;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.custom.ICustomPortIODelegate;
import com.lowdragmc.lowdraglib.utils.TypeAdapter;
import lombok.Getter;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodePort {
    public static final Table<Class<? extends BaseNode>, String, ICustomPortIODelegate> customPortIODelegateTable = Tables.synchronizedTable(HashBasedTable.create());
    public static ICustomPortIODelegate EMPTY = (node, edges, outputPort) -> {};

    public interface IPushDataDelegate {
        /**
         * Push the data from the input port to the output port.
         * <br>
         * Delegate that is made to send the data from this port to another port connected through an edge
         * This is an optimization compared to dynamically setting values using Reflection (which is really slow)
         */
        void pushData(PortEdge edge);
    }

    /**
     * The actual name of the property behind the port (must be exact, it is used for Reflection)
     */
    public String fieldName;
    /**
     * The node on which the port is
     */
    public BaseNode	owner;
    /**
     * The fieldInfo from the fieldName
     */
    public Field fieldInfo;
    /**
     * Owner of the FieldInfo, to be used in case of Get/SetValue
     */
    public Object fieldOwner;
    /**
     * Data of the port
     */
    public PortData portData;
    @Getter
    private final List<PortEdge> edges = new ArrayList<>();
    private final Map<PortEdge, IPushDataDelegate> pushDataDelegates = new HashMap<>();
    private final List<PortEdge> edgeWithRemoteCustomIO = new ArrayList<>();
    @Nullable
    private ICustomPortIODelegate customPortIOMethod;

    public NodePort(BaseNode owner, String fieldName, PortData portData) throws NoSuchFieldException {
        this(owner, owner, fieldName, portData);
    }

    public NodePort(BaseNode owner, Object fieldOwner, String fieldName, PortData portData) throws NoSuchFieldException {
        this.fieldName = fieldName;
        this.owner = owner;
        this.portData  = portData;
        this.fieldOwner = fieldOwner;
        fieldInfo = fieldOwner.getClass().getDeclaredField(fieldName);
        customPortIOMethod = tryGetCustomPortMethod(owner.getClass(), fieldName);
    }

    @Nullable
    private static ICustomPortIODelegate tryGetCustomPortMethod(Class<? extends BaseNode> nodeClazz, String fieldName) {
        if (!customPortIODelegateTable.contains(nodeClazz, fieldName)) {
            var methods = nodeClazz.getDeclaredMethods();
            var found = false;
            for (var method : methods) {
                var customPortInput = method.isAnnotationPresent(CustomPortInput.class) ? method.getAnnotation(CustomPortInput.class) : null;
                var customPortOutput = method.isAnnotationPresent(CustomPortOutput.class) ? method.getAnnotation(CustomPortOutput.class) : null;
                if (customPortInput == null && customPortOutput == null) continue;
                if (customPortInput != null && customPortOutput != null) {
                    LDLib.LOGGER.error("The method {} in the class {} is annotated with both CustomPortInput and CustomPortOutput, only one is allowed", method, nodeClazz);
                    continue;
                }
                var field = customPortInput != null ? customPortInput.field() : customPortOutput.field();
                if (field.equals(fieldName)) {
                    if (method.getParameterCount() != 2) {
                        LDLib.LOGGER.error("The method {} in the class {} annotated with CustomPortInput or CustomPortOutput must have 2 parameters", method, nodeClazz);
                        continue;
                    }
                    if (!method.getParameterTypes()[0].isAssignableFrom(List.class)) {
                        LDLib.LOGGER.error("The method {} in the class {} annotated with CustomPortInput or CustomPortOutput must have the second parameter of type List", method, nodeClazz);
                        continue;
                    }
                    if (!method.getParameterTypes()[1].isAssignableFrom(NodePort.class)) {
                        LDLib.LOGGER.error("The method {} in the class {} annotated with CustomPortInput or CustomPortOutput must have the third parameter of type NodePort", method, nodeClazz);
                    }
                    method.setAccessible(true);
                    customPortIODelegateTable.put(nodeClazz, fieldName, (owner, edges, outputPort) -> {
                        try {
                            method.invoke(owner, edges, outputPort);
                        } catch (Exception e) {
                            LDLib.LOGGER.error("Error while calling the method {} in the class {} annotated with CustomPortInput or CustomPortOutput", method, nodeClazz, e);
                        }
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                customPortIODelegateTable.put(nodeClazz, fieldName, EMPTY);
            }
        }
        var result = customPortIODelegateTable.get(nodeClazz, fieldName);
        return result == EMPTY ? null : result;
    }


    /**
     * Connect an edge to this port
     */
    public void add(PortEdge edge) {
        if (!edges.contains(edge))
            edges.add(edge);

        if (edge.inputNode == owner) {
            if (edge.outputPort.customPortIOMethod != null)
                edgeWithRemoteCustomIO.add(edge);
        } else {
            if (edge.inputPort.customPortIOMethod != null)
                edgeWithRemoteCustomIO.add(edge);
        }

        //if we have a custom io implementation, we don't need to generate the default one
        if (edge.inputPort.customPortIOMethod != null || edge.outputPort.customPortIOMethod != null) return ;
        pushDataDelegates.put(edge, new DefaultPushDataDelegate(edge));
    }

    /**
     * Disconnect an Edge from this port
     */
    public void remove(PortEdge edge) {
        if (!edges.contains(edge))
            return;

        pushDataDelegates.remove(edge);
        edgeWithRemoteCustomIO.remove(edge);
        edges.remove(edge);
    }

    /**
     * Push the value of the port through the edges This method can only be called on output ports
     */
    public void PushData() {
        if (customPortIOMethod != null) {
            customPortIOMethod.handle(owner, edges, this);
            return ;
        }

        pushDataDelegates.forEach((edge, pushDataDelegate) -> pushDataDelegate.pushData(edge));

        if (edgeWithRemoteCustomIO.isEmpty()) return ;

        //if there are custom IO implementation on the other ports, they'll need our value in the passThrough buffer
        Object ourValue;
        try {
            ourValue = fieldInfo.get(fieldOwner);
        } catch (IllegalAccessException e) {
            LDLib.LOGGER.error("Error while getting the value of the field {} for remove custom IO", fieldInfo, e);
            return;
        }
        for (var portEdge : edgeWithRemoteCustomIO) {
            portEdge.passThroughBuffer = ourValue;
        }
    }

    /**
     * Pull values from the edge (in case of a custom convertion method) This method can only be called on input ports
     */
    public void PullData() {
        if (customPortIOMethod != null) {
            customPortIOMethod.handle(owner, edges, this);
            return ;
        }

        // check if this port have connection to ports that have custom output functions
        if (edgeWithRemoteCustomIO.isEmpty()) return ;

        // Only one input connection is handled by this code, if you want to
        // take multiple inputs, you must create a custom input function see CustomPortsNode.cs
        if (!edges.isEmpty()) {
            var passThroughObject = edges.stream().findFirst().get().passThroughBuffer;

            // We do an extra convertion step in case the buffer output is not compatible with the input port
            if (passThroughObject != null)
                if (TypeAdapter.areAssignable(passThroughObject.getClass(), fieldInfo.getType()))
                    passThroughObject = TypeAdapter.convert(passThroughObject, fieldInfo.getType());

            try {
                fieldInfo.set(fieldOwner, passThroughObject);
            } catch (IllegalAccessException e) {
                LDLib.LOGGER.error("Error while setting the value of the field {} with {} for pull data", fieldInfo, passThroughObject, e);
            }
        }
    }

    /**
     * Reset the value of the field to default if possible
     */
    public void resetToDefault() {
        try {
            // Clear lists, set classes to null and struct to default value.
            if (List.class.isAssignableFrom(fieldInfo.getType())) {
                fieldInfo.setAccessible(true);
                var list = (List) fieldInfo.get(fieldOwner);
                list.clear();
            }
            var type = fieldInfo.getType();
            if (type.isEnum()) {
                fieldInfo.set(fieldOwner, null);
            } else if (type.equals(String.class)) {
                fieldInfo.set(fieldOwner, "");
            } else if (type.isPrimitive()) {
                fieldInfo.set(fieldOwner, 0);
            } else {
                fieldInfo.set(fieldOwner, null);
            }
        } catch (IllegalAccessException e) {
            LDLib.LOGGER.error("Error while resetting the value of the field {}", fieldInfo, e);
        }
    }

    /**
     * Default implementation of the IPushDataDelegate by using Reflection to set the value of the output port to the value of the input port
     */
    public static class DefaultPushDataDelegate implements IPushDataDelegate {
        private PortEdge edge;
        // runtime
        @Nullable
        private Runnable pushDataDelegate;

        public DefaultPushDataDelegate(PortEdge edge) {
            this.edge = edge;
        }

        private Runnable createRunnable() {
            try {
                //Creation of the delegate to move the data from the input node to the output node:
                var inputField = edge.inputNode.getClass().getDeclaredField(edge.inputFieldName);
                var outputField = edge.outputNode.getClass().getDeclaredField(edge.outputFieldName);
                var inType = inputField.getType();
                var outType = outputField.getType();

                if (outType.isAssignableFrom(inType)) {
                    // if outType is assignable from inType, we can directly assign the value
                    inputField.setAccessible(true);
                    return () -> {
                        try {
                            inputField.set(edge.inputNode, outputField.get(edge.outputNode));
                        } catch (IllegalAccessException e) {
                            LDLib.LOGGER.error("Error while pushing data from {} to {}", edge.inputNode, edge.outputNode, e);
                        }
                    };
                } else if (TypeAdapter.areAssignable(outType, inType)) {
                    return () -> {
                        try {
                            inputField.set(edge.inputNode, TypeAdapter.convert(outputField.get(edge.outputNode), inType));
                        } catch (IllegalAccessException e) {
                            LDLib.LOGGER.error("Error while pushing data from {} to {}", edge.inputNode, edge.outputNode, e);
                        }
                    };
                }
            } catch (Exception e) {
                LDLib.LOGGER.error("Error while creating the push data delegate for edge the {}", edge, e);
                return Runnables.doNothing();
            }
            LDLib.LOGGER.error("Error while creating the push data delegate for edge the {}", edge);
            return Runnables.doNothing();
        }

        @Override
        public void pushData(PortEdge edge) {
            if (edge == this.edge) {
                if (pushDataDelegate == null) {
                    pushDataDelegate = createRunnable();
                }
            } else {
                this.edge = edge;
                pushDataDelegate = createRunnable();
            }
            pushDataDelegate.run();
        }
    }
}
