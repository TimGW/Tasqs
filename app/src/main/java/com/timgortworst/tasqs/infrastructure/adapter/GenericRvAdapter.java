package com.timgortworst.tasqs.infrastructure.adapter;

import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.timgortworst.tasqs.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A general purpose adapter where items can be added of any kind as long as an appropriate
 * viewholderbinder is added along the items.
 *
 * todo convert to kotlin
 */
public class GenericRvAdapter extends RecyclerView.Adapter {

    protected List<Pair<List<?>, ViewHolderBinder>> mItemList = new ArrayList<>();

    protected List<StableIdProvider> mStableIdsProviders = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (Pair<List<?>, ViewHolderBinder> pair : mItemList) {
            if (viewType == pair.second.getClass().hashCode()) {
                return pair.second.createViewHolder(parent);
            }
        }
        if (BuildConfig.DEBUG) {
            throw new IllegalStateException("No ViewholderBinder matching was found");
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        for (Pair<List<?>, ViewHolderBinder> pair : mItemList) {
            if (position < pair.first.size()) {
                pair.second.bind(holder, pair.first.get(position));
                return;
            }
            position -= pair.first.size();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Pair<List<?>, ViewHolderBinder> pair : mItemList) {
            count += pair.first.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0) return -1;

        for (Pair<List<?>, ViewHolderBinder> pair : mItemList) {
            if (position < pair.first.size()) {
                return pair.second.getClass().hashCode();
            }
            position -= pair.first.size();
        }
        
        return -1;
    }

    /**
     * Method to add items to the adapter, items added are ordered by which is added first.
     *
     * @param objects
     * @param viewHolderBinder
     */
    public <T> void addItems(int index, List<T> objects, ViewHolderBinder<T,? extends RecyclerView.ViewHolder> viewHolderBinder){
        mItemList.add(index, new Pair<List<?>, ViewHolderBinder>(objects, viewHolderBinder));
    }

    /**
     * Method to add a single item to the adapter, items added are ordered by which is added first.
     *
     * @param object
     * @param viewHolderBinder
     */
    public <T> void addItem(T object, ViewHolderBinder<T,? extends RecyclerView.ViewHolder> viewHolderBinder){
        addItems(mItemList.size(), Collections.singletonList(object), viewHolderBinder);
    }


    public <T> void addItem(int index, T object, ViewHolderBinder<T,? extends RecyclerView.ViewHolder> viewHolderBinder){
        addItems(index, Collections.singletonList(object), viewHolderBinder);
    }

    public <T> void updateItem(int index, T newObject, ViewHolderBinder<T,? extends RecyclerView.ViewHolder> viewHolderBinder){
        mItemList.set(index, new Pair(Collections.singletonList(newObject), viewHolderBinder));
    }

    /**
     * Get the item representing this position
     * @param position the adapter position
     * @return the item
     */
    @Nullable
    public Object getItem(int position) {
        if (position < 0) return null;
        for (Pair<List<?>, ViewHolderBinder> pair : mItemList) {
            if (position < pair.first.size()) {
                return pair.first.get(position);
            }
            position -= pair.first.size();
        }
        return null;
    }

    public void addStableIdsProvider(@NonNull StableIdProvider stableIdsProvider) {
        mStableIdsProviders.add(stableIdsProvider);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        ViewHolderBinder viewHolderBinder = getViewHolderBinder(holder.getAdapterPosition());
        if (viewHolderBinder == null) return;

        viewHolderBinder.onAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        ViewHolderBinder viewHolderBinder = getViewHolderBinder(holder.getAdapterPosition());
        if (viewHolderBinder == null) return;

        viewHolderBinder.onDetachedToWindow(holder);
    }

    @Override
    public long getItemId(int position) {
        if (mStableIdsProviders != null && !mStableIdsProviders.isEmpty()) {
            Object item = getItem(position);
            ViewHolderBinder viewHolderBinder = getViewHolderBinder(position);
            for (StableIdProvider stableIdsProvider : mStableIdsProviders) {
                Long itemId = stableIdsProvider.getItemId(item, viewHolderBinder);
                if (itemId != null) {
                    return itemId;
                }
            }
        }
        if (BuildConfig.DEBUG) {
            throw new IllegalStateException("Id not provided when needed position=" + position);
        }
        return super.getItemId(position);
    }

    private ViewHolderBinder getViewHolderBinder(int position) {
        if (position < 0) return null;
        for (Pair<List<?>, ViewHolderBinder> pair : mItemList) {
            if (pair.first == null) continue;
            if (position < pair.first.size()) {
                return pair.second;
            }
            position -= pair.first.size();
        }
        return null;
    }
}
