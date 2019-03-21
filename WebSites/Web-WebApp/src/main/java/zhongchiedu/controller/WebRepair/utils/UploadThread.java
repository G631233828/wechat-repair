package zhongchiedu.controller.WebRepair.utils;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import zhongchiedu.WxRepair.pojo.RepairMsg;
import zhongchiedu.WxRepair.service.Impl.RepairMsgServiceImpl;
import zhongchiedu.general.pojo.MultiMedia;
import zhongchiedu.general.service.Impl.MultiMediaServiceImpl;

public class UploadThread  implements Runnable{

	@Override
	public void run() {
		
	}

	private MultiMediaServiceImpl  multiMediaService;
	private RepairMsgServiceImpl repairMsgService;
	
	public UploadThread(MultiMediaServiceImpl  multiMediaService,
			RepairMsgServiceImpl repairMsgService) {
		this.multiMediaService=multiMediaService;
		this.repairMsgService=repairMsgService;
	}
	
	private void upload(MultipartFile[] file,String dir,String path,String belong,RepairMsg msg) {
			 List<MultiMedia> lists=multiMediaService.uploadPictures(file, dir, path, belong);
			 msg.setPictureofresults(lists);
			 this.repairMsgService.save(msg);
	}
		
	
}
