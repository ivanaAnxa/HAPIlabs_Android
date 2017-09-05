package com.anxa.hapilabs.activities.fragments;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;

@SuppressLint("NewApi")
public class WebkitFragments extends Fragment{
	
	Context context;
	OnClickListener listener;
	WebView webView;
	String url;
	
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View rootView = inflater.inflate(R.layout.fragment_myweight, container, false);
	        webView = (WebView)rootView.findViewById(R.id.webview);
	        
	        setWebViewSettings();
	        return rootView;
	    }
	  
	  public void setUrl(String url){
		  this.url = url;
	  }

		private class MyWebViewClient extends WebViewClient {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
		    }
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}
		
		
		@SuppressLint("SetJavaScriptEnabled")
		private void setWebViewSettings(){
			   // workaround so that the default browser doesn't take over
		       webView.setWebViewClient(new MyWebViewClient());
		       webView.setWebChromeClient(new WebChromeClient());
		       
		       webView.setVerticalScrollbarOverlay(true);
		       webView.setFocusableInTouchMode(true);
		       webView.setFocusable(true);
		       webView.setBackgroundColor(0);
		       webView.requestFocus(View.FOCUS_DOWN);
		       
		    	
				
			  WebSettings webSettings = webView.getSettings();
		       webSettings.setUseWideViewPort(true);
		       webSettings.setDomStorageEnabled(true);
		       webSettings.setSaveFormData(true);
		       webSettings.setSavePassword(false);
		      webSettings.setJavaScriptEnabled(true);
		       webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		       webSettings.setLoadsImagesAutomatically(true);
		       webSettings.setBlockNetworkImage(false);
		       webSettings.setGeolocationEnabled(true);
		       webSettings.setLoadWithOverviewMode(true);
		       
		       //setDefaultUserAgent
		        String defaultagent = webView.getSettings().getUserAgentString();
				if (defaultagent==null)
					defaultagent = com.anxa.hapilabs.common.util.AppUtil.getDefaultUserAgent();
				webView.getSettings().setUserAgentString(ApplicationEx.getInstance().customAgent+" "+defaultagent);
			

			
		}
		
		
	    

	
}