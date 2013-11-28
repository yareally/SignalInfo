package com.actionbarsherlock.internal.view.menu;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubMenuWrapper extends MenuWrapper implements SubMenu {
    private final android.view.SubMenu mNativeSubMenu;
    @Nullable
    private MenuItem mItem = null;

    public SubMenuWrapper(android.view.SubMenu nativeSubMenu)
    {
        super(nativeSubMenu);
        mNativeSubMenu = nativeSubMenu;
    }

    @NotNull
    @Override
    public SubMenu setHeaderTitle(int titleRes)
    {
        mNativeSubMenu.setHeaderTitle(titleRes);
        return this;
    }

    @NotNull
    @Override
    public SubMenu setHeaderTitle(CharSequence title) {
        mNativeSubMenu.setHeaderTitle(title);
        return this;
    }

    @NotNull
    @Override
    public SubMenu setHeaderIcon(int iconRes) {
        mNativeSubMenu.setHeaderIcon(iconRes);
        return this;
    }

    @NotNull
    @Override
    public SubMenu setHeaderIcon(Drawable icon) {
        mNativeSubMenu.setHeaderIcon(icon);
        return this;
    }

    @NotNull
    @Override
    public SubMenu setHeaderView(View view) {
        mNativeSubMenu.setHeaderView(view);
        return this;
    }

    @Override
    public void clearHeader() {
        mNativeSubMenu.clearHeader();
    }

    @NotNull
    @Override
    public SubMenu setIcon(int iconRes) {
        mNativeSubMenu.setIcon(iconRes);
        return this;
    }

    @NotNull
    @Override
    public SubMenu setIcon(Drawable icon) {
        mNativeSubMenu.setIcon(icon);
        return this;
    }

    @Nullable
    @Override
    public MenuItem getItem() {
        if (mItem == null) {
            mItem = new MenuItemWrapper(mNativeSubMenu.getItem());
        }
        return mItem;
    }
}
