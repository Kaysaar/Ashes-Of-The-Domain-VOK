package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;

public class OrbitExampleComponent extends ResizableComponent {

    public OrbitExampleComponent(){
        componentPanel = Global.getSettings().createCustom(1,1,this);
    }
}
