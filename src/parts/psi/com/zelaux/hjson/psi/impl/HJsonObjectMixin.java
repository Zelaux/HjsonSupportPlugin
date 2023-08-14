package com.zelaux.hjson.psi.impl;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.impl.JsonContainerImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.zelaux.hjson.psi.HJsonContainer;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class HJsonObjectMixin extends HJsonValueImpl implements HJsonObject, HJsonContainer {
    private final CachedValueProvider<Map<String, HJsonMember>> myPropertyCache =
            () -> {
                final Map<String, HJsonMember> cache = new HashMap<>();
                for (HJsonMember property : getMemberList()) {
                    final String propertyName = property.getName();
                    // Preserve the old behavior - return the first value in findProperty()
                    if (!cache.containsKey(propertyName)) {
                        cache.put(propertyName, property);
                    }
                }
                // Cached value is invalidated every time file containing this object is modified
                return CachedValueProvider.Result.createSingleDependency(cache, this);
            };

    public HJsonObjectMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public HJsonMember findMember(@NotNull String name) {
        return CachedValuesManager.getCachedValue(this, myPropertyCache).get(name);
    }
}
