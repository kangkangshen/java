package archer.definition;/*
 *@author:wukang
 */

/*
增加验证功能，作为bean实例化的基础beanDefinition
 */
public class RootBeanDefinition extends BaseBeanDefinitionHolder {
    private BaseBeanDefinitionHolder wrappedInst;
    private MethodOverrides overrides;
    private static final String[] METHOD_OVERRIDE_TYPE={"lookup","replace"};
    public RootBeanDefinition(BaseBeanDefinitionHolder holder) {
        super(holder.getBeanClass());
        this.wrappedInst=holder;
        this.overrides=null;
    }

    public MethodOverrides getMethodOverrides() {
        if(wrappedInst!=null){
            if(overrides!=null){
                return overrides;
            }else{

            }
        }
        return null;
    }

    public void prepareMethodOverrides() throws Exception{
    }
    public BaseBeanDefinitionHolder getBeanDefinition(){
        return this.wrappedInst;
    }
}
