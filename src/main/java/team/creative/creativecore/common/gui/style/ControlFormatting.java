package team.creative.creativecore.common.gui.style;

public class ControlFormatting {
    
    public static final ControlFormatting PROGRESSBAR = new ControlFormatting(ControlStyleBorder.SMALL, 0, ControlStyleFace.BAR);
    public static final ControlFormatting CLICKABLE = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.CLICKABLE);
    public static final ControlFormatting NESTED = new ControlFormatting(ControlStyleBorder.SMALL, 2, ControlStyleFace.NESTED_BACKGROUND);
    public static final ControlFormatting SLIDER = new ControlFormatting(ControlStyleBorder.SMALL, 0, ControlStyleFace.NESTED_BACKGROUND);
    public static final ControlFormatting GUI = new ControlFormatting(ControlStyleBorder.BIG, 5, ControlStyleFace.BACKGROUND);
    public static final ControlFormatting TRANSPARENT = new ControlFormatting(ControlStyleBorder.NONE, 0, ControlStyleFace.NONE);
    public static final ControlFormatting TRANSPARENT_NO_DISABLE = new ControlFormatting(ControlStyleBorder.NONE, 0, ControlStyleFace.NONE, false);
    
    public final ControlStyleBorder border;
    public final int padding;
    public final ControlStyleFace face;
    public final boolean hasDisabledEffect;
    
    public ControlFormatting(ControlStyleBorder border, int padding, ControlStyleFace face) {
        this.border = border;
        this.padding = padding;
        this.face = face;
        this.hasDisabledEffect = true;
    }
    
    public ControlFormatting(ControlStyleBorder border, int padding, ControlStyleFace face, boolean hasDisabledEffect) {
        this.border = border;
        this.padding = padding;
        this.face = face;
        this.hasDisabledEffect = hasDisabledEffect;
    }
    
    public static enum ControlStyleBorder {
        
        BIG,
        SMALL,
        NONE;
        
    }
    
    public static enum ControlStyleFace {
        
        BAR,
        CLICKABLE,
        NESTED_BACKGROUND,
        BACKGROUND,
        NONE;
        
    }
    
}
