package com.inertiaclient.base.utils;

import java.util.*;

public class CollectionUtils {

    public static <T> HashMap<T, Integer> arraylistIndexToHashMapValue(ArrayList<T> arraylist) {
        HashMap<T, Integer> hashMap = new HashMap<T, Integer>(arraylist.size());

        for (int i = 0; i < arraylist.size(); i++) {
            hashMap.put(arraylist.get(i), i);
        }
        return hashMap;
    }

    /*
     * Checks if a hashmap has some key, if it doesn't it creates a new hashmap entry
     * then it adds the value to the arraylist assoicated with the key
     */
    public static <K, V> void addToArrayListHashMap(HashMap<K, ArrayList<V>> hashMap, K key, V value) {
        ArrayList<V> foundArrayList = hashMap.get(key);
        if (foundArrayList == null) {
            foundArrayList = new ArrayList<V>();
            hashMap.put(key, foundArrayList);
        }

        foundArrayList.add(value);
    }

    /*
     * Probably won't ever use again, but it's here!!
     */
    public static <K, V> void addMultipleToArrayListHashMap(HashMap<K, ArrayList<V>> hashMap, K[] keys, V value) {
        for (K key : keys) {
            CollectionUtils.addToArrayListHashMap(hashMap, key, value);
        }
    }

    public static <E> ArrayList<E> createArrayList(E initialValue) {
        ArrayList<E> arrayList = new ArrayList<E>();
        arrayList.add(initialValue);

        return arrayList;
    }

    public static <E> HashSet<E> arrayToHashset(E[] array) {
        HashSet<E> hashSet = new HashSet<>();
        for (E e : array) {
            hashSet.add(e);
        }
        return hashSet;
    }

    public static boolean isLastIndex(int size, int index) {
        return index >= size - 1;
    }

    public static boolean isLastIndex(List<?> list, int index) {
        return CollectionUtils.isLastIndex(list.size(), index);
    }

    public static int getCycledIndex(List<?> list, int newIndex) {
        if (newIndex < 0) {
            return list.size() - 1;
        }
        if (newIndex >= list.size()) {
            return 0;
        }
        return newIndex;
    }

    public static <T> T[] array(T... values) {
        return values;
    }

}