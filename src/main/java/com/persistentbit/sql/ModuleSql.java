package com.persistentbit.sql;

import com.persistentbit.core.ModuleCore;
import com.persistentbit.core.logging.printing.LogPrinter;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public class ModuleSql{

	public static LogPrinter createLogPrinter(boolean inColor) {
		return ModuleCore.createLogPrinter(inColor);
	}
}
