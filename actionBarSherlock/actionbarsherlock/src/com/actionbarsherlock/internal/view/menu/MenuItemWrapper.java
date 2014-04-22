package com.actionbarsherlock.internal.view.menu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import com.actionbarsherlock.internal.view.ActionProviderWrapper;
import com.actionbarsherlock.internal.widget.CollapsibleActionViewWrapper;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.CollapsibleActionView;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MenuItemWrapper implements MenuItem, android.view.MenuItem.OnMenuItemClickListener {
    @Nullable
    private final android.view.MenuItem mNativeItem;
    @Nullable
    private SubMenu                                      mSubMenu                    = null;
    @Nullable
    private OnMenuItemClickListener                      mMenuItemClickListener      = null;
    @Nullable
    private OnActionExpandListener                       mActionExpandListener       = null;
    @Nullable
    private android.view.MenuItem.OnActionExpandListener mNativeActionExpandListener = null;

    public MenuItemWrapper(@Nullable android.view.MenuItem nativeItem)
    {
        if (nativeItem == null) {
            throw new IllegalStateException("Wrapped menu item cannot be null.");
        }
        mNativeItem = nativeItem;
    }

    @Override
    public int getItemId()
    {
        return mNativeItem.getItemId();
    }

    @Override
    public int getGroupId()
    {
        return mNativeItem.getGroupId();
    }

    @Override
    public int getOrder()
    {
        return mNativeItem.getOrder();
    }

    @NotNull
    @Override
    public MenuItem setTitle(CharSequence title)
    {
        mNativeItem.setTitle(title);
        return this;
    }

    @NotNull
    @Override
    public MenuItem setTitle(int title)
    {
        mNativeItem.setTitle(title);
        return this;
    }

    @Override
    public CharSequence getTitle() {
        return mNativeItem.getTitle();
    }

    @NotNull
    @Override
    public MenuItem setTitleCondensed(CharSequence title) {
        mNativeItem.setTitleCondensed(title);
        return this;
    }

    @Nullable
    @Override
    public CharSequence getTitleCondensed() {
        return mNativeItem.getTitleCondensed();
    }

    @NotNull
    @Override
    public MenuItem setIcon(Drawable icon) {
        mNativeItem.setIcon(icon);
        return this;
    }

    @NotNull
    @Override
    public MenuItem setIcon(int iconRes) {
        mNativeItem.setIcon(iconRes);
        return this;
    }

    @Nullable
    @Override
    public Drawable getIcon() {
        return mNativeItem.getIcon();
    }

    @NotNull
    @Override
    public MenuItem setIntent(Intent intent) {
        mNativeItem.setIntent(intent);
        return this;
    }

    @Override
    public Intent getIntent() {
        return mNativeItem.getIntent();
    }

    @NotNull
    @Override
    public MenuItem setShortcut(char numericChar, char alphaChar) {
        mNativeItem.setShortcut(numericChar, alphaChar);
        return this;
    }

    @NotNull
    @Override
    public MenuItem setNumericShortcut(char numericChar) {
        mNativeItem.setNumericShortcut(numericChar);
        return this;
    }

    @Override
    public char getNumericShortcut() {
        return mNativeItem.getNumericShortcut();
    }

    @NotNull
    @Override
    public MenuItem setAlphabeticShortcut(char alphaChar) {
        mNativeItem.setAlphabeticShortcut(alphaChar);
        return this;
    }

    @Override
    public char getAlphabeticShortcut() {
        return mNativeItem.getAlphabeticShortcut();
    }

    @NotNull
    @Override
    public MenuItem setCheckable(boolean checkable) {
        mNativeItem.setCheckable(checkable);
        return this;
    }

    @Override
    public boolean isCheckable() {
        return mNativeItem.isCheckable();
    }

    @NotNull
    @Override
    public MenuItem setChecked(boolean checked) {
        mNativeItem.setChecked(checked);
        return this;
    }

    @Override
    public boolean isChecked() {
        return mNativeItem.isChecked();
    }

    @NotNull
    @Override
    public MenuItem setVisible(boolean visible) {
        mNativeItem.setVisible(visible);
        return this;
    }

    @Override
    public boolean isVisible() {
        return mNativeItem.isVisible();
    }

    @NotNull
    @Override
    public MenuItem setEnabled(boolean enabled) {
        mNativeItem.setEnabled(enabled);
        return this;
    }

    @Override
    public boolean isEnabled() {
        return mNativeItem.isEnabled();
    }

    @Override
    public boolean hasSubMenu() {
        return mNativeItem.hasSubMenu();
    }

    @Nullable
    @Override
    public SubMenu getSubMenu() {
        if (hasSubMenu() && (mSubMenu == null)) {
            mSubMenu = new SubMenuWrapper(mNativeItem.getSubMenu());
        }
        return mSubMenu;
    }

    @NotNull
    @Override
    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
        mMenuItemClickListener = menuItemClickListener;
        //Register ourselves as the listener to proxy
        mNativeItem.setOnMenuItemClickListener(this);
        return this;
    }

    @Override
    public boolean onMenuItemClick(android.view.MenuItem item) {
        if (mMenuItemClickListener != null) {
            return mMenuItemClickListener.onMenuItemClick(this);
        }
        return false;
    }

    @Nullable
    @Override
    public ContextMenuInfo getMenuInfo() {
        return mNativeItem.getMenuInfo();
    }

    @Override
    public void setShowAsAction(int actionEnum) {
        mNativeItem.setShowAsAction(actionEnum);
    }

    @NotNull
    @Override
    public MenuItem setShowAsActionFlags(int actionEnum) {
        mNativeItem.setShowAsActionFlags(actionEnum);
        return this;
    }

    @NotNull
    @Override
    public MenuItem setActionView(@Nullable View view) {
        if (view != null && view instanceof CollapsibleActionView) {
            view = new CollapsibleActionViewWrapper(view);
        }
        mNativeItem.setActionView(view);
        return this;
    }

    @NotNull
    @Override
    public MenuItem setActionView(int resId) {
        //Allow the native menu to inflate the resource
        mNativeItem.setActionView(resId);
        if (resId != 0) {
            //Get newly created view
            View view = mNativeItem.getActionView();
            if (view instanceof CollapsibleActionView) {
                //Wrap it and re-set it
                mNativeItem.setActionView(new CollapsibleActionViewWrapper(view));
            }
        }
        return this;
    }

    @Nullable
    @Override
    public View getActionView() {
        View actionView = mNativeItem.getActionView();
        if (actionView instanceof CollapsibleActionViewWrapper) {
            return ((CollapsibleActionViewWrapper)actionView).unwrap();
        }
        return actionView;
    }

    @NotNull
    @Override
    public MenuItem setActionProvider(ActionProvider actionProvider) {
        mNativeItem.setActionProvider(new ActionProviderWrapper(actionProvider));
        return this;
    }

    @Nullable
    @Override
    public ActionProvider getActionProvider() {
        android.view.ActionProvider nativeProvider = mNativeItem.getActionProvider();
        if (nativeProvider != null && nativeProvider instanceof ActionProviderWrapper) {
            return ((ActionProviderWrapper)nativeProvider).unwrap();
        }
        return null;
    }

    @Override
    public boolean expandActionView() {
        return mNativeItem.expandActionView();
    }

    @Override
    public boolean collapseActionView() {
        return mNativeItem.collapseActionView();
    }

    @Override
    public boolean isActionViewExpanded() {
        return mNativeItem.isActionViewExpanded();
    }

    @NotNull
    @Override
    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        mActionExpandListener = listener;

        if (mNativeActionExpandListener == null) {
            mNativeActionExpandListener = new android.view.MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(android.view.MenuItem menuItem) {
                    if (mActionExpandListener != null) {
                        return mActionExpandListener.onMenuItemActionExpand(MenuItemWrapper.this);
                    }
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(android.view.MenuItem menuItem) {
                    if (mActionExpandListener != null) {
                        return mActionExpandListener.onMenuItemActionCollapse(MenuItemWrapper.this);
                    }
                    return false;
                }
            };

            //Register our inner-class as the listener to proxy method calls
            mNativeItem.setOnActionExpandListener(mNativeActionExpandListener);
        }

        return this;
    }
}
