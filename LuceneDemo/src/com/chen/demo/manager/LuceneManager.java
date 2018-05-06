package com.chen.demo.manager;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneManager {
	
	public IndexWriter getInstance() throws Exception
	{
		//索引库的位置
		Directory directory=FSDirectory.open(new File("E:\\Lucene&solr\\index"));
		//分析器
		Analyzer analyzer=new IKAnalyzer();
		IndexWriterConfig config=new IndexWriterConfig(Version.LATEST, analyzer);
		return new IndexWriter(directory,config);
	}
	@Test
	public void deleteAll() throws Exception
	{
		IndexWriter indexWriter = getInstance();
		indexWriter.deleteAll();
		indexWriter.close();
	}
	@Test
	public void delete() throws Exception
	{
		IndexWriter indexWriter = getInstance();
		Query query=new TermQuery(new Term("fileContent", "early"));
		indexWriter.deleteDocuments(query);
		indexWriter.close();
	}
	public IndexSearcher getIndexSearcher() throws Exception
	{
		Directory directory=FSDirectory.open(new File("E:\\Lucene&solr\\index"));
		IndexReader indexReader= DirectoryReader.open(directory);
		return new IndexSearcher(indexReader);
	}
	public void showResult(IndexSearcher indexSearcher,Query query) throws IOException
	{
		TopDocs topDocs=indexSearcher.search(query, 5);
		System.out.println(topDocs.totalHits);
		for (ScoreDoc scoreDoc: topDocs.scoreDocs) {
			Document document= indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("fileName"));
			System.out.println(document.get("fileSize"));
			System.out.println(document.get("fileContent"));
			
		}
		
	}
	@Test
	public void testNumericRangeQuery()throws Exception
	{
		IndexSearcher indexSearcher=getIndexSearcher();
		Query query=NumericRangeQuery.newLongRange("fileSize", 0l, 1000l, true, true);
		showResult(indexSearcher, query);
		indexSearcher.getIndexReader().close();
	}
	@Test
	public void testBooleanQuery () throws Exception
	{
		IndexSearcher indexSearcher=getIndexSearcher();
		BooleanQuery booleanQuery=new BooleanQuery();
		Query query=new TermQuery(new Term("fileName","frame"));
		Query query2=new TermQuery(new Term("fileContent","know"));
		booleanQuery.add(query2, Occur.MUST);
		booleanQuery.add(query, Occur.SHOULD);
		System.out.println(booleanQuery);
		showResult(indexSearcher, booleanQuery);
		indexSearcher.getIndexReader().close();
	}
	@Test
	public void testMatchAllDocsQuery() throws Exception
	{
		IndexSearcher indexSearcher=getIndexSearcher();
		Query query= new MatchAllDocsQuery();
		showResult(indexSearcher, query);
		indexSearcher.getIndexReader().close();
	}
	@Test
	public void testQueryParser()throws Exception
	{
		IndexSearcher indexSearcher=getIndexSearcher();
		QueryParser queryParser=new QueryParser("fileName", new IKAnalyzer());
		Query query=queryParser.parse("+fileContent:know fileName:frame");
		showResult(indexSearcher, query);
		indexSearcher.getIndexReader().close();
		
	}

}
