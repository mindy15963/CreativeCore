package team.creative.creativecore.common.config.premade.curve;

import net.minecraftforge.api.distmarker.Dist;
import team.creative.creativecore.common.config.api.CreativeConfig;
import team.creative.creativecore.common.config.api.ICreativeConfig;

public class LinearCurve implements ICreativeConfig, Curve {
    
    @CreativeConfig
    public double value;
    
    public LinearCurve(double value) {
        this.value = value;
    }
    
    @Override
    public double valueAt(double x) {
        return value;
    }
    
    @Override
    public void configured(Dist side) {
        
    }
    
}
