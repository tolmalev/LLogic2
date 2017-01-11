package ru.llogic.ui.tool;

import ru.llogic.ui.DocumentManager;

/**
 * @author tolmalev
 */
public abstract class ToolBase {
    protected final DocumentManager documentManager;

    private boolean active;

    protected ToolBase(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void disactivate() {
        active = false;
    }
}
