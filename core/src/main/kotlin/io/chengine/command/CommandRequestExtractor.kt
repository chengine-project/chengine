package io.chengine.command

import io.chengine.connector.RequestExtractor
import io.chengine.connector.BotRequestContext

/**
 * Extracts command from [BotRequestContext] if possible
 */
interface CommandRequestExtractor : RequestExtractor<Command>