import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;

    private final static String DEFAULT_EVENT = "tabChange";

    public final static String CONTAINER_CLASS = "ui-accordion ui-widget ui-helper-reset ui-accordion-icons";
    public final static String ACTIVE_TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-state-active ui-corner-top";
    public final static String TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-corner-all";
    public final static String TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public final static String ACTIVE_TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public final static String TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

	public Tab findTab(String tabClientId) {
        for(UIComponent component : getChildren()) {
            if(component.getClientId().equals(tabClientId))
                return (Tab) component;
        }

        return null;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("tabChange")) {
                String tabClientId = params.get(clientId + "_newTab");
                TabChangeEvent changeEvent = new TabChangeEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(this.getVar() != null) {
                    int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setRowIndex(index);
                    changeEvent.setData(this.getRowData());
                    changeEvent.setTab((Tab) getChildren().get(0));
                    setRowIndex(-1);
                }

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(changeEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isRequestSource(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isRequestSource(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isRequestSource(context)) {
            super.processUpdates(context);
        }

        ValueExpression expr = this.getValueExpression("activeIndex");
        if(expr != null) {
            expr.setValue(getFacesContext().getELContext(), getActiveIndex());
            resetActiveIndex();
        }
    }

    protected void resetActiveIndex() {
		getStateHelper().remove(PropertyKeys.activeIndex);
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }