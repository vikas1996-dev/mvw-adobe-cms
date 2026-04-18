package com.mvw.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.wcm.api.Page;
import java.util.List;

public class SitemapNavigationItemImpl implements NavigationItem {

    private final NavigationItem delegate;
    private final List<NavigationItem> children;

    public SitemapNavigationItemImpl(NavigationItem delegate,
                                     List<NavigationItem> children) {
        this.delegate = delegate;
        this.children = children;
    }

    @Override
    public Page getPage() {
        return delegate.getPage();
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }

    @Override
    public boolean isCurrent() {
        return delegate.isCurrent();
    }

    @Override
    public int getLevel() {
        return delegate.getLevel();
    }

    @Override
    public List<NavigationItem> getChildren() {
        return children;
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public Link<Page> getLink() {
        return delegate.getLink();
    }

    @Override
    public ComponentData getData() {
        return delegate.getData();
    }

    public boolean isThirdPartyLink(){
        return getPage().getProperties().get("thirdPartyLink", false);
    }
}
