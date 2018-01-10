package onem.quzhigang.filessearch.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import onem.quzhigang.filessearch.model.FileModel;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

/**
 * 
 * ClassName: IndexHandleService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年1月10日 下午2:04:18 <br/>
 * 索引处理器
 * @author 屈志刚  
 * @version 
 * @since JDK 1.8
 */
public class IndexHandleService {
	
	/**
	 * 本地文件目录
	 */
	private static File FILE_PATH = null;
	
	/**
	 * 索引存放目录
	 */
	private static Path INDEX_PATH = null;
	
	
	static{
		try {
			FILE_PATH = ResourceUtils.getFile("classpath:files");
			INDEX_PATH = Paths.get(ResourceUtils.getFile("classpath:indexdir").getAbsolutePath());
		} catch (FileNotFoundException e) {
			System.out.println("文件路径不存在！");
			e.printStackTrace();
			
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		
		
		/**
		 * 1.把需要测试的文档文件放到 classpath:/resource/files 目录中
		 * 2.执行 createIndex()方法为文档创建索引
		 * 3.执行query(keyWords:要查询的关键字, 10:返回记录数);
		 * 
		 * 
		 */
		//createIndex();  //创建索引
		//query("沙僧", 10); //查询文档
		
	}
	
	
	/**
	 * 
	 * query:(文档查询). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 *
	 * @author 屈志刚  
	 * @param keyWords	查询关键字
	 * @param size      返回记录数
	 * @return
	 * @since JDK 1.8
	 */
	public static List<FileModel> query(String keyWords, int size){
		 
		
		List<FileModel> fileList = new ArrayList<FileModel>();
		//检索域
		String [] fields = {"title", "content"};

		Directory dir;
		
		try{
			dir = FSDirectory.open(INDEX_PATH);
			IndexReader reader =  DirectoryReader.open(dir);
			IndexSearcher indexSearch = new IndexSearcher(reader);
			Analyzer analyzer = new IKAnlyzer6x();
			
			QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
			
			//QueryParser parser = new MultiFieldQueryParser
			//查询字符串
			Query query = parser.parse(keyWords);
			TopDocs topDocs = indexSearch.search(query, size);
			//定制高亮标签
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(""
					+ "<span style=\"color:red;\">","</span>");
			
			QueryScorer scorerTitle = new QueryScorer(query, fields[0]);
			Highlighter hlTitle = new Highlighter(scorerTitle);
			
			QueryScorer scorerContent = new QueryScorer(query, fields[1]);
			Highlighter hlContent = new Highlighter(scorerContent);
			
			TopDocs hits = indexSearch.search(query, 100);
			
			 for(ScoreDoc scoreDoc : topDocs.scoreDocs){
				 
				  Document doc = indexSearch.doc(scoreDoc.doc);
				  String title = doc.get("title");
				  String content = doc.get("content");
				  
				  TokenStream tokenStream = TokenSources.getAnyTokenStream(indexSearch.getIndexReader(), scoreDoc.doc, fields[0], new IKAnlyzer6x());
				  Fragmenter fragmenter = new SimpleSpanFragmenter(scorerTitle);
				  hlTitle.setTextFragmenter(fragmenter);
				  String hl_title = hlTitle.getBestFragment(tokenStream, title);
				  
				  //获取高亮片段对其数量进行限制
				  tokenStream  = TokenSources.getAnyTokenStream(indexSearch.getIndexReader(), scoreDoc.doc, fields[1], new IKAnlyzer6x());
				  fragmenter = new SimpleSpanFragmenter(scorerContent);
				  hlContent.setTextFragmenter(fragmenter);
				  String hl_content = hlContent.getBestFragment(tokenStream, content);
				  
				  FileModel fileModel = new FileModel(hl_title != null ? hl_title : title, 
						  hl_content != null ? hl_content : content);
				  
				  System.out.println("title : "+ fileModel.getTitle());
				  
				  fileList.add(fileModel);
			 }
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
			
		}
		
		return fileList;
		
		
	}
	
	/**
	 * 
	 * createIndex:(创建索引). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 *
	 * @author 屈志刚  
	 * @since JDK 1.8
	 */
	public static void createIndex() {
		
		Directory dir = null;
		IndexWriter inWrite = null;
		
		try{
			
			Analyzer analyzer = new IKAnlyzer6x(); 
			
			IndexWriterConfig icw = new IndexWriterConfig(analyzer);
			
			icw.setOpenMode(OpenMode.CREATE);
			
			FieldType fileType = new FieldType();
			fileType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			fileType.setStored(true);
			fileType.setTokenized(true);
			fileType.setStoreTermVectorOffsets(true);
			fileType.setStoreTermVectorPositions(true);
			fileType.setStoreTermVectors(true);
			
			if(!Files.isReadable(INDEX_PATH)){
				System.out.println(INDEX_PATH+"：文件路径不存在！");
			}
			
			dir = FSDirectory.open(INDEX_PATH);
			inWrite = new IndexWriter(dir, icw);
			
			List<FileModel> fileList = showFiles();
			
			for(FileModel fileModel: fileList){
				
				Document doc = new Document();
				doc.add(new Field("title", fileModel.getTitle(), fileType));
				doc.add(new Field("content", fileModel.getContent(), fileType));
				
				inWrite.addDocument(doc);			
				
			}
			
		}catch(IOException ioe){
			
		}catch (Exception e) {
			
		}finally{
			try {
				inWrite.commit();
				inWrite.close();
				dir.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		
		System.out.println("index builded sourcess!");
	}
	
	/**
	 * 
	 * showFiles:(将目标文件夹下所有文件转换为FileModel对象集合). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 *
	 * @author 屈志刚  
	 * @return
	 * @throws IOException
	 * @since JDK 1.8
	 */
	public static List<FileModel> showFiles() throws IOException{
		
		List<FileModel> list = new ArrayList<FileModel>();
		
		File[] files = FILE_PATH.listFiles();
		
		for(File file : files){
			FileModel fileModel = new FileModel(file.getName(),parserExtraction(file));
			list.add(fileModel);
		}
		
		return list;
	}
	
	
	/**
	 * 
	 * parserExtraction:(获取文件内容). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 *
	 * @author 屈志刚  
	 * @param file
	 * @return
	 * @since JDK 1.8
	 */
	public static String parserExtraction(File file){
		
		String fileContent = "";
		
		BodyContentHandler handler = new BodyContentHandler();
		
		Parser parser =  new AutoDetectParser();
		
		Metadata metadate = new Metadata();
		
		FileInputStream inputStream;
		
		try {
			inputStream = new FileInputStream(file);
			ParseContext content = new ParseContext();
			parser.parse(inputStream, handler, metadate, content);
			fileContent = handler.toString();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		
		return fileContent;
	}
	
	/**
	 * 
	 * inputstreamtofile:(inputStream 转  file). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * TODO(这里描述这个方法的使用方法 – 可选).<br/>
	 * TODO(这里描述这个方法的注意事项 – 可选).<br/>
	 *
	 * @author 屈志刚  
	 * @param ins
	 * @param file
	 * @since JDK 1.8
	 */
	public static void inputstreamtofile(InputStream ins,File file) {
		  try {
			   OutputStream os = new FileOutputStream(file);
			   int bytesRead = 0;
			   byte[] buffer = new byte[8192];
			   while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
			    os.write(buffer, 0, bytesRead);
			   }
			   os.close();
			   ins.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
	}

}
