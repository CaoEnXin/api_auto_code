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

public class AuditTest extends BaseCase{

    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        System.out.println("@@@@@@环境变量=》项目ID1="+com.lemon.data.GlobalEnvironment.envData.get("Loan_ID1"));
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(5);
        System.out.println("@@@@@@环境变量=》项目ID1="+com.lemon.data.GlobalEnvironment.envData.get("Loan_ID1"));
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getAuditDatas")
    public void testAudit(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        System.out.println("项目ID1="+com.lemon.data.GlobalEnvironment.envData.get("Loan_ID1"));
        System.out.println("@@@审核项目输入参数="+caseInfo.getInputParams());
        //发起接口请求
        Response res =
                given().log().all().
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                        when().
                        //post(caseInfo.getUrl()).
                        patch(caseInfo.getUrl()).
                        then().log().all().
                        extract().response();
        Allure.addAttachment("接口请求响应信息",new FileInputStream(logFilePath));
        //断言
        assertExpected(caseInfo,res);

    }

    @DataProvider
    public Object[] getAuditDatas(){
        return caseInfoList.toArray();
    }

}
