package archer.util;



public enum NamingPattern {

    CamelClass("^([A-Z][a-z]+)$"),CamelBean("^[a-z]+([A-Z][a-z]+)$"),DBTable("");
    private String reg;
    private NamingPattern(String reg){
        this.reg=reg;
    }
    public void transfor(){}
}
