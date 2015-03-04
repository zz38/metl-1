package org.jumpmind.symmetric.is.ui.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jumpmind.symmetric.is.core.model.AbstractObjectWithSettings;
import org.jumpmind.symmetric.is.core.model.ComponentVersion;
import org.jumpmind.symmetric.is.core.model.Connection;
import org.jumpmind.symmetric.is.core.model.Setting;
import org.jumpmind.symmetric.is.core.model.SettingDefinition;
import org.jumpmind.symmetric.is.core.model.SettingDefinition.Type;
import org.jumpmind.symmetric.is.core.persist.IConfigurationService;
import org.jumpmind.symmetric.is.core.runtime.component.IComponentFactory;
import org.jumpmind.symmetric.is.core.runtime.connection.IConnectionFactory;
import org.jumpmind.symmetric.ui.common.ImmediateUpdateTextField;
import org.jumpmind.symmetric.ui.common.SqlField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class DesignPropertySheet extends Panel implements ValueChangeListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    IComponentFactory componentFactory;

    IConfigurationService configurationService;

    IConnectionFactory connectionFactory;

    public DesignPropertySheet(IComponentFactory componentFactory,
            IConfigurationService configurationService, IConnectionFactory connectionFactory) {
        this.componentFactory = componentFactory;
        this.configurationService = configurationService;
        this.connectionFactory = connectionFactory;
        setCaption("Property Sheet");
        setSizeFull();
        addStyleName("noborder");
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        valueChange(event.getProperty().getValue());
    }
    
    protected void valueChange(Object obj) {
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth(100, Unit.PERCENTAGE);
        formLayout.setMargin(false);
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        if (obj != null) {
            Map<String, SettingDefinition> settings = buildSettings(obj);
            Set<String> keys = settings.keySet();
            for (String key : keys) {
                SettingDefinition definition = settings.get(key);
                if (obj instanceof AbstractObjectWithSettings) {
                    addSettingField(key, definition, (AbstractObjectWithSettings) obj,
                            formLayout);
                }
            }
        }
        setContent(formLayout);
    }

    protected Map<String, SettingDefinition> buildSettings(Object obj) {
        if (obj instanceof ComponentVersion) {
            ComponentVersion version = (ComponentVersion) obj;
            return componentFactory.getSettingDefinitionsForComponentType(version.getComponent()
                    .getType());
        } else if (obj instanceof Connection) {
            Connection connection = (Connection) obj;
            return connectionFactory.getSettingDefinitionsForConnectionType(connection
                    .getType());
        } else {
            return new HashMap<String, SettingDefinition>();
        }
    }

    protected void addSettingField(final String key, final SettingDefinition definition,
            final AbstractObjectWithSettings obj, FormLayout formLayout) {
        boolean required = definition.required();
        String description = "Represents the " + key + " setting";
        Type type = definition.type();
        switch (type) {
            case BOOLEAN:
                final CheckBox checkBox = new CheckBox(definition.label());
                checkBox.setImmediate(true);
                checkBox.setValue(obj.getBoolean(key));
                checkBox.setRequired(required);
                checkBox.setDescription(description);
                checkBox.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        saveSetting(key, checkBox, obj);
                    }
                });
                formLayout.addComponent(checkBox);
                break;
            case CHOICE:
                final AbstractSelect choice = new ComboBox(definition.label());
                choice.setImmediate(true);
                String[] choices = definition.choices();
                for (String c : choices) {
                    choice.addItem(c);
                }
                choice.setValue(obj.get(key, definition.defaultValue()));
                choice.setDescription(description);
                choice.setNullSelectionAllowed(false);
                choice.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        saveSetting(key, choice, obj);
                    }
                });
                formLayout.addComponent(choice);
                break;
            case SQL:
                final SqlField sqlField = new SqlField();
                sqlField.setRequired(required);
                sqlField.setDescription(description);
                sqlField.setValue(obj.get(key));
                sqlField.setCaption(definition.label());
                sqlField.addValueChangeListener(new ValueChangeListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        saveSetting(key, sqlField, obj);
                    }
                });
                formLayout.addComponent(sqlField);
                break;
            case PASSWORD:
                // TODO
                break;
            case INTEGER:
                ImmediateUpdateTextField integerField = new ImmediateUpdateTextField(
                        definition.label()) {
                    private static final long serialVersionUID = 1L;

                    protected void save() {
                        saveSetting(key, this, obj);
                    };
                };
                integerField.setConverter(Integer.class);
                integerField.setValue(obj.get(key));
                integerField.setRequired(required);
                integerField.setDescription(description);
                formLayout.addComponent(integerField);
                break;
            case STRING:
                ImmediateUpdateTextField textField = new ImmediateUpdateTextField(
                        definition.label()) {
                    private static final long serialVersionUID = 1L;

                    protected void save() {
                        saveSetting(key, this, obj);
                    };
                };
                textField.setValue(obj.get(key));
                textField.setRequired(required);
                textField.setDescription(description);
                formLayout.addComponent(textField);
                break;
            case XML:
                // TODO - similar to sql
                break;
            default:
                break;

        }

    }

    protected void saveSetting(String key, Field<?> field, AbstractObjectWithSettings obj) {
        Setting data = obj.findSetting(key);
        data.setValue(field.getValue() != null ? field.getValue().toString() : null);
        configurationService.save(data);
        // componentSettingsChangedListener.componentSettingsChanges(flowNode,
        // false);
    }

}
