package onem.quzhigang.filessearch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filesSearch")
public class FilesSearchController {
	
	
	@RequestMapping(path="/index")
	public String index(){
		
		return "index";
	}

}
