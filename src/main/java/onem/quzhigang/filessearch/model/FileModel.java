package onem.quzhigang.filessearch.model;

/**
 * 
 * ClassName: FileModel <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年1月10日 下午5:07:19 <br/>
 *
 * @author 屈志刚  
 * @version 
 * @since JDK 1.8
 */
public class FileModel {
	
	private String title;  //文件标题
	
	private String content;  //文件内容

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public FileModel(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	public FileModel() {
		super();
	}

	@Override
	public String toString() {
		return "FileModel [title=" + title + ", content=" + content + "]";
	}
	
	
	
	
	

}
