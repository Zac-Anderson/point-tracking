package com.inMemory.user.adapters

import com.domain.user.ports.UserRepository
import com.domain.user.ports.UserRepositoryContractTest
import com.inMemory.TestDependencyLoader
import com.inMemory.user.UserCache
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles(profiles = ["test"])
@SpringBootTest(classes = [TestDependencyLoader::class])
class InMemoryUserRepositoryContractTest : UserRepositoryContractTest() {
    @Autowired
    private lateinit var userCache: UserCache

    override fun buildSubject(): UserRepository {
        userCache.clear()
        return InMemoryUserRepository(userCache)
    }
}