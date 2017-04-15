package jforth;

public final class DoubleLiteral extends BaseWord
{
  public DoubleLiteral(Double number)
  {
    super("", false, false, null);
    this.number = number;
  }

  public int execute(OStack dStack, OStack vStack)
  {
    dStack.push(number);
    return 1;
  }

  private final Double number;
}