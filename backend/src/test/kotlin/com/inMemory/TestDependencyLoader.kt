package com.inMemory

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(profiles = ["test"])
@SpringBootApplication
internal class TestDependencyLoader {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(TestDependencyLoader::class.java, *args)
        }
    }
}
