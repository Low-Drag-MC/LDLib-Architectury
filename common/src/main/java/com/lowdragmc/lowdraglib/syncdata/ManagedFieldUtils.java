package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.syncdata.annotation.*;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import com.lowdragmc.lowdraglib.syncdata.field.RPCMethodMeta;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagedFieldUtils {

    public static ManagedKey[] getManagedFields(Class<?> clazz) {
        List<ManagedKey> managedFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.isAnnotationPresent(Persisted.class) || field.isAnnotationPresent(DescSynced.class)) {
                var managedKey = createKey(field);

                var persisted = field.getAnnotation(Persisted.class);
                if (persisted != null) {
                    managedKey.setPersistentKey(persisted.key());
                }
                managedFields.add(managedKey);

            }
        }
        return managedFields.toArray(ManagedKey[]::new);
    }


    public static Map<String, RPCMethodMeta> getRPCMethods(Class<?> clazz) {
        Map<String, RPCMethodMeta> result = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.isAnnotationPresent(RPCMethod.class)) {
                var rpcMethod = new RPCMethodMeta(method);
                result.put(rpcMethod.getName(), rpcMethod);
            }
        }
        return result;
    }

    public static ManagedKey createKey(Field field) {
        boolean isLazy = field.isAnnotationPresent(LazyManaged.class);
        boolean isDestSync = field.isAnnotationPresent(DescSynced.class);
        boolean isGuiSync = field.isAnnotationPresent(GuiSynced.class);
        boolean isPersist = field.isAnnotationPresent(Persisted.class);
        boolean isDrop = field.isAnnotationPresent(DropSaved.class);
        String name = field.getName();
        Type type = field.getGenericType();

        return new ManagedKey(name, isDestSync, isGuiSync, isPersist, isDrop, isLazy, type, field);
    }

    public record FieldRefs(IRef[] syncedRefs, IRef[] persistedRefs, IRef[] nonLazyFields,
                            Map<ManagedKey, IRef> fieldRefMap) {

    }

    public interface FieldChangedCallback {
        void onFieldChanged(int indexInSync, int indexInPersist, boolean changed);
    }


    public static FieldRefs getFieldRefs(ManagedKey[] keys, Object obj, FieldChangedCallback callback) {
        List<IRef> syncedFields = new ArrayList<>();
        List<IRef> persistedFields = new ArrayList<>();
        List<IRef> nonLazyFields = new ArrayList<>();
        Map<ManagedKey, IRef> fieldRefMap = new HashMap<>();
        for (ManagedKey key : keys) {
            var fieldObj = key.createRef(obj);
            fieldObj.setChanged(true);
            fieldRefMap.put(key, fieldObj);
            if (!fieldObj.isLazy()) {
                nonLazyFields.add(fieldObj);
            }
            int syncIndex = -1;
            int persistIndex = -1;
            if (key.isDestSync()) {
                syncIndex = syncedFields.size();
                syncedFields.add(fieldObj);
            }
            if (key.isPersist()) {
                persistIndex = persistedFields.size();
                persistedFields.add(fieldObj);
            }
            int finalSyncIndex = syncIndex;
            int finalPersistIndex = persistIndex;
            fieldObj.setChangeListener((changed) -> callback.onFieldChanged(finalSyncIndex, finalPersistIndex, changed));
        }
        return new FieldRefs(
                syncedFields.toArray(IRef[]::new),
                persistedFields.toArray(IRef[]::new),
                nonLazyFields.toArray(IRef[]::new),
                fieldRefMap
        );
    }

}
