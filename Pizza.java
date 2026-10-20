interface Pizza{
    double getPrice();
    String getDescription();
}
class PlainPizza implements Pizza{
    public String getDescription(){
        return "Plain Dough & Sauce";
    }
    public double getPrice(){
        return 5;
    }
}
abstract class ToppingDecorator implements Pizza{
    Pizza pizza;
    ToppingDecorator(Pizza pizza){
        this.pizza = pizza;
    }
    public String getDescription(){
        return pizza.getDescription();
 }
    public double getPrice(){
        return pizza.getPrice();
    }
}
class Cheese extends ToppingDecorator{
    Cheese(Pizza pizza){
        super(Pizza);
    }
    public String getDescription(){
        return getDescription() + " Cheese";
    }
    public double getPrice(){
        return getPrice() + 2.0;
    }
}
class Olives extends ToppingDecorator{
    Olives(Pizza pizza){
        super(Pizza);
    }
    public String getDescription(){
        return getDescription() + " Olives";
    }
    public double getPrice(){
        return getPrice() + 1.0;
    }
}
class Pineapple extends ToppingDecorator{
    Pineapple(Pizza pizza){
        super(Pizza);
    }
    public String getDescription(){
        return getDescription() + " Pineapple";
    }
    public double getPrice(){
        return getPrice() + 1.5;
    }
}


