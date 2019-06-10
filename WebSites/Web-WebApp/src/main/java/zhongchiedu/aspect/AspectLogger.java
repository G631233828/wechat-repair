package zhongchiedu.aspect;

import java.util.Iterator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Around;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AspectLogger {

    @Around("within(zhongchiedu.controller.WebRepair.wx..*) && @annotation(aspectLog)")
    public Object Around(ProceedingJoinPoint pjp,AspectLog aspectLog)throws  Throwable{
       String methodName=pjp.getSignature().getName();
       String operationName=aspectLog.operationName();
       String operationType=aspectLog.operationType();
       Object[] args=pjp.getArgs();
       String[] paramNames=((CodeSignature)pjp.getSignature()).getParameterNames();
       String params="";
       for (int i=0;i<args.length;i++) {
    	   params+=paramNames[i]+":"+args[i]+"<-->";
       }
       log.info("操作类型是:{}, 操作说明:{},操作方法:{},参数列{}",operationType,operationName,methodName,
    		   params);
       return pjp.proceed();
    }


//    @Before("execution(*  com.zhongchi.controller.UserController.*(..))")
//    public void before(){
//       startTime=new Date().getTime();
//    }
    
//    @After("execution(*  com.zhongchi.controller.UserController.*(..))")
//    public  void after(JoinPoint joinPoint){
//           String targetName=joinPoint.getTarget().getClass().getName();
//           String methodName=joinPoint.getSignature().getName();
////           Object[]  objs=joinPoint.getArgs();
//           Class targetClass=null;
//           Class targetClass02=joinPoint.getSignature().getDeclaringType();
//           try {
//                 targetClass=Class.forName(targetName);
//               } catch (ClassNotFoundException e) {
//                   e.printStackTrace();
//               }
//           Method[] methods=targetClass.getMethods();
//           Optional optional =Arrays.stream(methods).filter(method -> method.getName().equals(methodName)).findAny();
//           Object object=optional.orElse("There is no this method!");
//           if(object instanceof Method){
//               String operationName=((Method)object).getAnnotation(AspectLog.class).operationName();
//               String operationType=((Method)object).getAnnotation(AspectLog.class).operationType();
//               log.info("操作类型是:{}, 操作说明:{}",operationType,operationName);
//           }else{
//               System.out.println(object);
//           }
//           System.out.println(targetClass == targetClass02);
//        endTime=new Date().getTime();
//        System.out.println("Aspect  cost time is : "+(endTime-startTime));
//    }
	
	

}
