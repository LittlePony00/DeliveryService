package grpc.demo

import io.grpc.StatusRuntimeException
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class GrpcClientRunner(
    private val userServiceStub: UserServiceGrpc.UserServiceBlockingStub
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("--- Клиент gRPC запущен ---")

        try {
            println("--> Вызов CreateUser...")

            val createUserRequest = CreateUserRequest.newBuilder().setName("Студент").setEmail("student@example.com").build()
            val createUserResponse = userServiceStub.createUser(createUserRequest)

            val newUserId = createUserResponse.user.userId
            println("<-- Пользователь успешно создан с ID: $newUserId}; с email: ${createUserResponse.user.email}")

            println("\n--> Вызов GetUserBadges для пользователя $newUserId...")
            val badgesRequest = GetUserBadgesRequest.newBuilder().setUserId(newUserId).build()
            val badgesResponse = userServiceStub.getUserBadges(badgesRequest)
            println("<-- Получены достижения:")

            badgesResponse.badgesList.forEach { badge ->
                println("    - " + badge.getName() + " (" + badge.getDescription() + ")")
            }

        } catch (e: StatusRuntimeException) {
            System.err.println("!!! Ошибка при вызове gRPC: ${e.status}")
        }

        println("--- Клиент gRPC завершил работу ---")
    }
}