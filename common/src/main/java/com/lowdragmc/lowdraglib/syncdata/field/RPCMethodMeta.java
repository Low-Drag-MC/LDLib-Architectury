package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.syncdata.accessor.ManagedAccessor;
import com.lowdragmc.lowdraglib.syncdata.managed.ManagedHolder;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.rpc.RPCSender;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class RPCMethodMeta {
    @Getter
    private final String name;
    private final ManagedAccessor[] argsAccessor;
    private final Class<?>[] argsType;
    private final Method method;
    private final boolean isFirstArgSender;

    public RPCMethodMeta(Method method) {
        this.method = method;
        method.setAccessible(true);
        this.name = method.getName();

        var args = method.getParameters();

        if (args.length == 0) {
            argsAccessor = new ManagedAccessor[0];
            argsType = new Class[0];
            isFirstArgSender = false;
        } else {

            var firstArg = args[0];
            if (RPCSender.class.isAssignableFrom(firstArg.getType())) {
                argsAccessor = new ManagedAccessor[args.length - 1];
                argsType = new Class[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    var arg = args[i];
                    argsAccessor[i - 1] = getAccessor(arg.getType());
                    argsType[i - 1] = arg.getType();
                }
                isFirstArgSender = true;
            } else {
                argsAccessor = new ManagedAccessor[args.length];
                argsType = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    var arg = args[i];
                    argsAccessor[i] = getAccessor(arg.getType());
                    argsType[i] = arg.getType();
                }
                isFirstArgSender = false;
            }

        }
    }


    public void invoke(Object instance, RPCSender sender, ITypedPayload<?>[] payloads) {
        if(argsAccessor.length != payloads.length) {
            throw new IllegalArgumentException("Invalid number of arguments, expected " + argsAccessor.length + " but got " + payloads.length);
        }
        Object[] args;
        if (isFirstArgSender) {
            args = new Object[argsAccessor.length + 1];
            args[0] = sender;
            for (int i = 0; i < argsAccessor.length; i++) {
                args[i + 1] = deserialize(payloads[i], argsType[i], argsAccessor[i]);
            }
        } else {
            args = new Object[argsAccessor.length];
            for (int i = 0; i < argsAccessor.length; i++) {
                args[i] = deserialize(payloads[i], argsType[i], argsAccessor[i]);
            }
        }

        try {
            method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ITypedPayload<?>[] serializeArgs(Object[] args) {
        if(argsAccessor.length != args.length) {
            throw new IllegalArgumentException("Invalid number of arguments, expected " + argsAccessor.length + " but got " + args.length);
        }
        var payloads = new ITypedPayload[argsAccessor.length];
        for (int i = 0; i < argsAccessor.length; i++) {
            payloads[i] = serialize(args[i], argsAccessor[i]);
        }
        return payloads;
    }


    private static ManagedAccessor getAccessor(Type type) {
        var accessor = TypedPayloadRegistries.findByType(type);
        if (accessor == null) {
            throw new IllegalArgumentException("Cannot find accessor for type " + type);
        }
        if (accessor instanceof ManagedAccessor) {
            return (ManagedAccessor) accessor;
        }
        throw new IllegalArgumentException("Accessor for type " + type + " is not a ManagedAccessor");
    }

    private static Object deserialize(ITypedPayload<?> payload, Class<?> type, ManagedAccessor accessor) {
        var cache = ManagedHolder.ofType(type);
        accessor.writeManagedField(AccessorOp.PERSISTED, cache, payload);
        return cache.value();
    }

    private static ITypedPayload<?> serialize(Object value, ManagedAccessor accessor) {
        var cache = ManagedHolder.of(value);
        return accessor.readManagedField(AccessorOp.PERSISTED, cache);
    }
}
