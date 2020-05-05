package moe.sunjiao.osmunda.model

/**
 * choose which writer to use, full-sql, simple-sql or realm (in development)
 * created on 5/3/2020.
 *
 * @author Sun Jiao(孙娇）
 */

enum class WriterType {
    SQLITE_WRITER,
    SIMPLE_SQL_WRITER,
    REALM_WRITER
}