//package com.lowdragmc.lowdraglib.syncdata.managed;
//
//import java.lang.reflect.Array;
//
///**
// * @author KilaBash
// * @date 2023/2/19
// * @implNote ReadOnlyManagedArrayRef
// */
//public class ReadOnlyManagedArrayLikeRef extends ManagedArrayLikeRef{
//
//    public ReadOnlyManagedArrayLikeRef(ReadOnlyManagedField field, boolean lazy) {
//        super(field, lazy);
//    }
//
//    public ReadOnlyManagedField getReadOnlyField() {
//        return ((ReadOnlyManagedField)field);
//    }
//
//    @Override
//    public void update() {
//        Object newValue = getField().value();
//        if (Array.getLength(newValue) != oldLength || getReadOnlyField().isDirty()) {
//            this.oldLength = Array.getLength(newValue);
//            setChanged(true);
//        }
//    }
//
//}
