package cuccovillo.alessio;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CLIOptions implements Serializable {

    static Long serialVersionUUID = 1L;
    private List<String> options;

    public CLIOptions() {

        this.options = Collections.emptyList();
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
