package onem.quzhigang.filessearch.service;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 
 * ClassName: IKTonkenizer6x <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年1月10日 下午5:05:54 <br/>
 *
 * @author 屈志刚  
 * @version 
 * @since JDK 1.8
 */
public class IKTonkenizer6x extends Tokenizer {
	
	private IKSegmenter _IKImplement;  //IK分词器实现
	
	private final CharTermAttribute termAttr;  //词元文本属性
	
	private final OffsetAttribute offsetAttr; //词元位移属性
	
	private final TypeAttribute typeAttr; //词元分类属性
	
	private int endPosition; //记录最后一个词元的位置
	
	public IKTonkenizer6x(boolean useSmart){
		
		super();
		offsetAttr = addAttribute(OffsetAttribute.class);
		termAttr = addAttribute(CharTermAttribute.class);
		typeAttr = addAttribute(TypeAttribute.class);
		_IKImplement = new IKSegmenter(input, useSmart);
	}
	

	@Override
	public boolean incrementToken() throws IOException {
		
		clearAttributes();  //消除所以词元属性
		
		Lexeme nextLexme = _IKImplement.next();
		
		if(nextLexme != null){
			
			//设置词元文本
			termAttr.append(nextLexme.getLexemeText());
			//设置词元长度
			termAttr.setLength(nextLexme.getLength());
			//设置词元位移
			offsetAttr.setOffset(nextLexme.getBeginPosition(), nextLexme.getEndPosition());
			//记录词元的最后位置
			endPosition = nextLexme.getEndPosition();
			//记录词元分类
			typeAttr.setType(nextLexme.getLexemeText());
			return true;  //返回true 告知有下个词元
		}
		
		return false;  //返回false 告知词元输出完毕
	}
	
	@Override
	public void reset() throws IOException {
		super.reset();
		_IKImplement.reset(input);
	}
	
	@Override
	public void end() throws IOException {
		
		int finalOffset = correctOffset(this.endPosition);
		offsetAttr.setOffset(finalOffset, finalOffset);
	}
	
	

}
