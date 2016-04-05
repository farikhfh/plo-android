package com.plo.ploworks.network;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Farikh Fadlul Huda on 10/1/2015.
 */
public class RequestBuilder {
    public static class UrlBuilder
    {
        private String baseURL;
        private String url;
        private String urlParams;
        private final HashMap<String, Integer> idResources;

        public UrlBuilder()
        {
            baseURL = ListUrl.BASE_URL;
            urlParams = "";
            idResources = new HashMap<>();
        }

        public UrlBuilder(String baseURL)
        {
            this();
            if (baseURL != null && !baseURL.equals(""))
            {
                this.baseURL = baseURL;
            }
        }

        /**
         * set API url you want to communicate with
         *
         * @param url
         * @return
         */
        public UrlBuilder setUrl(String url)
        {
            this.url = url;
            return this;
        }

        /**
         * @param key
         * @param value
         * @return
         */
        public UrlBuilder addIdResource(String key, int value)
        {
            idResources.put(key, value);

            return this;
        }

        /**
         * to append GET query, query that appended in the last part of url.
         * call this method multiple times to append more than one params
         *
         * @param key   key of the params
         * @param value value of the params.
         * @return
         */
        public UrlBuilder appendUrlQuery(String key, String value)
        {
            if (urlParams.equals(""))
            {
                urlParams = "?";
            } else
            {
                urlParams += "&";
            }

            urlParams += key + "=" + value;

            return this;
        }

        /**
         * to append GET query, query that appended in the last part of url.
         * call this method multiple times to append more than one params
         *
         * @param query value of the params.
         * @return
         */
        public UrlBuilder setCustomUrlQuery(String query)
        {
            if (TextUtils.isEmpty(query))
            {
                return this;
            }

            if (urlParams.equals(""))
            {
                urlParams = "?";
            } else
            {
                urlParams += "&";
            }

            urlParams += query;

            return this;
        }

        public String build()
        {
            return reformatUrl();
        }

        private String reformatUrl()
        {
            String[] partUrl = url.split("/");
            String url = baseURL;
            for (String s : partUrl)
            {
                url += "/";
                if (s.startsWith("{"))
                {
                    s = s.replace("{", "");
                    s = s.replace("}", "");

                    if (idResources.containsKey(s))
                    {
                        url += idResources.get(s);
                    } else
                    {
                        throw new RuntimeException("WARNING!!! Cannot find "
                                + s + " replacement. "
                                + "Please set it via addIdResource(" + s
                                + ", {resource id})");
                    }
                } else
                {
                    url += s;
                }
            }
            url += urlParams;
            Log.d(getClass().getSimpleName(), "url = " + url);
            return url;
        }
      }
}

