package com.fayupable.mailsender.repository;

import com.fayupable.mailsender.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, UUID> {
}
