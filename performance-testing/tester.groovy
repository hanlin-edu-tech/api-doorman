#! /usr/bin/env groovy

import groovyx.gpars.GParsPool

def message = '{"user":"tester","message":"HELLO"}'

def results
GParsPool.withPool(64) {
    results = (1..10240).collectParallel {
        def startTime = System.currentTimeMillis()
        def postConnection = null
        try {
            postConnection = new URL('http://localhost:8080/hello').openConnection()
            postConnection.requestMethod = 'POST'
            postConnection.setRequestProperty('Content-Type', 'application/json')
            postConnection.doOutput = true
            def stream = postConnection.getOutputStream()
            stream.write(message.getBytes("UTF-8"))
            stream.flush()
            if (postConnection.responseCode == 200) {
                return [true, System.currentTimeMillis() - startTime]
            } else {
                return [false, System.currentTimeMillis() - startTime]
            }
        } catch (e) {
            return [false, System.currentTimeMillis() - startTime]
        } finally {
            postConnection.disconnect()
        }
    }
}

def calcStdev = (values, avg) -> {
    if(values.size() == 0) {
        return 0.0
    }
    return Math.sqrt(values.collect { Math.pow((it[1] - avg), 2) }.sum() / (values.size() - 1))
}

def report = (values, title) -> {
    if(values.size() == 0) {
        println("${title}: 0 Avg: 0 Stdev: 0")
        return [0, 0.0, 0.0]
    }
    def num = values.size()
    def avg = values.collect { it[1] }.average()
    def stdev = calcStdev(values, avg)
    println("${title}: ${num} Avg: ${avg} Stdev: ${stdev}")
    return [num, avg, stdev]
}

def successReport = report(results.findAll { it[0] == true }, 'Success')
report(results.findAll { it[0] == true && it[1] > successReport[1] + (successReport[2] * 2) }, 'Slow')
report(results.findAll { it[0] == false }, 'Failure')
