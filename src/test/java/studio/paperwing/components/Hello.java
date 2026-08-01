package studio.paperwing.components;

import com.artemis.Component;

// note: artemis is looking for the components folder somwhere in your project. no the exact package route.
public class Hello extends Component {
    public String message;
    public void set(String message) {
        this.message = message;
    }
}