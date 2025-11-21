package grpc.demo

import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@GrpcService
class UserServiceImpl : UserServiceGrpc.UserServiceImplBase() {
    private val users = ConcurrentHashMap<String, User>()

    override fun createUser(request: CreateUserRequest, responseObserver: StreamObserver<CreateUserResponse>) {
        val newUser = User.newBuilder()
            .setUserId(UUID.randomUUID().toString())
            .setName(request.name)
            .setEmail(request.email)
            .build()

        users[newUser.userId] = newUser

        println("Создан пользователь: ${newUser.name}")

        val response = CreateUserResponse.newBuilder().setUser(newUser).build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getUserBadges(
        request: GetUserBadgesRequest,
        responseObserver: StreamObserver<GetUserBadgesResponse>
    ) {
        val userId = request.userId

        println("Запрошены достижения для пользователя: $userId")
        val badge1 = Badge.newBuilder()
            .setBadgeId("b1")
            .setName("Первый шаг")
            .setDescription("Успешно создан аккаунт")
            .build()

        val badge2 = Badge.newBuilder()
            .setBadgeId("b2")
            .setName("Любопытный")
            .setDescription("Запросил свои достижения")
            .build()

        val response = GetUserBadgesResponse.newBuilder().addAllBadges(listOf(badge1, badge2)).build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun updateUser(request: UpdateUserRequest, responseObserver: StreamObserver<UpdateUserResponse>) {
        val user = findUser(request.userId) ?: return

        val updatedUser = User.newBuilder()
            .setUserId(user.userId)
            .setName(request.name)
            .setEmail(request.email)
            .build()

        users[updatedUser.userId] = updatedUser

        val response = UpdateUserResponse.newBuilder()
            .setUser(updatedUser)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    private fun findUser(id: String): User? = users[id]
}
