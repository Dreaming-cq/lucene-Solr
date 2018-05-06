package com.chen.demo.Lucene;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {
	

	
	@Test
	public void createIndex() throws IOException
	{
		//指定索引库存放的位置
		//FSDirectory 全名File System Directory
		Directory directory=FSDirectory.open(new File("E:\\Lucene&solr\\index"));
		//创建索引时使用的分词器
		//StandardAnalyzer 官方推荐的标准分词器
		Analyzer analyzer=new IKAnalyzer();
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		//需要使用到IndexWriter对象来创建索引
		IndexWriter indexWriter=new IndexWriter(directory, config);
		//获取文档
		File file=new File("E:\\Lucene&solr\\demo");
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			//创建文档对象
			Document document=new Document();
			String fileName=file2.getName();
			//创建域
			Field filed1=new TextField("fileName", fileName, Store.YES);
			long fileSize = FileUtils.sizeOf(file2);
			Field filed2=new LongField("fileSize", fileSize,Store.YES);
			String filePath=file2.getPath();
			Field filed3=new TextField("filePath", filePath, Store.YES);
			String fileContent=FileUtils.readFileToString(file2);
			Field filed4=new TextField("fileContent", fileContent, Store.YES);
			document.add(filed1);
			document.add(filed2);
			document.add(filed3);
			document.add(filed4);
			//将文档对象使用indexWriter对象写入索引库
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	@Test
	public void searchIndex() throws IOException
	{
		Directory directory=FSDirectory.open(new File("E:\\Lucene&solr\\index"));
		IndexReader indexReader=DirectoryReader.open(directory);
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		Query query=new TermQuery(new Term("fileContent", "early"));
		
		TopDocs topDocs=indexSearcher.search(query, 4);
		System.out.println(topDocs.totalHits);
		for (ScoreDoc scoreDoc: topDocs.scoreDocs) {
			Document document= indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("fileName"));
			System.out.println(document.get("fileSize"));
			System.out.println(document.get("fileContent"));
			
		}
		
		indexReader.close();
	}
	// 查看标准分析器的分词效果
		@Test
		public void testTokenStream() throws Exception {
			// 创建一个标准分析器对象
		//Analyzer analyzer = new StandardAnalyzer();
//			Analyzer analyzer = new CJKAnalyzer();
//			Analyzer analyzer = new SmartChineseAnalyzer();
			Analyzer analyzer = new IKAnalyzer();
			// 获得tokenStream对象
			// 第一个参数：域名，可以随便给一个
			// 第二个参数：要分析的文本内容
//			TokenStream tokenStream = analyzer.tokenStream("test",
//					"The Spring Framework provides a comprehensive programming and configuration model.");
			TokenStream tokenStream = analyzer.tokenStream("test",
					"高富帅可以用二维表结构来逻辑表达实现的数据");
			// 添加一个引用，可以获得每个关键词
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			// 添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
			OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
			// 将指针调整到列表的头部
			tokenStream.reset();
			// 遍历关键词列表，通过incrementToken方法判断列表是否结束
			while (tokenStream.incrementToken()) {
				// 关键词的起始位置
				System.out.println("start->" + offsetAttribute.startOffset());
				// 取关键词
				System.out.println(charTermAttribute);
				// 结束位置
				System.out.println("end->" + offsetAttribute.endOffset());
			}
			tokenStream.close();
		}


}
