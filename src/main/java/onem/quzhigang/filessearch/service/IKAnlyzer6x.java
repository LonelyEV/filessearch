package onem.quzhigang.filessearch.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * 
 * ClassName: IKAnlyzer6x <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年1月10日 下午5:06:15 <br/>
 *
 * @author 屈志刚  
 * @version 
 * @since JDK 1.8
 */
public class IKAnlyzer6x extends Analyzer{
	
	private boolean useSmart;

	public boolean isUseSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}
	
	public IKAnlyzer6x(){
		this(false);
	}
	
	public IKAnlyzer6x(boolean useSmart){
		super();
		this.useSmart = useSmart;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer _IKTokenizer =  new IKTonkenizer6x(this.useSmart);
		return new TokenStreamComponents(_IKTokenizer);
	}


}
