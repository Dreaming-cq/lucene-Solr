package com.chen.solr.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrTest {
	/** 
	   * @Title : TestAdd 
	   * @��������: TODO �������
	   * @�����ߣ���ǿ  
	   * @������ @throws Exception 
	   * @�������ͣ�void 
	   * @throws ��
	   */
	@Test
	public void TestAdd()throws Exception
	{
		String url="http://localhost:8080/solr/";
		SolrServer solrServer=new HttpSolrServer(url);
		SolrInputDocument doc=new SolrInputDocument();
		doc.addField("name", "value");
		doc.addField("id", "haha");
		solrServer.add(doc);
		solrServer.commit();
		
		
		
		
	}
	/** 
	   * @Title : TestDeleteIndex 
	   * @��������: TODO ����ɾ��
	   * @�����ߣ���ǿ  
	   * @������ @throws SolrServerException
	   * @������ @throws IOException 
	   * @�������ͣ�void 
	   * @throws ��
	   */
	@Test
	public void TestDeleteIndex() throws SolrServerException, IOException
	{

		String url="http://localhost:8080/solr/";
		SolrServer solrServer=new HttpSolrServer(url);
		//UpdateResponse response = solrServer.deleteById("haha");
	
		solrServer.deleteByQuery("id:haha");
		solrServer.commit();
	}
	/** 
	   * @Title : Test1 
	   * @��������: TODO ��������
	   * @�����ߣ���ǿ  
	   * @������ @throws Exception 
	   * @�������ͣ�void 
	   * @throws ��
	   */
	@Test
	 public void Test1() throws Exception
	 {
		 	String url="http://localhost:8080/solr/";
		 	SolrServer solrServer=new HttpSolrServer(url);
		 	//ʹ��SolrQuery
			SolrQuery query=new SolrQuery();
			//���ֲ�ѯ���� ��q�� ��query
			// 1.query.set("q","id:*");
			query.setQuery("id:*");
			QueryResponse response = solrServer.query(query);
			SolrDocumentList results = response.getResults();
			System.out.println(results.size());
			for (SolrDocument solrDocument : results) {
				System.out.println(solrDocument.getFieldValue("information_name"));
				System.out.println(solrDocument.getFieldValue("information_sex"));
				System.out.println(solrDocument.getFieldValue("information_address"));
				System.out.println(solrDocument.getFieldValue("id"));
				
			}
	 }
	/** 
	   * @Title : test2 
	   * @��������: TODO ��ϲ�ѯ�����˲�ѯ������ָ�����ѯ
	   * @�����ߣ���ǿ  
	   * @������ @throws Exception 
	   * @�������ͣ�void 
	   * @throws ��
	   */
	@Test
	public void test2() throws Exception
	{

	 	String url="http://localhost:8080/solr/";
	 	SolrServer solrServer=new HttpSolrServer(url);
	 	//ʹ��SolrQuery
		SolrQuery query=new SolrQuery();
		query.setQuery("id:*");
		//���ù�����������fq���� filter query
		query.setFilterQueries("information_sex:boy");
		//����
		query.addSort("id", ORDER.desc);
		// ֻ��ָ������
		query.set("fl","id,information_name");
		QueryResponse response = solrServer.query(query);
		SolrDocumentList results = response.getResults();
		System.out.println(results.size());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.getFieldValue("information_name"));
			System.out.println(solrDocument.getFieldValue("information_sex"));
			System.out.println(solrDocument.getFieldValue("information_address"));
			System.out.println(solrDocument.getFieldValue("id"));
			
		}
	}
	/** 
	   * @Title : TestSearch 
	   * @��������: TODO ��������ҳ
	   * @�����ߣ���ǿ  
	   * @������ @throws Exception 
	   * @�������ͣ�void 
	   * @throws ��
	   */
	@Test
	public void TestSearch()throws Exception
	{
		String url="http://localhost:8080/solr/";
		SolrServer solrServer=new HttpSolrServer(url);
		SolrQuery query=new SolrQuery();
		//query.add("q", "id:*");
		query.setQuery("id:*");
		//fq filter query
		//query.set("fq", "id:haha");
		//	query.setFilterQueries("id:haha");
		//query.addSort("id",ORDER.desc);
		query.setStart(0);
		query.setRows(3);
		//
		query.setHighlight(true);
		query.addHighlightField("information_name");
		query.setHighlightSimplePre("<span style='color:red'>");
		query.setHighlightSimplePost("</span>");
		QueryResponse response = solrServer.query(query);
		SolrDocumentList results = response.getResults();
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		System.out.println(results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.getFieldValue("information_name"));
			System.out.println(solrDocument.getFieldValue("information_sex"));
			System.out.println(solrDocument.getFieldValue("information_address"));
			System.out.println(solrDocument.getFieldValue("id"));
			 Map<String, List<String>> map = highlighting.get(solrDocument.getFieldValue("id"));
			 List<String> list = map.get("information_name");
			 System.out.println(list.get(0));
		}
		/*QueryResponse response = solrServer.query(query);
		SolrDocumentList results = response.getResults();
		System.out.println(results.size());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.getFieldValue("information_name"));
			System.out.println(solrDocument.getFieldValue("information_sex"));
			System.out.println(solrDocument.getFieldValue("information_address"));
			System.out.println(solrDocument.getFieldValue("id"));
			
		}*/
	}

}
