package web.dispatch;

import app.handler.RegisterHandlerImpl;
import config.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.dispatch.adapter.DefaultHandlerAdapter;
import web.dispatch.adapter.SingleArgHandlerAdapter;
import web.handler.StaticContentHandler;
import web.handler.WebHandler;

import java.util.List;

class HandlerAdapterTest {
    AppConfig config = new AppConfig();
    List<HandlerAdapter> adapterList = config.handlerAdapterList();

    @Test
    @DisplayName("HandlerAdapter 선택 테스트: SingleArgHandler")
    void handler_adapter_list_test_single_arg(){
        WebHandler handler = new RegisterHandlerImpl();
        HandlerAdapter adapter = adapterList.stream().filter(ha -> ha.support(handler))
                .findFirst().orElse(null);
        Assertions.assertThat(adapter).isInstanceOf(SingleArgHandlerAdapter.class);
    }

    @Test
    @DisplayName("HandlerAdapter 선택 테스트: DefaultHandler")
    void handler_adapter_list_test_Default(){
        WebHandler handler = new StaticContentHandler();
        HandlerAdapter adapter = adapterList.stream().filter(ha -> ha.support(handler))
                .findFirst().orElse(null);
        Assertions.assertThat(adapter).isInstanceOf(DefaultHandlerAdapter.class);
    }
}