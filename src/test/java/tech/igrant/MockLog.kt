package tech.igrant

import org.apache.maven.plugin.logging.Log

class MockLog : Log {
    override fun warn(content: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun warn(content: CharSequence?, error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun warn(error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun info(content: CharSequence?) {
        println(content)
    }

    override fun info(content: CharSequence?, error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun info(error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isInfoEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isErrorEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isWarnEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun error(content: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun error(content: CharSequence?, error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun error(error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isDebugEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun debug(content: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun debug(content: CharSequence?, error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun debug(error: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}