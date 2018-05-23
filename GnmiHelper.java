package com.alibaba.ais.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gnmi.Gnmi.GetRequest;
import gnmi.Gnmi.Path;
import gnmi.Gnmi.Path.Builder;
import gnmi.Gnmi.PathElem;
import gnmi.Gnmi.SetRequest;
import gnmi.Gnmi.TypedValue;
import gnmi.Gnmi.Update;

/**
 * 类GnmiHelper.java的实现描述：根据https://github.com/openconfig/reference/tree/master/rpc/gnmi规范实现的Java端的工具
 * 
 * @author yongjie.wyj 2018年4月27日 下午2:57:43
 */
public class GnmiHelper {

    private static String TOKEN_PARTTERN = "/";

    /**
     * 通过gNMI规范转换对应的Java实现GET实现工具
     * 
     * @param gNMIPath
     * @return
     */
    public static GetRequest buildGetRequest(String gNMIPath) {

        GetRequest request = null;
        Builder builder = getGNMIBuilder(gNMIPath);
        if(builder != null){
            request = GetRequest.newBuilder().addPath(builder.build()).build();
        }
        return request;
    }
    
    /**
     * 通过gNMI规范转换对应的Java实现SET实现工具
     * 
     * @param gNMIPath
     * @param val
     * @return
     */
    public static SetRequest buildSetRequest(String gNMIPath,String val) {

        SetRequest request = null;
        Builder builder = getGNMIBuilder(gNMIPath);
        if(builder != null){
            request = SetRequest.newBuilder().
                    addUpdate(Update.newBuilder().
                              setPath(builder.build()).
                              setVal(TypedValue.newBuilder().setStringVal(val).build())).build();
        }
        return request;
    }
    
    private static Builder getGNMIBuilder(String gNMIPath){
        
        if (!StringUtils.isBlank(gNMIPath)) {
            String[] tokens = gNMIPath.split(TOKEN_PARTTERN);
            Builder pathBuilder = Path.newBuilder();
            
            for (String token : tokens) {
                if(!StringUtils.isBlank(token)) {
                    String elem = null;
                    Map<String,String> map = new HashMap<String,String>();
                    if(token.contains("[")){
                        elem = token.substring(0, token.indexOf("["));
                        //TODO maybe has multiply key [a=b][b=c]
                        String keyPeers = token.substring(token.indexOf("[") + 1,token.indexOf("]"));
                        String[] kpArr = keyPeers.split("=");
                        map.put(kpArr[0], kpArr[1]);
                    }else{
                        elem = token;
                    }
                    gnmi.Gnmi.PathElem.Builder elemBuiler = PathElem.newBuilder().setName(elem);
                    if(!map.isEmpty()){
                        elemBuiler.putKey(map.keySet().toArray()[0].toString(), map.values().toArray()[0].toString());
                    }
                    pathBuilder.addElem(elemBuiler.build());
                }
            }
            return pathBuilder;
        }
        return null;
    }

    public static void main(String[] args) {
        buildGetRequest("openconfig-interfaces:interfaces/interface[name=Ethernet1]/state");
        System.out.println("##########another case######");
        buildGetRequest("interface/state");
    }
}
