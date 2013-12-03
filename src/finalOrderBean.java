import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="order")
public class finalOrderBean
{
	@XmlAttribute(name="order_date")
	private Date orderPlacedAt;
	public finalOrderBean()
	{
		
	}
	public finalOrderBean(List<finalOrderItemBean> list, Date d )
	{
		this.list = list;
		this.orderPlacedAt = d;
	}
	private List<finalOrderItemBean> list;
	
	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	public List<finalOrderItemBean> getList()
	{
		return list;
	}

	public void setList(List<finalOrderItemBean> list)
	{
		this.list = list;
	}

	
}
