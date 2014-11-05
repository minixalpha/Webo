package com.minixalpha.webo.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//response:[{"screen_name":"\u7ae5\u4e4b\u4f1f","followers_count":146189,"uid":1837869620}]
public class SearchResult {
	public String screen_name;
	public int followers_count;
	public String uid;

	public static SearchResult parse(JSONObject jsonObject) {
		SearchResult result = new SearchResult();

		result.screen_name = jsonObject.optString("screen_name");
		result.followers_count = jsonObject.optInt("followers_count");
		result.uid = String.valueOf(jsonObject.optLong("uid"));
		return result;
	}

	public static List<SearchResult> parseTotal(String jsonString) {
		List<SearchResult> resultList = new ArrayList<>();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					resultList.add(parse(jsonArray.getJSONObject((i))));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultList;
	}
}
