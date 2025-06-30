package www.wheelershigley.me.trade_experience.config;

public class Configuration<T> {
    private final String name;
    private final String description;

    private final T defaultValue;
    private T value;

    public Configuration(String name, T defaultValue) {
        this.name = name;
        this.description = null;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }
    public Configuration(String name, T defaultValue, String description) {
        this.name = name;
        this.description = "# "+description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }
    public Configuration(String name, T defaultValue, String[] descriptions) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;

        StringBuilder builder = new StringBuilder();
        for(int descriptionNumber = 0; descriptionNumber < descriptions.length; descriptionNumber++) {
            builder.append("# ").append(descriptions[descriptionNumber]);
            if(descriptionNumber < descriptions.length-1) {
                builder.append("\r\n");
            }
        }
        this.description = builder.toString();
    }


    public void setValue(Object value) {
        if(value != null) {
            this.value = (T)value;
        }
    }

    public String getName() {
        return this.name;
    }
    public T getValue() {
        return this.value;
    }
    public T getDefaultValue() {
        return this.defaultValue;
    }
    public String getDescription() {
        return this.description;
    }

    public String getDefaultConfiguration() {
        StringBuilder builder = new StringBuilder();
        if(description != null) {
            builder.append(description).append("\r\n");
        }
        builder.append( this.getName() ).append(": ").append( this.getDefaultValue() );

        return builder.toString();
    }
}
