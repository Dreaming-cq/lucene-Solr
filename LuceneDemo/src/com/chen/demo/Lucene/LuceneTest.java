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
		//ָ���������ŵ�λ��
		//FSDirectory ȫ��File System Directory
		Directory directory=FSDirectory.open(new File("E:\\Lucene&solr\\index"));
		//��������ʱʹ�õķִ���
		//StandardAnalyzer �ٷ��Ƽ��ı�׼�ִ���
		Analyzer analyzer=new IKAnalyzer();
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		//��Ҫʹ�õ�IndexWriter��������������
		IndexWriter indexWriter=new IndexWriter(directory, config);
		//��ȡ�ĵ�
		File file=new File("E:\\Lucene&solr\\demo");
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			//�����ĵ�����
			Document document=new Document();
			String fileName=file2.getName();
			//������
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
			//���ĵ�����ʹ��indexWriter����д��������
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
	// �鿴��׼�������ķִ�Ч��
		@Test
		public void testTokenStream() throws Exception {
			// ����һ����׼����������
		//Analyzer analyzer = new StandardAnalyzer();
//			Analyzer analyzer = new CJKAnalyzer();
//			Analyzer analyzer = new SmartChineseAnalyzer();
			Analyzer analyzer = new IKAnalyzer();
			// ���tokenStream����
			// ��һ����������������������һ��
			// �ڶ���������Ҫ�������ı�����
//			TokenStream tokenStream = analyzer.tokenStream("test",
//					"The Spring Framework provides a comprehensive programming and configuration model.");
			TokenStream tokenStream = analyzer.tokenStream("test",
					"�߸�˧�����ö�ά��ṹ���߼����ʵ�ֵ�����");
			// ���һ�����ã����Ի��ÿ���ؼ���
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			// ���һ��ƫ���������ã���¼�˹ؼ��ʵĿ�ʼλ���Լ�����λ��
			OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
			// ��ָ��������б��ͷ��
			tokenStream.reset();
			// �����ؼ����б�ͨ��incrementToken�����ж��б��Ƿ����
			while (tokenStream.incrementToken()) {
				// �ؼ��ʵ���ʼλ��
				System.out.println("start->" + offsetAttribute.startOffset());
				// ȡ�ؼ���
				System.out.println(charTermAttribute);
				// ����λ��
				System.out.println("end->" + offsetAttribute.endOffset());
			}
			tokenStream.close();
		}


}
