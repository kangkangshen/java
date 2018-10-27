package archer.container.support.baseImpl;/*
 *@author:wukang
 */

import archer.container.BeanAliasRegistrar;
import archer.container.support.AliasCircleMappingException;
import archer.container.support.NameAliasMappingChangedException;
import archer.container.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractAliasRegistrar implements BeanAliasRegistrar {
    //使用alias->name的映射结构
    protected Map<String,String> aliasMap=new ConcurrentHashMap<String,String>(16);
    private Set<Character> needDeleteChar=new HashSet<>(4);
    private boolean allowChangeNameMapping;

    public void setAllowChangeNameMapping(boolean allowChangeNameMapping){
        this.allowChangeNameMapping=allowChangeNameMapping;
    }

    @Override
    public void registerAlias(String beanName, String alias) {
        synchronized (aliasMap) {
            if (aliasMap.containsKey(alias) && !allowChangeNameMapping) {
                throw new NameAliasMappingChangedException();
            } else {
                aliasMap.put(alias, beanName);
            }

        }
    }

    @Override
    public void registerAliases(String beanName, String[] aliases) {
        for(String alias:aliases){
            registerAlias(beanName,alias);
        }
    }

    @Override
    public void removeAllAliases(String beanName) {
        String name=formalizeBeanName(beanName);
        if(!aliasMap.containsValue(beanName)){
            return ;
        }
        Set<String> keySet=aliasMap.keySet();
    }

    @Override
    public boolean hasAnyAlias(String beanName) {
        return aliasMap.containsValue(formalizeBeanName(beanName));
    }

    @Override
    public List<String> getAliases(String beanName) {
        String name=formalizeBeanName(beanName);
        if(!aliasMap.containsValue(name)){
            return null;
        }
        List<String> aliases=new LinkedList<>();
        Set<String> aliasKey=aliasMap.keySet();
        for(String alias:aliasKey){
            if(aliasMap.get(alias).equals(name)){
                ((LinkedList<String>) aliases).addFirst(alias);
                aliases.addAll(getAliases(alias));
            }
        }
        return aliases;

    }

    @Override
    public boolean isAlias(String name, String alias) {
        String beanName=formalizeBeanName(name);
        String beanAlias=formalizeBeanName(alias);
        List<String> aliases=getAliases(beanName);
        return aliases.contains(beanAlias);
    }

    @Override
    public String getAutonym(String alias) {
        String autonym=alias;
        while(aliasMap.containsKey(autonym)){
            autonym=aliasMap.get(autonym);
        }
        return autonym;
    }

    public void checkCircleMapping(String name,String alias){
        if(isAlias(alias,name)){
            throw new AliasCircleMappingException("There are cyclic mappings between bean name : "+name+" and alias : "+alias);
        }
    }
    protected String formalizeBeanName(String beanName){
        if(needDeleteChar!=null&&!needDeleteChar.isEmpty()){
            for(char ch:needDeleteChar){
                beanName.replace(ch,' ');
            }
        }
        String result=StringUtils.trimAllWhitespace(beanName);
        return result;
    }


}
