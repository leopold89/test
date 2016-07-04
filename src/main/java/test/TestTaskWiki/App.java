package test.TestTaskWiki;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import test.TestTaskWiki.model.Offer;
import test.TestTaskWiki.service.Parser;

public class App {
	
	private static Set<Offer>modifOffers = new HashSet<>();
	private static AtomicInteger a = new AtomicInteger(); 
	
    public static void main(String[] args) {
    	
    	String newFile = args[0];
    	String oldFile = args[1];
    	
    	ExecutorService executor = Executors.newFixedThreadPool(50);
    	List<Future<String>> futures = new ArrayList<>();
    	
    	
    	try {
			Map<Integer, Offer> newOffers = Parser.parse(newFile);
			Map<Integer, Offer> oldOffers = Parser.parse(oldFile);
			
			System.out.println(newOffers.size());
			
			for (Entry<Integer, Offer> en : newOffers.entrySet()) {
				Offer newOffer = en.getValue();
				Offer oldOffer = oldOffers.get(en.getKey());
				
				if (oldOffer == null) {
					newOffer.setMark("n");
					modifOffers.add(newOffer);
				} else if (!newOffer.getHash().equals(oldOffer.getHash())) {
					newOffer.setMark("m");
					modifOffers.add(newOffer);
				} 
				oldOffers.remove(en.getKey());
				
//				chekOffers.add(newOffer);
//				
//				if (chekOffers.size() >= 100) {
//					
//					Set<Offer> set = new HashSet<>(chekOffers);
//					
//					executor.execute(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								checkPicture(set);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					});
//					
//					chekOffers.clear();
//				}
				
				futures.add(executor.submit(new Callable<String>() {

					@Override
					public String call() throws Exception {
						checkPicture(newOffer);
						return "";
					}
				}));
			}
			
			for (Offer removedOffer : oldOffers.values()) {
				removedOffer.setMark("r");
				modifOffers.add(removedOffer);
			}
			
//			for (Offer o : newOffers.values()) {
////				checkPicture(o);
//				executor.execute(new Runnable() {
//					
//					@Override
//					public void run() {
//						try {
//							checkPicture(o);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
//			}
			
//			executor.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						checkPicture(chekOffers);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
			
			executor.shutdown();
			
			for (Future<String> future : futures) {
				future.get();
			}
			
			modifOffers.forEach(n -> System.out.println(n.getId() + " " + n.getMark()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }

	private static void checkPicture(Offer newOffer) {
		String pic = null;

		for (String s : newOffer.getPictures())
			pic = s;
		if (pic == null || pic.isEmpty())
			return;
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(pic);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);

			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println(Thread.currentThread().getName() + ": "
						+ newOffer.getId() + "=" + responseCode);
				newOffer.setMark("p");
				modifOffers.add(newOffer);
			}

		} catch (IOException e) {
			System.out.println(pic);
			System.out.println(e);
			newOffer.setMark("p");
			modifOffers.add(newOffer);
//			System.out.println(a.addAndGet(1));
		} finally {
			conn.disconnect();
		}

		
//		HttpClient client = HttpClientBuilder.create().setConnectionTimeToLive(100, TimeUnit.MILLISECONDS).build();
//		HttpGet request = new HttpGet(pic);
//		try {
//			request.setHeader("Connection", "close");
//			HttpResponse response = client.execute(request);
//			if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
//				System.out.println(Thread.currentThread().getName() + ": "
//						+ newOffer.getId() + "=" + response.getStatusLine().getStatusCode());
//			
//		} catch (IOException e) {
//			System.out.println(pic);
//			System.out.println(e);
//			System.out.println(a.addAndGet(1));
//		} finally {
//			request.completed();
//			request.releaseConnection();
//		}
//		
//		
////		CloseableHttpClient httpclient = HttpClients.createDefault();
//		HttpGet httpget = new HttpGet(pic);
////		CloseableHttpResponse response = httpclient.execute(httpget);
//		try (CloseableHttpClient httpclient = HttpClients.createDefault();CloseableHttpResponse response = httpclient.execute(httpget);) {
//			if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
//				System.out.println(Thread.currentThread().getName() + ": "
//						+ newOffer.getId() + "=" + response.getStatusLine().getStatusCode());
//		} catch (IOException e) {
//			System.out.println(pic);
//			System.out.println(e);
//			System.out.println(a.addAndGet(1));
//		} finally {
////		    response.close();
//		    httpget.releaseConnection();
////		    httpclient.close();
//		}
//		
	}
	
	private static void checkPicture(Set<Offer> offers) throws ClientProtocolException, IOException, InterruptedException, URISyntaxException {
		
//		HttpGet httpget = null;
//		try (CloseableHttpClient httpclient = HttpClients.custom().build();) {
//			httpget = new HttpGet();
//			httpget.setHeader("Connection", /*"close"*/"keep-alive");
//			
//			for (Offer newOffer : offers) {
//				
//				for (String pic : newOffer.getPictures()) {
//					
//					if (pic == null || pic.isEmpty())
//						continue;
//					
//					httpget.setHeader("Request URL", pic);
//					httpget.setURI(new URI(pic));
////					System.out.println(httpget.getURI());
////					System.out.println("Request:" + Arrays.toString(httpget.getAllHeaders()));
//					
//					CloseableHttpResponse response = httpclient.execute(httpget);
//					
////					System.out.println("Response:" + Arrays.toString(response.getAllHeaders()));
////					Thread.currentThread().sleep(10000);
//					
////					System.out.println(response.getStatusLine());
//					
//					if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
//						System.out.println(Thread.currentThread().getName() + ": "
//								+ newOffer.getId() + "=" + response.getStatusLine().getStatusCode());
//					response.close();
//				}
//			}
//		} catch (IOException e) {
//			System.out.println(e);
//			System.out.println(a.addAndGet(1));
//		} finally {
//			httpget.completed();
//		    httpget.releaseConnection();
//		}
		
//		HttpClientContext context = HttpClientContext.create();
//		HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
//		HttpClientConnection conn = null;
//		
//		for (Offer newOffer : offers) {
//
//			for (String pic : newOffer.getPictures()) {
//
//				try {
//					HttpRoute route = new HttpRoute(new HttpHost(pic));
//					ConnectionRequest connRequest = connMrg.requestConnection(route, null);
//					conn = connRequest.get(1, TimeUnit.SECONDS);
//					if (!conn.isOpen()) {
//						connMrg.connect(conn, route, 1000, context);
//						connMrg.routeComplete(conn, route, context);
//					}
//				} catch (ExecutionException e) {
//					e.printStackTrace();
//				} finally {
//					connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
//				}
//			}
//		}

	}

}
