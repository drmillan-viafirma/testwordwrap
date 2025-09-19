import java.awt.*;
import java.util.List;

public interface TextWrapper {
    List<String> wrap(String string, FontMetrics metrics, int maxWidth) throws IllegalArgumentException;
}
