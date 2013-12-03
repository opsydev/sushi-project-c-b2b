import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"itemNumber", "supplier", "quote", "confirmationNumber", "quantity"})
public class finalOrderItemBean
{
	private String itemNumber,tns, confirmationNumber,supplier;
	private Integer quantity;
	private double quote;
	
	@XmlElement(name="supplier")
	public String getSupplier()
	{
		return supplier;
	}

	public void setSupplier(String supplier)
	{
		this.supplier = supplier;
	}

	@XmlElement(name="confirmationNumber")
	public String getConfirmationNumber()
	{
		return confirmationNumber;
	}
	
	public void setConfirmationNumber(String confirmationNumber)
	{
		this.confirmationNumber = confirmationNumber;
	}
	
	@XmlElement(name="itemNumber")
	public String getItemNumber()
	{
		return itemNumber;
	}
	public void setItemNumber(String itemNumber)
	{
		this.itemNumber = itemNumber;
	}
	
	@XmlTransient
	public String getTns()
	{
		return tns;
	}
	public void setTns(String tns)
	{
		this.tns = tns;
	}
	@XmlElement(name="quantity")
	public Integer getQuantity()
	{
		return quantity;
	}
	public void setQuantity(Integer quantity)
	{
		this.quantity = quantity;
	}
	
	@XmlElement(name="Quote")
	public double getQuote()
	{
		return quote;
	}
	public void setQuote(double quote)
	{
		this.quote = quote;
	}

	
}
