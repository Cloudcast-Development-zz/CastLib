package club.cloudcastgames.castlib.command.param;

import lombok.Getter;

public class ParameterData {
    @Getter private String name;
    @Getter private boolean wildcard;
    @Getter private String defaultValue;
    @Getter private String[] tabCompleteFlags;
    @Getter private Class<?> paramterClass;

    public ParameterData(Param parameter, Class<?> paramterClass) {
        this.name = parameter.name();
        this.wildcard = parameter.wildcard();
        this.defaultValue = parameter.defaultValue();
        this.tabCompleteFlags = parameter.tabCompleteFlags();
        this.paramterClass = paramterClass;
    }
}
