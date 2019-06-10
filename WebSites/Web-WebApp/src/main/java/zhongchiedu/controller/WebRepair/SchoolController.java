package zhongchiedu.controller.WebRepair;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.pojo.School;
import zhongchiedu.WxRepair.service.Impl.RepairClassServiceImpl;
import zhongchiedu.WxRepair.service.Impl.SchoolImpl;
import zhongchiedu.common.utils.FileOperateUtil;
import zhongchiedu.common.utils.ZipCompress;
import zhongchiedu.framework.pagination.Pagination;
import zhongchiedu.log.annotation.SystemControllerLog;
@Slf4j
@Controller
public class SchoolController {

	
	@Value("${wxappid}")
	private String appid;
	
	@Autowired
	private SchoolImpl schoolImpl;
	
	@Autowired
	private RepairClassServiceImpl classImpl;
	
	@Value("${upload-Qrcode}")
	private String qrpath;
	@Value("${upload-dir}")
	private String dir;
	
	@GetMapping("/schools")
//	@RequiresPermissions(value = "school:list")
	@SystemControllerLog(description = "查询所有学校")
	public String list(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, Model model,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
		// 分页查询数据
		Pagination<School> pagination;
		try {
			pagination = schoolImpl.findPaginationByQuery(new Query(), pageNo, pageSize, School.class);
			if (pagination == null)
				pagination = new Pagination<School>();

			model.addAttribute("pageList", pagination);
		} catch (Exception e) {
			log.info("查询所有维修分类信息失败——————————》" + e.toString());
			e.printStackTrace();
		}

		return "repair/school/list";
	}
	
	/**
	 * 跳转到添加页面
	 */
	@GetMapping("/school")
	@RequiresPermissions(value = "school:add")
	public String addUserPage(Model model) {
		return "repair/school/add";
	}
	
	@PostMapping("/school")
	@RequiresPermissions(value="school:add")
	@SystemControllerLog(description = "添加维修类别")
	public String addOne(@ModelAttribute("school")School school) {
		log.info(school.toString());
		this.schoolImpl.addOrUpdate(school);
		return "redirect:schools";
		
	}
	@PutMapping("/school")
	@RequiresPermissions(value="school:edit")
	@SystemControllerLog(description = "修改维修类别")
	public String editOne(@ModelAttribute("school")School school) {
		this.schoolImpl.addOrUpdate(school);
		return "redirect:schools";
	}
	
	@GetMapping("/school{id}")
	public String toEdit(@PathVariable("id")String id,Model model) {
		School rc=this.schoolImpl.findOneById(id, School.class);
		model.addAttribute("school", rc);
		return "repair/school/add";
	}
	
	
	@DeleteMapping("/school/{id}")
	@RequiresPermissions(value = "school:delete")
	@SystemControllerLog(description = "删除维修分类")
	public String delete(@PathVariable String id){
		String[] strids = id.split(",");
		for (String delids : strids) {
			log.info("删除维修分类---》" + delids);
			School rc = this.schoolImpl.findOneById(delids,School.class);
			this.schoolImpl.remove(rc);// 删除某个id
		}
		return "redirect:/schools";
	}
	
	
	@RequestMapping("/wx/upload")
	public String upload(HttpServletRequest request,HttpSession session) throws Exception {
		String msg = "";
		String upname = "WEB-INF" + File.separator + "FileUpload" + File.separator + "schoollist";
		String[] filetype = { "xls,xlsx" };
			List<Map<String, Object>> result = FileOperateUtil.upload(request, upname, filetype);
			Set<String>  ss=result.get(0).keySet();
			if(!ss.contains("hassuffix")){
				msg="文件上传异常!";
			}else{
			boolean has = ((Boolean) ((Map<String, Object>) result.get(0)).get("hassuffix")).booleanValue();
			if (has) {
				String path = (String) ((Map<String, Object>) result.get(0)).get("savepath");
				File file = new File(path);
				this.schoolImpl.batchImport(file, 1);
			 }
			}
			return "redirect:/schools";
	}    
	
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private HttpServletRequest request;
	
    @Autowired
    private ServletContext servletContext;
    
	@RequestMapping("/wx/Qr/{ids}")
	@ResponseBody
	public void QrCode(@PathVariable String[] ids) throws IOException {
        URL requestURL = new URL(request.getRequestURL().toString());
		String redirectURL=String.format("%s://%s/web/wx/redirect/%s/jump?", requestURL.getProtocol(), requestURL.getHost(),appid);
		Query query=new Query();
		query.addCriteria(Criteria.where("id").in(ids));
		List<School> schools=this.schoolImpl.find(query, School.class);
		Query query2=new Query();
		query2.addCriteria(Criteria.where("isparent").is(true));
		List<RepairClass> rpcs=this.classImpl.find(query2, RepairClass.class);
		List<File> lfs=null;
		if(schools.size()>0) {
		lfs=schools.stream().map(school->{
				List<File> files=rpcs.stream().map(rpc->{
					File f=this.schoolImpl.QrcodeOne(redirectURL, school, rpc);
					return f;
				}).collect(Collectors.toList());
				File file=this.schoolImpl.GetQrZip(files, school.getName());
				return file;
			}).collect(Collectors.toList());
		}
		String filepath=dir+qrpath;
		String filename=filepath+ZipCompress.getZipFilename();
		File srcfile=new File(filename);
		File zipfile=ZipCompress.zipFile2(lfs,srcfile);
		response.setHeader("content-disposition", "attachment;filename="+URLEncoder.encode("二维码.zip","UTF-8"));
		String mimeType=servletContext.getMimeType("二维码.zip");
		response.setContentType(mimeType);
		ServletOutputStream out=response.getOutputStream();
		FileInputStream in=new FileInputStream(zipfile);
		byte[] buffer=new byte[1024];
		int len=0;
		while((len=in.read(buffer))!=-1) {
			out.write(buffer, 0, len);
		}
		in.close();
	}
	
	
	
	
//	@RequestMapping("/wx/Qr/{ids}")
//	@ResponseBody
//	public void QrCode(@PathVariable String[] ids) throws IOException {
//        URL requestURL = new URL(request.getRequestURL().toString());
//		String redirectURL=String.format("%s://%s/web/wx/redirect/%s/repairIndex/1?", requestURL.getProtocol(), requestURL.getHost(),appid);
//		Query query=new Query();
//		query.addCriteria(Criteria.where("id").in(ids));
//		List<School> schools=this.schoolImpl.find(query, School.class);
//		List<RepairClass> rpcs=this.classImpl.find(new Query(), RepairClass.class);
//		List<File> lfs=null;
//		if(schools.size()>0) {
//		lfs=schools.stream().map(school->{
//				List<File> files=rpcs.stream().map(rpc->{
//					File f=this.schoolImpl.QrcodeOne(redirectURL, school, rpc);
//					return f;
//				}).collect(Collectors.toList());
//				File file=this.schoolImpl.GetQrZip(files, school.getName());
//				return file;
//			}).collect(Collectors.toList());
//		}
//		String filepath=dir+qrpath;
//		String filename=filepath+ZipCompress.getZipFilename();
//		File srcfile=new File(filename);
//		File zipfile=ZipCompress.zipFile2(lfs,srcfile);
//		response.setHeader("content-disposition", "attachment;filename="+URLEncoder.encode("二维码.zip","UTF-8"));
//		String mimeType=servletContext.getMimeType("二维码.zip");
//		response.setContentType(mimeType);
//		ServletOutputStream out=response.getOutputStream();
//		FileInputStream in=new FileInputStream(zipfile);
//		byte[] buffer=new byte[1024];
//		int len=0;
//		while((len=in.read(buffer))!=-1) {
//			out.write(buffer, 0, len);
//		}
//		in.close();
//	}
		
}
