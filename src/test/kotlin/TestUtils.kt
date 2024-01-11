
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import reactor.core.publisher.Mono

class TestUtils {

    companion object {
        private val tables = listOf(
           "qq"// PAYMENT_SYSTEM_REQUEST_LOG, CUSTOMERS, CARDS, TRANSACTIONS
        )

        /*fun cleanDb(dslContext: DSLContext) {
            tables.forEach {
                Mono.from(
                    dslContext.delete(it)
                ).block()
            }
        }*/

        fun createPostgreSQLContainer(): PostgreSQLContainer<*> {
            return PostgreSQLContainer("postgres:15.1-alpine")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("123456")
                .withReuse(true)
        }

        open class ContainerInitializer(private val container: PostgreSQLContainer<*>) :
            ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                val r2dbcUrl = container.jdbcUrl.replace("jdbc", "r2dbc")

                TestPropertyValues.of(
                    "spring.r2dbc.url=$r2dbcUrl",
                    "spring.r2dbc.username=${container.username}",
                    "spring.r2dbc.password=${container.password}",
                    "spring.liquibase.url=${container.jdbcUrl}",
                    "spring.liquibase.user=${container.username}",
                    "spring.liquibase.password=${container.password}",
                ).applyTo(configurableApplicationContext.environment)
            }
        }
    }
}