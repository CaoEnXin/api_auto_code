package com.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lemon.base.BaseCase;
import com.lemon.data.GlobalEnvironment;
import com.lemon.pojo.CaseInfo;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class InvestTest extends BaseCase {

    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(6);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);

        //GlobalEnvironment.PreAmount=GetInvestAmount(caseInfoList.get);
    }

    @Test(dataProvider = "getInvestDatas")
    public void testInvest(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {

        if(caseInfo.getCaseId()==1) {
            //System.out.println("member_id2:"+GlobalEnvironment.envData.get("member_id2").toString());
            int member_id2=Integer.valueOf(GlobalEnvironment.envData.get("member_id2").toString());
            GlobalEnvironment.PreAmount=GetLeaveAmount(member_id2);
            System.out.println("请求前可用余额="+GlobalEnvironment.PreAmount);
        }


        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        //System.out.println("项目ID1="+com.lemon.data.GlobalEnvironment.envData.get("Loan_ID1"));
        //System.out.println("@@@审核项目输入参数="+caseInfo.getInputParams());
        //发起接口请求
        Response res =
                given().log().all().
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                        when().
                        post(caseInfo.getUrl()).
                        //patch(caseInfo.getUrl()).
                        then().log().all().
                        extract().response();
        Allure.addAttachment("接口请求响应信息",new FileInputStream(logFilePath));
        //断言
        assertExpected(caseInfo,res);
        //数据库断言
        assertSQL(caseInfo);



        //正向用例需要验证用户余额
        if(caseInfo.getCaseId()==1) {
            //验证可用余额
            BigDecimal amount = new BigDecimal(res.path("data.amount").toString());
            System.out.println("投资金额=" + amount);
            int member_id2 = Integer.valueOf(GlobalEnvironment.envData.get("member_id2").toString());
            BigDecimal ActualAmount = GetLeaveAmount(member_id2);
            System.out.println("实际可用余额=" + ActualAmount);
            BigDecimal ExpectedAmount = GlobalEnvironment.PreAmount.subtract(amount);
            System.out.println("期望可用余额=" + ExpectedAmount);
            Assert.assertEquals(ActualAmount, ExpectedAmount);
        }


    }

    @DataProvider
    public Object[] getInvestDatas(){
        return caseInfoList.toArray();
    }

    public static void main(String[] args) {
        String fresult="{\"code\":1,\"msg\":\"密码为空\",\"data\":null,\"copyright\":\"Copyright 柠檬班 ? 2017-2020 湖南省零檬信息技术有限公司 All Rights Reserved\"}";
        String preResult="{\"code\":1,\"msg\":\"密码为空\",\"data\":null,\"copyright\":\"Copyright 柠檬班 ? 2017-2020 湖南省零檬信息技术有限公司 All Rights Reserved\"}";
        String ActualResult=fresult.split(",")[1];
        String ExpectedResult=preResult.split(",")[1];
        //验证预期结果和实际结果的MSG是否一致
        if(ExpectedResult.contains(ActualResult)) {
            System.out.println("结果一致");
        }else {
            System.out.println("结果不一致");
        }
    }

}
