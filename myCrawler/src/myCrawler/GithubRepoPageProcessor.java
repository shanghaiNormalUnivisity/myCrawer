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
	 private String bookScore;//����
	 private String readerNum;//��������
	 
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
             // �������ͼ��url
             page.addTargetRequests(page.getHtml().xpath("//div[@class='info']/a/text()").links()// �޶�ͼ���б��ȡ����
                    .regex("https://book.douban.com/subject/\\d+/")
                    .all());
             // ��ӻ�ҳ��url
             page.addTargetRequests(page.getHtml().xpath("//div[@class='paginator']//span[@class='thispage']/a/text()").links()// �޶���ҳ��
                     .regex("/subject_search?start=\\d+&search_text")
                     .replace("/subject_search", "https://book\\.douban\\.com/subject_search")// �滻�ɾ���·��
                     .all());
             // ÿ��������ҳ
         } else {
             size++;// ����������1
             int scoreNum=Integer.parseInt(page.getHtml().xpath("//div[@class='rating_sum']//span/a//span/text()").toString());
             if(scoreNum>2000)
             {
            	 //��ȡ����
                 //page.putField("bookName", page.getHtml().xpath("//title/text()").toString());
                 page.putField("bookName", page.getHtml().xpath("div[@class='articalTitle']/text()").toString());
                 //div[@class='articalTitle']
                 //��ȡ��������
                 page.putField("scoreNum", page.getHtml().xpath("//div[@class='rating_sum']//span/a//span/text()").toString());
                 //��ȡ���۵÷�
                 page.putField("score", page.getHtml().xpath("//div[@class='rating_self clearfix']/strong/text()").toString());  
             }
            
         }
    }

    @Override
    public Site getSite() {
        return site;
    }

	public static void main(String[] args) {
    	//String urlOne="https://book.douban.com/subject_search?start=0&search_text=������&cat=1001";
    	List<String> list = new ArrayList<String> ();
    	list.add("������");
    	list.add("���");
    	list.add("�㷨");
    	for(int i=0;i<3;i++)
    	{
    		String urlOne="https://book.douban.com/subject_search?start=0&search_text="+list.get(i)+"&cat=1001";
        	//������D:\\webmagic\\
            Spider.create(new GithubRepoPageProcessor()).addUrl(urlOne).addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(5).run();
    	}
    	
    }
}
