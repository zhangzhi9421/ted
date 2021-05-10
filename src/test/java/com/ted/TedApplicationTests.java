package com.ted;

import com.ted.constant.LanguageConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class TedApplicationTests {

    private LanguageConstant languageConstant;

    @Autowired
    public void setLanguageConstant(LanguageConstant languageConstant) {
        this.languageConstant = languageConstant;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void langText(){
        log.error("languageConstant---" + languageConstant);
    }

}
