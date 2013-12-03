import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="order")
public class finalOrderBean
{
	public finalOrderBean()
	{
		
	}
	public finalOrderBean(List<finalOrderItemBean> list )
	{
		this.list = list;
	}
	private List<finalOrderItemBean> list;
	
	@XmlElement(name="items")
	public List<finalOrderItemBean> getList()
	{
		return list;
	}

	public void setList(List<finalOrderItemBean> list)
	{
		this.list = list;
	}

	
}
