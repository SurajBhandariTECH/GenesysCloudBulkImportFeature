package com.surajDev.Bulk_Import_Feature.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.Configuration;
import com.surajDev.Bulk_Import_Feature.service.GenesysServices;
import com.surajDev.Bulk_Import_Feature.service.LanguageSkillServices;
import com.surajDev.Bulk_Import_Feature.service.OrgConfigService;
import com.surajDev.Bulk_Import_Feature.service.UserService;
import com.surajDev.Bulk_Import_Feature.service.WrapUpCodeServices;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.Configuration;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/genesysContactCenter")
public class SkillController {
	
	@Autowired
	private OrgConfigService orgConfigService;
	
	@Autowired
	private GenesysServices genesysService;
	
	@Autowired
	private LanguageSkillServices languageSkillServices;
	
	@Autowired
	private WrapUpCodeServices wrapUpCodeService;
	
	@Autowired UserService userService;
	
	public SkillController(GenesysServices genesysService) {
		this.genesysService = genesysService;
	}
	
	
	// Show login form
	@GetMapping("/login")
	public String showLoginForm(Model model) {
//		model.addAttribute("environment",List.of("mypurecloud.com","usw2.pure.cloud","eu.pure.cloud"));
		model.addAttribute("organizationName",orgConfigService.getAvailableOrganizations());
		return "login";
	}
	
	// Handle login
	
	@PostMapping("/login")
	public String connectTogenesys(
			@RequestParam("organizationName")String organizationName,
			@RequestParam("environment")String environment,
			RedirectAttributes redirectAttributes,
			HttpSession session,
			Model model) {
		
		try {
			
			Map<String, String> credentials = orgConfigService.getCredentials(organizationName);
			String clientId = credentials.get("clientId");
			String clientSecret = credentials.get("clientSecret");
			
			if(clientId==null || clientSecret==null || clientId.isEmpty() ||clientSecret.isEmpty()) {
				throw new IllegalArgumentException("Error: Client Credentials not found for organization "+ organizationName);
				
			}
			
			
				//initialize the Genesys API Client
			ApiClient apiClient = ApiClient.Builder.standard().withBasePath("https://api."+environment).build();
			apiClient.authorizeClientCredentials(clientId, clientSecret);
			Configuration.setDefaultApiClient(apiClient);
//			System.out.println("clientId"+clientId+" client Secret"+clientSecret);
			
			//Store credentials in the session
			session.setAttribute("organizationName", organizationName);
			session.setAttribute("environment", environment);
			session.setAttribute("credentials", credentials);
			
		//add confirmation message and redirect to the import page
			 redirectAttributes.addFlashAttribute("confirmationMessage", "Connected to Genesys org: " + organizationName);
			 
		//save organization and environment in session or attributes for next page
		redirectAttributes.addFlashAttribute("organizationName",organizationName);
		redirectAttributes.addFlashAttribute("environment",environment);
		return "redirect:/genesysContactCenter/importOptions";
		
	}catch(IllegalArgumentException e) {
		model.addAttribute("errorMessage",e.getMessage());
		//re-add the available organizations and environment option after error
		 model.addAttribute("organizationName", orgConfigService.getAvailableOrganizations());
		return "login";
	}
		catch(Exception e) {
			model.addAttribute("errorMessage", "Unable to connect to Genesys: " + e.getMessage());
			 // Re-add the available organizations and environment options after error
			model.addAttribute("organizationName", orgConfigService.getAvailableOrganizations());
			model.addAttribute("errorMessage","Unable to connect to genesys:"+e.getMessage()+" Organization or Selected Environment is incorrect "+environment);
			return "login";
	}
	
		
}
	
	 // Show choose import options page
    @GetMapping("/importOptions")
    public String showChooseImportPage(HttpSession session, Model model) {
    	String organizationName = (String) session.getAttribute("organizationName");
	    String environment = (String) session.getAttribute("environment");

	    if (organizationName == null || environment == null) {
	        model.addAttribute("errorMessage", "Session expired. Please log in again.");
	        return "login";
	    }

	    String confirmationMessage = "Connected to Genesys org: " + organizationName;
	    model.addAttribute("confirmationMessage", confirmationMessage);
	    model.addAttribute("organizationName", organizationName);
	    model.addAttribute("environment", environment);

	    return "importOptions";
    }
	//show upload page for bulk skill
	@GetMapping("/upload")
	public String showUploadPage(HttpSession session,Model model) {
		
		 String organizationName = (String) session.getAttribute("organizationName");
		    String environment = (String) session.getAttribute("environment");

		    if (organizationName == null || environment == null) {
		        model.addAttribute("errorMessage", "Session expired. Please log in again.");
		        return "login";
		    }

		    String confirmationMessage = "Connected to Genesys org: " + organizationName;
		    model.addAttribute("confirmationMessage", confirmationMessage);
		    model.addAttribute("organizationName", organizationName);
		    model.addAttribute("environment", environment);

		    return "upload";
	}
	
	@GetMapping("/deleteSkill")
    public String showUploadSkillPage(HttpSession session, Model model) {
		String organizationName = (String) session.getAttribute("organizationName");
	    String environment = (String) session.getAttribute("environment");

	    if (organizationName == null || environment == null) {
	        model.addAttribute("errorMessage", "Session expired. Please log in again.");
	        return "login";
	    }

	    String confirmationMessage = "Connected to Genesys org: " + organizationName;
	    model.addAttribute("confirmationMessage", confirmationMessage);
	    model.addAttribute("organizationName", organizationName);
	    model.addAttribute("environment", environment);

	    return "upload";
    }
	
	 // Show upload page for Bulk Language Skill
    @GetMapping("/uploadLanguageSkill")
    public String showUploadLanguageSkillPage(HttpSession session, Model model) {
    	String organizationName = (String) session.getAttribute("organizationName");
	    String environment = (String) session.getAttribute("environment");

	    if (organizationName == null || environment == null) {
	        model.addAttribute("errorMessage", "Session expired. Please log in again.");
	        return "login";
	    }

	    String confirmationMessage = "Connected to Genesys org: " + organizationName;
	    model.addAttribute("confirmationMessage", confirmationMessage);
	    model.addAttribute("organizationName", organizationName);
	    model.addAttribute("environment", environment);

        return "uploadLanguageSkill"; // Create this page for Language Skill upload
    }
    
    //show the upload language skill page for deleting languageSkill
    @GetMapping("/deleteLanguageSkill")
    public String showDeleteLangaugeSkillPage(HttpSession session, Model model) {
		String organizationName = (String) session.getAttribute("organizationName");
	    String environment = (String) session.getAttribute("environment");

	    if (organizationName == null || environment == null) {
	        model.addAttribute("errorMessage", "Session expired. Please log in again.");
	        return "login";
	    }

	    String confirmationMessage = "Connected to Genesys org: " + organizationName;
	    model.addAttribute("confirmationMessage", confirmationMessage);
	    model.addAttribute("organizationName", organizationName);
	    model.addAttribute("environment", environment);

	    return "uploadLanguageSkill";
    }

	@GetMapping("/uploadWrapUpCode")
	
	public String uploadWrapUpCodePage(HttpSession session, Model model) {
		String organizationName = (String)session.getAttribute("organizationName");
		String environment = (String)session.getAttribute("environment");
		
		if(organizationName == null || environment == null) {
			model.addAttribute("errorMessage","Session expired. Please log in again");
			return "login";
		}
		
		String confirmationMessage = "Connected to genesys org: "+ organizationName;
		model.addAttribute("organizationName", organizationName);
		model.addAttribute("environment",environment);
		model.addAttribute("confirmationMessage",confirmationMessage);
		
		return "uploadWrapUpCode";
	}
    
	@GetMapping("/deleteWrapUpCode")
	
	public String deleteWrapUpCodePage(HttpSession session, Model model) {
		String organizationName = (String)session.getAttribute("organizationName");
		String environment = (String)session.getAttribute("environment");
		
		if(organizationName == null || environment == null) {
			model.addAttribute("errorMessage","Session expired. Please log in again");
			return "login";
		}
		
		String confirmationMessage = "Connected to genesys org: "+ organizationName;
		model.addAttribute("organizationName", organizationName);
		model.addAttribute("environment",environment);
		model.addAttribute("confirmationMessage",confirmationMessage);
		
		return "uploadWrapUpCode";
	}
	
	@GetMapping("/uploadUser")
	public String EmployeerInfoUpdation(HttpSession httpSession, Model model) {
		String organizationName = (String)httpSession.getAttribute("organizationName");
		String environment = (String)httpSession.getAttribute("environment");
		
		if(organizationName == null || environment == null) {
			model.addAttribute("errorMessage","Session expired. Please log in again");
			return "login";
			
		}
		String confirmationMessage = "Connected to genesys org: "+organizationName;
		model.addAttribute("organizationName",organizationName);
		model.addAttribute("environment",environment);
		model.addAttribute("confirmationMessage",confirmationMessage);
		return "uploadUser";
		
	}
    
    @PostMapping("/upload")
    public String handleSkillFileUpload(@RequestParam("file") MultipartFile file,
                                        HttpSession session, Model model) {
//    	 model.addAttribute("confirmationMessage", confirmationMessage);
        return handleFileUpload(file, session, model);
        
    }
    
    //handle the deletion of skill
    @PostMapping("/deleteSkill")
    public String handleDeleteSkillFileUpload(@RequestParam("file")MultipartFile file,
    		HttpSession session,
    		Model model,
    		RedirectAttributes redirectAttributes) {
    	return handleDeleteSkillFile(file,session,model,redirectAttributes);
    	
    }

    // Handle File Upload for Bulk Language Skills
    @PostMapping("/uploadLanguageSkill")
    public String handleLanguageSkillFileUpload(@RequestParam("file") MultipartFile file,
                                                HttpSession httpSession,
                                                RedirectAttributes redirectAttributes,
                                                Model model) {
    	
        return handleLanguageSkillFile(file, httpSession, model,redirectAttributes);
    }
    
    //handle deletion of language skills
    @PostMapping("/deleteLanguageSkill")
    public String handleDeleteLanguageSkill(@RequestParam("file")MultipartFile file,
    		HttpSession session,
    		Model model,
    		RedirectAttributes redirectAttributes) {
    	return handleDeleteLanguageSkillFile(file,session,model,redirectAttributes);
    	
    }
	
 // Handle File Upload for Bulk Wrap-Up Code (with different validation and functionality)
    @PostMapping("/uploadWrapUpCode")
	public String handleWrapUpCodeFileUpload(@RequestParam("file")MultipartFile file,
			HttpSession httpSession,
			RedirectAttributes redirectAttributes,
			Model model) {
		return handleWrapUpCodeFile(file,httpSession,redirectAttributes,model);
	}
    
    @PostMapping("/deleteWrapUpCode")
    public String handleDeletewrapUpCode(@RequestParam("file")MultipartFile file,
    		HttpSession session,
    		Model model,
    		RedirectAttributes redirectAttributes) {
    	return handleDeleteWrapUpCodeFile(file,session,model,redirectAttributes);
    	
    }
    
    @PostMapping("/uploadUser")
    public String handleUserUpdate(@RequestParam("file")MultipartFile file,
    		HttpSession session, 
    		RedirectAttributes redirectAttributes,
    		Model model) {
    	return handleUserEmployerInfoFile(file, session, model, redirectAttributes);
    }
	// Handle File Upload
	
	
	public String handleFileUpload(MultipartFile file,
			HttpSession httpSession,
			Model model){
		
		

		try {
			
				String organizationName= (String) httpSession.getAttribute("organizationName");
				String environment = (String)httpSession.getAttribute("environment");
			
				if(organizationName==null || environment==null) {
					model.addAttribute("errorMessage","Session expired. Please login in again.");
					return "login";
					}
			
				@SuppressWarnings("unchecked")
				Map<String, String> credentials = (Map<String,String>) httpSession.getAttribute("credentials");
				if(credentials ==null || !orgConfigService.validateCredentials(credentials,environment)) {
						throw new IllegalArgumentException("Client credentials have changed or are invalid. Please log in again:");
					}
				
		//process the CSV file and create Skill
				List<String> results = genesysService.createSkillFromCsv(file, organizationName,environment);
		//add the confirmation message and results to the redirect attributes so it can be displayed in the success message
				model.addAttribute("confirmationMessage","Connect to Genesys org:"+organizationName);
				model.addAttribute("results",results);
				model.addAttribute("successMessage","Skill Upload successfully");
		
//				redirectAttributes.addFlashAttribute("results",results);
				
			}catch(IllegalArgumentException e) {
				model.addAttribute("errorMessage"+ e.getLocalizedMessage());
				return "login";
			}catch(IOException e) {
				model.addAttribute("errorMessage","File process error: "+e.getMessage());
				return "upload";
			}catch(Exception e) {
				model.addAttribute("errorMessage","Error processing the files:"+e.getMessage());
				return "upload";
			}
		return "upload";
		}
	
	private String handleLanguageSkillFile(MultipartFile file, HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
			
		
		try {
			String organizationName = (String)httpSession.getAttribute("organizationName");
			String environment = (String)httpSession.getAttribute("environment");
			
			if(organizationName==null || environment==null) {
				model.addAttribute("errorMessage","Session expired. Please login again.");
				return "login";
			}
			
			@SuppressWarnings("unchecked")
			Map<String, String> credentials = (Map<String,String>)httpSession.getAttribute("credentials");
			if(credentials==null || !orgConfigService.validateCredentials(credentials, environment)) {
				throw new IllegalArgumentException("Client credentials have changed or are invalid. Please login again.");
			}
			
			//process the csv file and create language skills
		 List<String> results =  languageSkillServices.createLanguageSkillFromCsv(file, organizationName, environment);
			
			//add result to model for display
		//add the confirmation message and results to the redirect attributes so it can be displayed in the success message
			model.addAttribute("confirmationMessage","Connect to Genesys org:"+organizationName);
			model.addAttribute("results",results);
			model.addAttribute("successMessage", "Language skills uploaded successfully.");
			
		    
		}catch (IllegalArgumentException e) {
	        model.addAttribute("errorMessage", e.getMessage());
	        return "login";
	    } catch (IOException e) {
	        model.addAttribute("errorMessage", e.getMessage());
	        return "uploadLanguageSkill";
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "Error processing the file: " + e.getMessage());
	        return "uploadLanguageSkill";
	    }
		
		return "uploadLanguageSkill";
	}
		
	
	private String handleDeleteSkillFile(MultipartFile file, HttpSession session,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			
			 String organizationName = (String) session.getAttribute("organizationName");
	            String environment = (String) session.getAttribute("environment");

	            if (organizationName == null || environment == null) {
	                model.addAttribute("errorMessage", "Session expired. Please log in again.");
	                return "login";
	            }
	            
	            @SuppressWarnings("unchecked")
	            Map<String, String> credentials = (Map<String, String>) session.getAttribute("credentials");
	            if (credentials == null || !orgConfigService.validateCredentials(credentials, environment)) {
	                throw new IllegalArgumentException("Client credentials have changed or are invalid. Please log in again.");
	            }
	            
	            //process the CSV file and delete skills
	            List<String> results = genesysService.deleteSkillsFromCSV(file, organizationName, environment);
	            
	            model.addAttribute("confirmationMessage", "Connected to Genesys org: " + organizationName);
	            model.addAttribute("results", results);
	            model.addAttribute("successMessage", "file upload successfully.");
		}
		
		catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "File processing error: " + e.getMessage());
            return "upload";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error processing the file: " + e.getMessage());
            return "upload";
        }

        return "upload";
    }
	
	private String handleDeleteLanguageSkillFile(MultipartFile file, HttpSession session,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			
			 String organizationName = (String) session.getAttribute("organizationName");
	            String environment = (String) session.getAttribute("environment");

	            if (organizationName == null || environment == null) {
	                model.addAttribute("errorMessage", "Session expired. Please log in again.");
	                return "login";
	            }
	            
	            @SuppressWarnings("unchecked")
	            Map<String, String> credentials = (Map<String, String>) session.getAttribute("credentials");
	            if (credentials == null || !orgConfigService.validateCredentials(credentials, environment)) {
	                throw new IllegalArgumentException("Client credentials have changed or are invalid. Please log in again.");
	            }
	            
	            //process the CSV file and delete skills
	            List<String> results = languageSkillServices.deleteLanguageSkillsFromCSV(file, organizationName, environment);
	            
	            model.addAttribute("confirmationMessage", "Connected to Genesys org: " + organizationName);
	            model.addAttribute("results", results);
	            model.addAttribute("successMessage", "file upload successfully.");
		}
		
		catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "File processing error: " + e.getMessage());
            return "uploadLanguageSkill";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error processing the file: " + e.getMessage());
            return "uploadLanguageSkill";
        }

        return "uploadLanguageSkill";
    }
	
	//handle file upload
		public String handleWrapUpCodeFile(MultipartFile file, HttpSession httpSession, RedirectAttributes redirectAttributes,Model model) {
			try {
				
				String organizationName = (String)httpSession.getAttribute("organizationName");
				String environment = (String)httpSession.getAttribute("environment");
				
				if(organizationName==null || environment==null) {
					model.addAttribute("errorMessage","Session expired. Please login in again.");
					return "login";
					}
			
				@SuppressWarnings("unchecked")
				Map<String, String> credentials = (Map<String,String>) httpSession.getAttribute("credentials");
				if(credentials ==null || !orgConfigService.validateCredentials(credentials,environment)) {
						throw new IllegalArgumentException("Client credentials have changed or are invalid. Please log in again:");
					}
				
		//process the CSV file and create Skill
				List<String> results = wrapUpCodeService.createWrapUpCodeFromCSV(file, organizationName,environment);
		//add the confirmation message and results to the redirect attributes so it can be displayed in the success message
				model.addAttribute("confirmationMessage","Connect to Genesys org:"+organizationName);
				model.addAttribute("results",results);
				model.addAttribute("successMessage","WrapUpCode File Upload successfully");
		
//				redirectAttributes.addFlashAttribute("results",results);
				
			}catch(IllegalArgumentException e) {
				model.addAttribute("errorMessage"+ e.getLocalizedMessage());
				return "login";
			}catch(IOException e) {
				model.addAttribute("errorMessage","File process error: "+e.getMessage());
				return "uploadWrapUpCode";
			}catch(Exception e) {
				model.addAttribute("errorMessage","Error processing the files:"+e.getMessage());
				return "uploadWrapUpCode";
			}
		return "uploadWrapUpCode";
		}
	
		private String handleDeleteWrapUpCodeFile(MultipartFile file, HttpSession session,
				Model model, RedirectAttributes redirectAttributes) {
			try {
				
				 String organizationName = (String) session.getAttribute("organizationName");
		            String environment = (String) session.getAttribute("environment");

		            if (organizationName == null || environment == null) {
		                model.addAttribute("errorMessage", "Session expired. Please log in again.");
		                return "login";
		            }
		            
		            @SuppressWarnings("unchecked")
		            Map<String, String> credentials = (Map<String, String>) session.getAttribute("credentials");
		            if (credentials == null || !orgConfigService.validateCredentials(credentials, environment)) {
		                throw new IllegalArgumentException("Client credentials have changed or are invalid. Please log in again.");
		            }
		            
		            //process the CSV file and delete skills
		            List<String> results = wrapUpCodeService.deleteWrapUpCodeFromCSV(file, organizationName, environment);
		            
		            model.addAttribute("confirmationMessage", "Connected to Genesys org: " + organizationName);
		            model.addAttribute("results", results);
		            model.addAttribute("successMessage", "file upload successfully.");
			}
			
			catch (IllegalArgumentException e) {
	            model.addAttribute("errorMessage", e.getMessage());
	            return "login";
	        } catch (IOException e) {
	            model.addAttribute("errorMessage", "File processing error: " + e.getMessage());
	            return "uploadWrapUpCode";
	        } catch (Exception e) {
	            model.addAttribute("errorMessage", "Error processing the file: " + e.getMessage());
	            return "uploadWrapUpCode";
	        }

	        return "uploadWrapUpCode";
	    }
		
		public String handleUserEmployerInfoFile(MultipartFile file, HttpSession session,
				Model model, RedirectAttributes redirectAttributes) {
			try {
				 String organizationName = (String) session.getAttribute("organizationName");
		            String environment = (String) session.getAttribute("environment");

		            if (organizationName == null || environment == null) {
		                model.addAttribute("errorMessage", "Session expired. Please log in again.");
		                return "login";
		            }
		            @SuppressWarnings("unchecked")
					Map<String, String> credentials = (Map<String,String>)session.getAttribute("credentials");
		            
		            if (credentials == null || !orgConfigService.validateCredentials(credentials, environment)) {
		                throw new IllegalArgumentException("Client credentials have changed or are invalid. Please log in again.");
		            }
		            
		            List<String> results = userService.updateUsersFromCsv(file, organizationName, environment);
		            	model.addAttribute("confirmationMessage", "Connected to Genesys org: " + organizationName);
			            model.addAttribute("results", results);
			            model.addAttribute("successMessage", "file upload successfully.");
		            
			}catch (IllegalArgumentException e) {
	            model.addAttribute("errorMessage", e.getMessage());
	            return "login";
	        } catch (IOException e) {
	            model.addAttribute("errorMessage", "File processing error: " + e.getMessage());
	            return "uploadUser";
	        } catch (Exception e) {
	            model.addAttribute("errorMessage", "Error processing the file: " + e.getMessage());
	            return "uploadUser";
	        }
			
			return "uploadUser";
		}
	
}
