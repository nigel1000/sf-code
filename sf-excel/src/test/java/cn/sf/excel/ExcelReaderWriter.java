package cn.sf.excel;

import cn.sf.excel.common.ReaderSheetVO;
import cn.sf.excel.common.WriterSheetVO;
import cn.sf.excel.excp.ExcelParseException;
import cn.sf.excel.excp.ExcelParseExceptionInfo;
import cn.sf.excel.utils.ExcelUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by hznijianfeng on 2016/4/9.
 */
@Slf4j
public class ExcelReaderWriter {

    private static String path = ExcelReaderWriter.class.getResource("/").getPath();

    public static void main(String[] args) {
        System.out.println(path);
//        writerTemplateXLSExcelTest();
//        writerXLSXExcelTest();
//        writerXLSExcelTest();
        readExcelTest();
    }

    public static void writerTemplateXLSExcelTest(){
        File file = new File(path+"template-writer-test.xlsx");
        File fileImpl = new File(path+"template-writer-test-impl.xlsx");
        System.out.println(file.getAbsolutePath());
        fileImpl.delete();
        //数据准备
        WriterSheetVO o1 = new WriterSheetVO();
        o1.setAttr1("萨芬发放萨法俄啊饿疯俄国呃啊飞");
        o1.setAttr2(12);
        o1.setAttr3(22L);
        o1.setAttr4(22.22);
        o1.setDate1(new Date());
        o1.setDate2(new Date());
        WriterSheetVO o2 = new WriterSheetVO();
        o2.setAttr1("萨芬");
        o2.setAttr2(12);
        o2.setAttr3(22L);
        o2.setAttr4(22.22);
        o2.setDate1(new Date());
        o2.setDate2(new Date());
        List<WriterSheetVO> list = Lists.newArrayList(o1,o2);

        //第一张sheet
        XLSXExport xlsxExport = new XLSXExport(fileImpl.getAbsolutePath(),file.getAbsolutePath());
        xlsxExport.changeSheet("测试1");
        xlsxExport.createEXCEL(list,WriterSheetVO.class,1);
        //第二张sheet
        xlsxExport.changeSheet("测试2");
        xlsxExport.createEXCEL(list,WriterSheetVO.class,1);

        xlsxExport.exportXLS();
    }

    public static void writerXLSExcelTest() {
        File file = new File(path + "writer-test.xls");
        System.out.println(file.getAbsolutePath());
        file.delete();
        //数据准备
        WriterSheetVO o1 = new WriterSheetVO();
        o1.setAttr1("萨芬发放萨法俄啊饿疯俄国呃啊飞");
        o1.setAttr2(12);
        o1.setAttr3(22L);
        o1.setAttr4(22.22);
        o1.setDate1(new Date());
        o1.setDate2(new Date());
        WriterSheetVO o2 = new WriterSheetVO();
        o2.setAttr1("萨芬");
        o2.setAttr2(12);
        o2.setAttr3(22L);
        o2.setAttr4(22.22);
        o2.setDate1(new Date());
        o2.setDate2(new Date());
        List<WriterSheetVO> list = Lists.newArrayList(o1,o2);

        //第一张sheet
        XLSExport xlsExport = new XLSExport(file.getAbsolutePath());
        xlsExport.addSheet("测试1");
        xlsExport.createEXCEL(list,WriterSheetVO.class);
        //第二张sheet
        xlsExport.addSheet("测试2");
        xlsExport.createEXCEL(list,WriterSheetVO.class);

        xlsExport.exportXLS();
    }

    public static void writerXLSXExcelTest() {
        File file = new File(path + "writer-test.xlsx");
        System.out.println(file.getAbsolutePath());
        file.delete();
        //数据准备
        WriterSheetVO o1 = new WriterSheetVO();
        o1.setAttr1("萨芬发放萨法俄啊饿疯俄国呃啊飞");
        o1.setAttr2(12);
        o1.setAttr3(22L);
        o1.setAttr4(22.22);
        o1.setDate1(new Date());
        o1.setDate2(new Date());
        WriterSheetVO o2 = new WriterSheetVO();
        o2.setAttr1("萨芬");
        o2.setAttr2(12);
        o2.setAttr3(22L);
        o2.setAttr4(22.22);
        o2.setDate1(new Date());
        o2.setDate2(new Date());
        List<WriterSheetVO> list = Lists.newArrayList(o1,o2);

        //第一张sheet
        XLSXExport xlsxExport = new XLSXExport(file.getAbsolutePath());
        xlsxExport.addSheet("测试1");
        xlsxExport.createEXCEL(list,WriterSheetVO.class);
        //第二张sheet
        xlsxExport.addSheet("测试2");
        xlsxExport.createEXCEL(list,WriterSheetVO.class);

        xlsxExport.exportXLS();
    }

    public static void readExcelTest() {
        // 验证错误信息采集，保存错误采集
        String filePath = path + "reader-test.xlsx";
        System.out.println(filePath);
        StringBuilder sb = new StringBuilder();
        String sheetName = "测试";
        Workbook book =  ExcelUtils.createWorkBook(filePath);
        ExcelParse<ReaderSheetVO> excelParse = new ExcelParse<>();
        try {
            List<ReaderSheetVO> retList = excelParse.getList(book,sheetName,1,ReaderSheetVO.class);
            System.out.println(retList);
        } catch (ExcelParseException e) {
            List<ExcelParseExceptionInfo> errInfos = e.getInfoList();
            if (errInfos != null && errInfos.size() > 0) {
                for (ExcelParseExceptionInfo errInfo : errInfos) {
                    sb.append(errInfo.getSheetName())
                            .append( "第" + errInfo.getRowNum()).append("行").append("，")
                            .append("字段“").append(errInfo.getColumnName()).append("”").append("，")
                            .append(errInfo.getErrMsg()).append(";");
                }
            }
        }
        System.out.println(sb.toString());
    }
}
