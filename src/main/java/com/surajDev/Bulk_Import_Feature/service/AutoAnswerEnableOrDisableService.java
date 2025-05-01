package com.surajDev.Bulk_Import_Feature.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.Configuration;
import com.mypurecloud.sdk.v2.api.SettingsApi;
import com.mypurecloud.sdk.v2.api.UsersApi;
import com.mypurecloud.sdk.v2.model.UpdateUser;
import com.mypurecloud.sdk.v2.model.User;
import com.mypurecloud.sdk.v2.model.UserEntityListing;

@Service
public class AutoAnswerEnableOrDisableService {
	
	public List<Map<String,Object>> getAgentWithAutoAnswerStatus(String organizationName, String environment, String token){
		List<Map<String,Object>> agents = new ArrayList<>();
		
		try {
			ApiClient apiClient = ApiClient.Builder.standard().withAccessToken(token).withBasePath("https://api." + environment)
                    .build();
			Configuration.setDefaultApiClient(apiClient);
			
			UsersApi usersApi = new UsersApi(apiClient);
            int pageSize = 100;
            int pageNumber = 1;
            
            while(true) {
            	UserEntityListing userListing = usersApi.getUsers(pageSize, pageNumber, null, null, null, 
                        Arrays.asList("routingStatus"), null, null);
            	if (userListing.getEntities() == null || userListing.getEntities().isEmpty()) {
                    break;
                }
            	
            	for(User user: userListing.getEntities()) {
            		Map<String, Object> agentInfo = new HashMap<>();
            		agentInfo.put("ID", user.getId());
            		agentInfo.put("name", user.getName());
            		agentInfo.put("email", user.getEmail());
            		
            		//checking auto answer status
            		boolean autoAnswerEnabled = user.getAcdAutoAnswer();
            		
            		agentInfo.put("autoAnswerEnabled", autoAnswerEnabled);
            		
            		agents.add(agentInfo);
            	}
            	pageNumber++;
            }
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return agents;
	}
	
    public void updateAutoAnswerStatus(String userId, boolean enable, String environment, String token) {
        try {
            ApiClient apiClient = ApiClient.Builder.standard()
                    .withAccessToken(token)
                    .withBasePath("https://api." + environment)
                    .build();
            Configuration.setDefaultApiClient(apiClient);

            SettingsApi usersApi = new SettingsApi(apiClient);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
