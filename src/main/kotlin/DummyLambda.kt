import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

data class Response(val message: String)

class DummyLambda : RequestStreamHandler {

    private val gson = Gson()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val logger = context.logger
        val proxyUrl = System.getenv("PROXY_URL")
        val waitTime = System.getenv("WAIT_TIME")

        return when {
            proxyUrl != null -> callProxy(proxyUrl, output, logger)
            waitTime != null -> doSleepAndRespond(output, waitTime.toLong(), logger)
            else -> throw IllegalStateException("Lambda is misconfigured. Missing environment variables")
        }
    }

    private fun callProxy(url: String, output: OutputStream, logger: LambdaLogger) {
        logger.log("Proxying request to $url\n")
        val stopwatch = Stopwatch()
        stopwatch.start()
        val request = Request.Builder()
            .url(url)
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string()
            OutputStreamWriter(output, Charsets.UTF_8).use { writer ->
                writer.write(body)
            }
        }
        val elapsedTime = stopwatch.elapsedMillis()
        logger.log("Call to fetch data took $elapsedTime ms")
    }

    private fun doSleepAndRespond(output: OutputStream, millis: Long, logger: LambdaLogger) {
        val elapsedTime = measureTimeMillis {
            logger.log("Sleeping for $millis ms \n")
            Thread.sleep(millis)
        }
        logger.log("Just woke up after sleeping for $elapsedTime\n ms")

        val response = Response("Hello, World!")
        val jsonResponse = gson.toJson(response)

        OutputStreamWriter(output, Charsets.UTF_8).use { writer ->
            writer.write(jsonResponse)
        }
    }

    class Stopwatch {
        private var startTime: Long = 0

        fun start() {
            startTime = System.currentTimeMillis()
        }

        fun elapsedMillis(): Long {
            return System.currentTimeMillis() - startTime
        }
    }
}
