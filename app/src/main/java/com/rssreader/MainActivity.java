package com.rssreader;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;
import com.rssreader.adapter.EndlessOnScrollListener;
import com.rssreader.adapter.ItemClickListener;
import com.rssreader.adapter.RssAdapter;
import com.rssreader.network.Network;
import com.rssreader.rssinfo.RssDetail;
import com.rssreader.rssinfo.RssObject;
import com.rssreader.rssload.RssLoad;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    private ArrayList<RssObject> rssObjects;
    Context context;
    RecyclerView.Adapter adapter;
    NodeList nodeList;
    LinearLayoutManager layoutManager;
    TextView urlTextView;
    String url;
    int loadPerPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = "http://www.feedforall.com/blog-feed.xml";
        loadPerPage = 10;
        context = this;

        initViews();
        loadRssData();
    }

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        urlTextView = (TextView)  findViewById(R.id.urlTextView);
        urlTextView.setText(url);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        rssObjects = new ArrayList<>();
        adapter = new RssAdapter(rssObjects, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener (new EndlessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currPage) {
                loadMoreData(currPage);
            }
        });

    }

    public void loadRssData() {
        if (Network.isNetworkConnected(context)) {
            if (URLUtil.isValidUrl(url)) {
                final ProgressDialog progress = new ProgressDialog(context);
                final RssLoad rssLoad = new RssLoad(context);
                String progressText = getResources().getString(R.string.progress_text);
                progress.setMessage(progressText);
                progress.show();
                rssLoad.sendRequest(url);
                rssLoad.setEventListener(new RssLoad.OnResponseListener() {
                    @Override
                    public void onResponse(String status, String response) {
                        progress.dismiss();
                        boolean result = true;
                        if (status.equals("Success")) {
                            if (!parseXmlResponse(response))
                                result = false;
                        } else {
                            result = false;
                        }
                        if (!result) {
                            Toast toast = Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });
            } else {
                Toast toast = Toast.makeText(context, R.string.invalid_url, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    boolean parseXmlResponse(String xml) {
        boolean result = true;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                InputSource inputSource = new InputSource();
                inputSource.setCharacterStream(new StringReader(xml));
                Document doc = documentBuilder.parse(inputSource);
                doc.getDocumentElement().normalize();
                nodeList = doc.getElementsByTagName("item");
                loadMoreData(0);
            } catch (SAXException e) {
                result = false;
                e.printStackTrace();
            } catch (IOException e) {
                result = false;
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    public void loadMoreData(int position)
    {
        int start = loadPerPage * position;
        int end = loadPerPage * position + loadPerPage;

        if (start < nodeList.getLength()) {
            if (end > nodeList.getLength())
                end = nodeList.getLength();
            RssObject rssObject = new RssObject();
            rssObject.description = "progress";
            rssObjects.add(rssObject);
            adapter.notifyItemInserted(rssObjects.size());
            ArrayList<RssObject> rssObjectsCurr = new ArrayList();
            for (int i = start; i < end; i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String link = eElement.getElementsByTagName("link").item(0).getTextContent();
                    String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                    String pubDate = eElement.getElementsByTagName("pubDate").item(0).getTextContent();

                    RssObject rssNodeObject = new RssObject();
                    rssNodeObject.description = description;
                    rssNodeObject.pubDate = pubDate;
                    rssNodeObject.link = link;
                    rssObjectsCurr.add(rssNodeObject);
                }
            }
            rssObjects.remove(rssObjects.size() - 1);
            rssObjects.addAll(rssObjectsCurr);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onItemClick(int pos, TextView descTextView) {
        Pair[] pair = new Pair[1];
        pair[0] = new Pair<View, String>(descTextView, "descShare");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);

        RssObject rssObject = rssObjects.get(pos);
        Intent intent = new Intent(this, RssDetail.class);
        intent.putExtra("rssObject", rssObject);
        startActivity(intent, options.toBundle());
    }
}
