package com.fr.design.mainframe.loghandler.socketio;

import com.fr.design.mainframe.loghandler.DesignerLogHandler;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.LogRecordTime;
import com.fr.general.LogUtils;
import com.fr.log.FineLoggerFactory;
import com.fr.third.guava.base.Optional;
import com.fr.workspace.WorkContext;
import com.fr.workspace.Workspace;
import com.fr.workspace.WorkspaceEvent;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.io.ByteArrayInputStream;

public class DesignerSocketIO {

    private static Optional<Socket> socketIO = Optional.absent();

    private static final Emitter.Listener printLog = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            try {
                LogRecordTime[] logRecordTimes = LogUtils.readXMLLogRecords(new ByteArrayInputStream((byte[]) objects[0]));
                for (LogRecordTime logRecordTime : logRecordTimes) {
                    DesignerLogHandler.getInstance().printRemoteLog(logRecordTime);
                }
            } catch (Exception e) {
                FineLoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    };

    static {
        EventDispatcher.listen(WorkspaceEvent.BeforeSwitch, new Listener<Workspace>() {
            @Override
            public void on(Event event, Workspace param) {
                if (socketIO.isPresent()) {
                    socketIO.get().close();
                    socketIO = Optional.absent();
                }
            }
        });
        EventDispatcher.listen(WorkspaceEvent.AfterSwitch, new Listener<Workspace>() {
            @Override
            public void on(Event event, Workspace param) {
                updateSocket();
            }
        });
    }

    public static void init() {
        updateSocket();
    }

    private static void updateSocket() {
    
        if (WorkContext.getCurrent().isLocal()) {
            return;
        }
        try {
//            RemoteEnvConfig config = ((RemoteEnv)env).getEnvConfig();
//            String uri = String.format("http://%s:%s%s?%s=%s",
//                    config.getHost(),
//                    WebSocketConfig.getInstance().getPort(),
//                    EnvConstants.WS_NAMESPACE,
//                    DecisionServiceConstants.WEB_SOCKET_TOKEN_NAME,
//                    EnvContext.currentToken());
//
//            socketIO = Optional.of(IO.socket(new URI(uri)));
//            socketIO.get().on(EnvConstants.WS_LOGRECORD, printLog);
//            socketIO.get().on(EnvConstants.CONFIG, new Emitter.Listener() {
//                @Override
//                public void call(Object... objects) {
//                    if (objects == null || objects.length != 1) {
//                        throw new IllegalArgumentException("config should have only one param");
//                    }
//                    Object param = objects[0];
//                    if (param instanceof Class) {
//                        EventDispatcher.fire(ConfigEvent.EDIT, (Class<? extends Configuration>) param);
//                    }
//                }
//            });
            socketIO.get().connect();
        } catch (Exception e) {
            FineLoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }
}