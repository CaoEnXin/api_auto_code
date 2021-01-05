package com.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.BaseCase;
import com.lemon.data.Constants;
import com.lemon.pojo.CaseInfo;
import com.test.day03.GlobalEnvironment;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class AddLoanTest extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(4);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getAddLoanDatas")
    public void testAddLoan(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        //发起接口请求
        Response res =
                given().log().all().
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                when().
                        post(caseInfo.getUrl()).
                then().log().all().
                        extract().response();
        Allure.addAttachment("接口请求响应信息",new FileInputStream(logFilePath));

        System.out.println("@@@添加项目响应报文="+res.path("data"));
        System.out.println("@@@添加项目响应报文=>项目ID="+res.path("data.id"));
        if(res.path("data.id") != null) {
            //System.out.println("@@@项目ID不为空@@@");
            if(caseInfo.getCaseId()==1) {
                com.lemon.data.GlobalEnvironment.envData.put("Loan_ID1", res.path("data.id"));
            }else if(caseInfo.getCaseId()==2){
                com.lemon.data.GlobalEnvironment.envData.put("Loan_ID2", res.path("data.id"));
            }else if(caseInfo.getCaseId()==3){
                com.lemon.data.GlobalEnvironment.envData.put("Loan_ID3", res.path("data.id"));
            }else if(caseInfo.getCaseId()==4){
                com.lemon.data.GlobalEnvironment.envData.put("Loan_ID4", res.path("data.id"));
            }
        }
        System.out.println("环境变量=》项目ID1="+com.lemon.data.GlobalEnvironment.envData.get("Loan_ID1"));

        //断言
        assertExpected(caseInfo,res);
        //获取项目id
        //保存到环境变量中
        //System.out.println("@@@添加项目响应报文="+res.path("data"));
        //if(res.path("data.id") != null) {
        //     GlobalEnvironment.envData.put("loan_id", res.path("data.id"));
        //}
        System.out.println("环境变量=》项目ID1="+GlobalEnvironment.envData.get("Loan_ID1"));
    }

    @DataProvider
    public Object[] getAddLoanDatas(){
        return caseInfoList.toArray();
    }
}
