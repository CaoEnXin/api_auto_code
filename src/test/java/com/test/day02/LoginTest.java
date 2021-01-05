package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class LoginTest {

    @Test(dataProvider = "getLoginDatas02")
    public void testLogin01(CaseInfo caseInfo){
        //String jsonStr = "{\"mobile_phone\":\"13323231011\",\"pwd\":\"12345678\"}";
        String requestHeader = caseInfo.getRequestHeader();
        System.out.println("请求头："+requestHeader);
        System.out.println("请求头："+caseInfo.getInputParams());
        //字符串请求头转换成Map -- 下节课讲
        given().
              //  header("Content-Type",header1).
              //  header("X-Lemonban-Media-Type",header2).
                body(caseInfo.getInputParams()).
        when().
                post(caseInfo.getUrl()).
        then().
                log().body();
    }
    @DataProvider
    public Object[] getLoginDatas02(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //从Excel读取所有的用例数据
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);
        File excelFile = new File("src\\test\\resources\\api_testcases_futureloan_v1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile,CaseInfo.class,importParams);
        //怎么list集合转换为Object[][]或者Object[]？？？
        Object[] datas = list.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象
        return datas;
    }

    @DataProvider
    public Object[][] getLoginDatas01(){
        //1、请求接口地址 2、请求方式 3、请求头 4、请求数据
        Object[][] datas= {{"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"13323231011\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"133232310112\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"1332323101a\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\":\"11015541764\",\"pwd\":\"12345678\"}"}
        };
        return datas;
    }



    public static void main(String[] args) {
        //读取Excel测试用例数据？？poi麻烦/笨重
        //推荐读取Excel技术：EasyPOI
        //第一个参数：File对象 第二个参数：映射的实体类.class  第三个参数：读取配置对象
        ImportParams importParams = new ImportParams();
        //sheet索引 ，默认的起始值为0
        importParams.setStartSheetIndex(0);
        //要读取的sheet数量， 默认为1
        importParams.setSheetNum(2);
        File excelFile = new File("src\\test\\resources\\api_testcases_futureloan_v1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile,CaseInfo.class,importParams);
        for (CaseInfo caseInfo:list){
            System.out.println(caseInfo);
        }
    }
}
