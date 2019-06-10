package zhongchiedu.common.Excel;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

public class ExcelListener extends AnalysisEventListener {
	
	private List<Object> datas=new ArrayList<>();
	
	
	
	@Override
	public void invoke(Object object, AnalysisContext context) {
        //数据存储到list，供批量处理，或后续自己业务逻辑处理。
        datas.add(object);
        //根据业务自行 do something
        doSomething();

        /*
        如数据过大，可以进行定量分批处理
        if(datas.size()<=100){
            datas.add(object);
        }else {
            doSomething();
            datas = new ArrayList<Object>();
        }
         */

	}

    /**
     * 根据业务自行实现该方法
     */
    private void doSomething() {
    }
    
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
        /*
        		datas.clear();
        		解析结束销毁不用的资源
     */
	}
    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

}
