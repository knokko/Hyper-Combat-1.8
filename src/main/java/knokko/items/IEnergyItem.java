package knokko.items;

public interface IEnergyItem {
	
	/**
	 * The amount of energy that can be given in 1 tick when there is enough energy available.
	 * @return
	 */
	public int drainSpeed();
}
