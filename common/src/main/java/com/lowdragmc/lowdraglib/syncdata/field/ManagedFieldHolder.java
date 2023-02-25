package com.lowdragmc.lowdraglib.syncdata.field;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.ManagedFieldUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

public class ManagedFieldHolder {

    private final Map<String, ManagedKey> fieldNameMap = new HashMap<>();
    private ManagedKey[] fields;
    private Map<String, RPCMethodMeta> rpcMethodMap = new HashMap<>();

    /**
     * @param clazz the class to get the sync field keys from
     */
    public ManagedFieldHolder(Class<? extends IManaged> clazz) {
        this.clazz = clazz;
        this.initAll();
    }


    public void merge(ManagedFieldHolder other) {
        this.fields = ArrayUtils.addAll(this.fields, other.fields);
        this.resetSyncFieldIndexMap();
        this.rpcMethodMap.putAll(other.rpcMethodMap);
    }

    /**
     * merge the sync field keys from the given class
     *
     * @param clazz  the class to get the sync field keys from
     * @param parent the parent class to get the sync field keys from
     */
    public ManagedFieldHolder(Class<? extends IManaged> clazz, ManagedFieldHolder parent) {
        this(clazz);
        merge(parent);
    }

    private final Class<? extends IManaged> clazz;


    private void initAll() {
        this.fields = ManagedFieldUtils.getManagedFields(clazz);
        resetSyncFieldIndexMap();
        this.rpcMethodMap = ManagedFieldUtils.getRPCMethods(clazz);
    }

    private void resetSyncFieldIndexMap() {
        fieldNameMap.clear();
        for (ManagedKey key : fields) {
            if (fieldNameMap.containsKey(key.getName())) {
                LDLib.LOGGER.warn("Duplicate sync field name: " + key.getName());
                continue;
            }
            fieldNameMap.put(key.getName(), key);
        }
    }

    public ManagedKey[] getFields() {
        return fields;
    }

    public Map<String, RPCMethodMeta> getRpcMethodMap() {
        return rpcMethodMap;
    }

    public ManagedKey getSyncedFieldIndex(String name) {
        if (!fieldNameMap.containsKey(name)) {
            throw new IllegalArgumentException("No sync field with name " + name);
        }
        return fieldNameMap.get(name);
    }
}
