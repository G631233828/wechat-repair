package zhongchiedu.WxRepair.service.Impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.baidu.Qrcode.Util.QRCodeUtil;

import me.chanjar.weixin.common.api.WxConsts;
import zhongchiedu.WxRepair.pojo.RepairClass;
import zhongchiedu.WxRepair.pojo.School;
import zhongchiedu.common.utils.BasicDataResult;
import zhongchiedu.common.utils.Common;
import zhongchiedu.common.utils.ExcelReadUtil;
import zhongchiedu.common.utils.ZipCompress;
import zhongchiedu.framework.service.GeneralServiceImpl;
import zhongchiedu.wx.mp.config.WxMpConfiguration;


@Repository
public class SchoolImpl extends GeneralServiceImpl<School>{

	
	@Value("${upload-Qrcode}")
	private String qrpath;
	@Value("${upload-dir}")
	private String dir;
	@Value("${wxappid}")
	private String appid;
	
	
	
	
	public void addOne(School school) {
		this.save(school);	
	}
	
	
	public void addOrUpdate(School school) {
		if(school != null) {
			if(Common.isNotEmpty(school.getId())) {
			School rc=this.findOneById(school.getId(), School.class);
			if(rc==null) {	rc=new School();}
			BeanUtils.copyProperties(school, rc);
			this.save(rc);
			}else {
				School r=new School();
				BeanUtils.copyProperties(school, r);
				this.insert(r);
			}
		}
	}
	
	
	public void deleteOne(String id) {
		this.findAndRemove(new Query()
				.addCriteria(Criteria.where("id").is(id)), School.class);
	}
	
	public void editOne(School school) {
		this.insert(school);
	}
	
	
	
	public School findOneByName(String name) {
		Query query=new Query();
		query.addCriteria(Criteria.where("name").is(name));
		School school=this.findOneByQuery(query, School.class);
		return school!=null?school:null;
	}
	
	
	public List<School> findAll(){
		List<School> schooles=this.find(new Query(), School.class);
		return schooles!=null?schooles:null;	
	}
	
	
	
	//查询重复名字
	public BasicDataResult ajaxgetRepletes(String Name) {
		if(Common.isNotEmpty(Name)){
			School school = this.findOneByName(Name);
			if(school!=null){
				return BasicDataResult.build(206,"学校名字重复", null);
			}
			return BasicDataResult.ok();
		}
		return BasicDataResult.build(400,"未能获取到请求的信息", null);
	}
	
	
    public void batchImport(File file, int row)
            throws IOException {
        int j = 0;
//        String msg = "";
        String[][] resultexcel = ExcelReadUtil.readExcel(file, row);
        int rowLength = resultexcel.length;
        for (int i = 0; i < rowLength; ++i) {
        	School school=new School();
        	school.setName(resultexcel[i][j]);
        	school.setAddr(resultexcel[i][j+1]);
        	String area=resultexcel[i][j+2].equals("奉贤")?"fx":"pudong";
        	school.setArea(area);
        	this.insert(school);
        }
//        msg =(msg=="")?"成功导入":msg;
//        return msg;
    }
    
    
    /**
     * 根据schoolId和classId生成二维码文件
     * @param redirectUrl
     * @param school
     * @param rps
     * @return
     */
    public File QrcodeOne(String redirectUrl,School school,RepairClass rps) {
        String rul=redirectUrl+"schoolId="+school.getId()+"&classId="+rps.getId();
//        String url1= WxMpConfiguration.getMpServices().get(appid)
//                .oauth2buildAuthorizationUrl(rul,WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
        String filepath=dir+qrpath+school.getName()+"/";
        File file=null;
        try {
			 file= QRCodeUtil.encode(rul, "d:/demo/2.jpg", filepath, true,rps.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}  
    	return file;
    }
    
    /**
     * 生成二维码压缩包
     * @param files
     * @param name
     * @return
     */
    public File GetQrZip(List<File> files,String name) {
    		String filepath=dir+qrpath;
    		String filename=filepath+name+ZipCompress.getZipFilename();
    		File zipfile = new File(filename);
    		File f=ZipCompress.zipFile2(files,zipfile);
    		return f;
    }
    
}
