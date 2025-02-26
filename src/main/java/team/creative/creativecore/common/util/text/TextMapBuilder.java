package team.creative.creativecore.common.util.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.network.chat.Component;
import team.creative.creativecore.client.render.text.CompiledText;

public class TextMapBuilder<K> implements ITextCollection {
    
    private LinkedHashMap<K, List<Component>> lines = new LinkedHashMap<>();
    private Predicate<String> filter;
    
    public TextMapBuilder() {
        
    }
    
    public TextMapBuilder<K> setFilter(Predicate<String> predicate) {
        this.filter = predicate;
        return this;
    }
    
    private void addNewLine(K key, Component line) {
        List<Component> newLine = new ArrayList<>();
        newLine.add(line);
        addNewLine(key, newLine);
    }
    
    private void addNewLine(K key, List<Component> line) {
        if (filter != null) {
            StringBuilder builder = new StringBuilder();
            for (Component component : line)
                builder.append(component.getString());
            if (!filter.test(builder.toString()))
                return;
        }
        lines.put(key, line);
    }
    
    public TextMapBuilder addComponent(K[] array, Function<K, Component> toComponent) {
        for (int i = 0; i < array.length; i++)
            addNewLine(array[i], toComponent.apply(array[i]));
        return this;
    }
    
    public TextMapBuilder addComponent(Collection<K> collection, Function<K, Component> toComponent) {
        for (K t : collection)
            addNewLine(t, toComponent.apply(t));
        return this;
    }
    
    public TextMapBuilder addComponents(Collection<K> collection, Function<K, List<Component>> toComponent) {
        for (K t : collection)
            addNewLine(t, toComponent.apply(t));
        return this;
    }
    
    @Override
    public CompiledText[] build() {
        CompiledText[] lines = new CompiledText[this.lines.size()];
        int i = 0;
        for (List<Component> text : this.lines.values()) {
            lines[i] = CompiledText.createAnySize();
            lines[i].setText(text);
            i++;
        }
        return lines;
    }
    
    public List<K> keys() {
        return new ArrayList<>(lines.keySet());
    }
    
}
