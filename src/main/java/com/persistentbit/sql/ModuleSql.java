package com.persistentbit.sql;

import com.persistentbit.core.ModuleCore;
import com.persistentbit.core.logging.printing.LogFormatter;
import com.persistentbit.core.logging.printing.LogPrint;
import com.persistentbit.core.logging.printing.LogPrintStream;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public class ModuleSql{

	public static LogFormatter createLogFormatter(boolean inColor) {
		return ModuleCore.createLogFormatter(inColor);
	}

	public static LogPrint logPrint = LogPrintStream.sysOut(createLogFormatter(true));
}
