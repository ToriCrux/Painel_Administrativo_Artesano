package com.sistema.autenticacao_service.infra.mongo;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserLogRepository extends MongoRepository<UserLog, String> {}
