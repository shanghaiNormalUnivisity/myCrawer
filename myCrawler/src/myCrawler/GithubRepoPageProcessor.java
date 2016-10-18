package myCrawler;
import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
class grabBook
{
	 private String bookName;
	 private String bookScore;//评分
	 private String readerNum;//评价人数
	 
	 public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookScore() {
		return bookScore;
	}

	public void setBookScore(String bookScore) {
		this.bookScore = bookScore;
	}

	public String getReaderNum() {
		return readerNum;
	}

	public void setReaderNum(String readerNum) {
		this.readerNum = readerNum;
	}
}
public class GithubRepoPageProcessor implements PageProcessor {
	private static int size=0;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
    	 if (!page.getUrl().regex("https://book.douban.com/subject/\\d+/").match()) {
             // 添加所有图书url
             page.addTargetRequests(page.getHtml().xpath("//div[@class='info']/a/text()").links()// 限定图书列表获取区域
                    .regex("https://book.douban.com/subject/\\d+/")
                    .all());
             // 添加换页符url
             page.addTargetRequests(page.getHtml().xpath("//div[@class='paginator']//span[@class='thispage']/a/text()").links()// 限定换页符
                     .regex("/subject_search?start=\\d+&search_text")
                     .replace("/subject_search", "https://book\\.douban\\.com/subject_search")// 替换成绝对路径
                     .all());
             // 每本书详情页
         } else {
             size++;// 文章数量加1
             int scoreNum=Integer.parseInt(page.getHtml().xpath("//div[@class='rating_sum']//span/a//span/text()").toString());
             if(scoreNum>2000)
             {
            	 //获取书名
                 //page.putField("bookName", page.getHtml().xpath("//title/text()").toString());
                 page.putField("bookName", page.getHtml().xpath("div[@class='articalTitle']/text()").toString());
                 //div[@class='articalTitle']
                 //获取评价人数
                 page.putField("scoreNum", page.getHtml().xpath("//div[@class='rating_sum']//span/a//span/text()").toString());
                 //获取评价得分
                 page.putField("score", page.getHtml().xpath("//div[@class='rating_self clearfix']/strong/text()").toString());  
             }
            
         }
    }

    @Override
    public Site getSite() {
        return site;
    }

	public static void main(String[] args) {
    	//String urlOne="https://book.douban.com/subject_search?start=0&search_text=互联网&cat=1001";
    	List<String> list = new ArrayList<String> ();
    	list.add("互联网");
    	list.add("编程");
    	list.add("算法");
    	for(int i=0;i<3;i++)
    	{
    		String urlOne="https://book.douban.com/subject_search?start=0&search_text="+list.get(i)+"&cat=1001";
        	//保存在D:\\webmagic\\
            Spider.create(new GithubRepoPageProcessor()).addUrl(urlOne).addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(5).run();
    	}
    	
    }
}
