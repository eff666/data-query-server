package com.shuyun.query.exceptions;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.log4j.Logger;

public class FileErrorListener extends BaseErrorListener {
    public static final FileErrorListener INSTANCE = new FileErrorListener();

    private static final Logger logger = Logger.getLogger(FileErrorListener.class);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
            RecognitionException e) {
        logger.error("line " + line + ":" + charPositionInLine + " " + msg);
    }

}
