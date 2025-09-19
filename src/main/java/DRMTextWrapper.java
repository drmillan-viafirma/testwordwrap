import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum DRMTextWrapper implements TextWrapper {
    INSTANCE;
    private DRMTextWrapper(){

    }
    public List<String> wrap(String string, FontMetrics metrics, int maxWidth) {
        // Separamos en líneas, y procesamos cada línea por separado
        return Arrays.stream(string.split("\n")).map(s->wrapLine(s,metrics,maxWidth)).flatMap(List::stream).collect(Collectors.toList());
    }
    public List<String> wrapLine(String line, FontMetrics metrics, int maxWidth) {
        List<String> result= new java.util.ArrayList<>();
        // Si la linea es menor que el maxWidth, devolverla tal cual
        if(metrics.stringWidth(line)<=maxWidth){
            result.add(line);
            return result;
        }
        // Separamos por " "
        String[] words= line.split(" ");
        // Precalculo el tamaño del espacio
        int spaceWidth=metrics.stringWidth(" ");
        int offset=0;
        // Aquí vamos a ir almacenando la línea.
        StringBuilder currentLine=new StringBuilder();
        for(String word:words) {
            int currentWordLength=metrics.stringWidth(word);
            // Si el offset que tenemos + el tamaño de esta palabra es menor que el ancho, añadimos la palabra
            // y movemos el offset
            if(offset+currentWordLength+spaceWidth<maxWidth){
                currentLine.append(word).append(" ");
                offset+=currentWordLength+spaceWidth;
            }
            else
            {
                // Si la palabra que queremos meter es mayor que el maxWidth, vamos añadiendo la palabra
                // carácter a carácter y calculando la longitud de la linea, hay fuentes que en ligadura
                // dos carácteres juntos ocupan menos que separados, así que no podemos cachear el ancho
                // de caracter.
                if(currentWordLength>maxWidth){
                    // Mientras la palabra actual no esté vacía, quitamos el primer carácter y
                    // lo metemos en la linea, y ponemos el offset en el ancho de la linea+espacio
                    while(!word.isEmpty()) {
                        int currentLineLength = metrics.stringWidth(currentLine.toString());
                        // Ir añadiendo caracteres hasta que se alcance el maxWidth
                        while (!word.isEmpty() && currentLineLength + spaceWidth < maxWidth ) {
                            currentLine.append(word.charAt(0));
                            word = word.substring(1);
                            // Calculamos el largo de la línea actual
                            currentLineLength = metrics.stringWidth(currentLine.toString());
                            // Si un único carácter provoca que la palabra sea demasiado ancha, avisar de fallo y salir
                            if(word.length()==1 && currentLineLength>maxWidth){
                                throw new IllegalArgumentException("No solution, single char wont fit");
                            }
                            // Movemos el offset
                            offset = currentLineLength + spaceWidth;
                        }
                        // Añadimos un espacio
                        if(!currentLine.isEmpty()) {
                            currentLine.append(" ");
                            // Si ahora la línea actual es mayor que el maxWidth, añadirla al resultado y empezar una nueva línea
                            if (metrics.stringWidth(currentLine.toString()) + spaceWidth > maxWidth) {
                                result.add(currentLine.toString().trim());
                                currentLine.setLength(0);
                                offset = 0;
                            }
                        }else{
                            // No hay solución , un único carácter no cabe
                            throw new IllegalArgumentException("No solution, single char wont fit");
                        }
                    }
                }
                // Si la palabra es menor que el maxWidth, añadir la línea actual al resultado y empezar una nueva línea
                else {
                    result.add(currentLine.toString().trim());
                    currentLine.setLength(0);
                    currentLine.append(word).append(" ");
                    offset=currentWordLength+spaceWidth;
                }
            }
        }
        // Si nos hemos dejado algo en el current line, lo añadimos
        if(!currentLine.isEmpty()){
            result.add(currentLine.toString().trim());
        }
        return result;
    }
}