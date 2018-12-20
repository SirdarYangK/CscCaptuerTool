package com.csc.capturetool.myapplication.modules.action.model;

import java.util.List;

/**
 * Created by SirdarYangK on 2018/11/14
 * des:
 */
public class GcdecodeBean {
    /**
     * ver : 01
     * send : ff
     * receive : 01
     * deviceid : c987a7540200
     * timespan : 1542194165
     * radom : 17710
     * cmd : 40080102000000000000
     * cmds : [{"cmdname":"启动 按摩椅 应答","cmdid":"40","cmdlen":"08","cmdctx":"0102000000000000","cmdlen_i10":8,"cmdctx_i10":513}]
     */

    private String ver;
    private String send;
    private String receive;
    private String deviceid;
    private int timespan;
    private int radom;
    private String cmd;
    private List<CmdsBean> cmds;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public int getTimespan() {
        return timespan;
    }

    public void setTimespan(int timespan) {
        this.timespan = timespan;
    }

    public int getRadom() {
        return radom;
    }

    public void setRadom(int radom) {
        this.radom = radom;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public List<CmdsBean> getCmds() {
        return cmds;
    }

    public void setCmds(List<CmdsBean> cmds) {
        this.cmds = cmds;
    }

    public static class CmdsBean {
        /**
         * cmdname : 启动 按摩椅 应答
         * cmdid : 40
         * cmdlen : 08
         * cmdctx : 0102000000000000
         * cmdlen_i10 : 8
         * cmdctx_i10 : 513
         */

        private String cmdname;
        private String cmdid;
        private String cmdlen;
        private String cmdctx;
        private int cmdlen_i10;
        private int cmdctx_i10;

        public String getCmdname() {
            return cmdname;
        }

        public void setCmdname(String cmdname) {
            this.cmdname = cmdname;
        }

        public String getCmdid() {
            return cmdid;
        }

        public void setCmdid(String cmdid) {
            this.cmdid = cmdid;
        }

        public String getCmdlen() {
            return cmdlen;
        }

        public void setCmdlen(String cmdlen) {
            this.cmdlen = cmdlen;
        }

        public String getCmdctx() {
            return cmdctx;
        }

        public void setCmdctx(String cmdctx) {
            this.cmdctx = cmdctx;
        }

        public int getCmdlen_i10() {
            return cmdlen_i10;
        }

        public void setCmdlen_i10(int cmdlen_i10) {
            this.cmdlen_i10 = cmdlen_i10;
        }

        public int getCmdctx_i10() {
            return cmdctx_i10;
        }

        public void setCmdctx_i10(int cmdctx_i10) {
            this.cmdctx_i10 = cmdctx_i10;
        }

        @Override
        public String toString() {
            return "CmdsBean{" +
                    "cmdname='" + cmdname + '\'' +
                    ", cmdid='" + cmdid + '\'' +
                    ", cmdlen='" + cmdlen + '\'' +
                    ", cmdctx='" + cmdctx + '\'' +
                    ", cmdlen_i10=" + cmdlen_i10 +
                    ", cmdctx_i10=" + cmdctx_i10 +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GcdecodeBean{" +
                "ver='" + ver + '\'' +
                ", send='" + send + '\'' +
                ", receive='" + receive + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", timespan=" + timespan +
                ", radom=" + radom +
                ", cmd='" + cmd + '\'' +
                ", cmds=" + cmds +
                '}';
    }
}
