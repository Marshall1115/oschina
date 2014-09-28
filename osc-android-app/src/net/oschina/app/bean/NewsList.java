package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 新闻列表实体类
 * 		
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月27日 下午5:55:58
 *
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class NewsList extends Entity implements ListEntity {

	public final static int CATALOG_ALL = 1;
	public final static int CATALOG_INTEGRATION = 2;
	public final static int CATALOG_SOFTWARE = 3;
	
	@XStreamAlias("catalog")
	private int catalog;
	
	@XStreamAlias("pagesize")
	private int pageSize;
	
	@XStreamAlias("newscount")
	private int newsCount;
	
	@XStreamAlias("newslist")
	private List<News> newslist = new ArrayList<News>();

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(int newsCount) {
		this.newsCount = newsCount;
	}
	
	public List<News> getNewslist() {
		return newslist;
	}

	public void setNewslist(List<News> newslist) {
		this.newslist = newslist;
	}

	@Override
	public List<?> getList() {
		return newslist;
	}
}
