package com.plo.ploworks.network;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
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

        /**
         * This builder for building POST data that will be sent to server
         */
        public static class PostBuilder<T>
        {

            private String json;
            private final Gson gson;

            public PostBuilder()
            {
                gson = new GsonBuilder()
                        .setFieldNamingPolicy(
                                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .registerTypeAdapter(Date.class, new DateTypeAdapter())
                        .create();
            }

            /**
             * to set POST params. Don't call this on non POST method. Check out for
             * Log if something is wrong. could be caused by Converting error.
             *
             * @param entity Object entity to be sent as POST params
             * @return
             */
            public PostBuilder<T> setParams(T entity)
            {
                json = gson.toJson(entity);
                Log.d(PostBuilder.class.getSimpleName(), "params = " + json);
                return this;
            }

            public JSONObject build()
            {
                try
                {
                    return new JSONObject(json);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e(PostBuilder.class.getSimpleName(),
                            "ERROR CONVERTING TO JSON!!!! Reason: "
                                    + e.getMessage());
                    return null;
                }
            }
        }
      }
}

