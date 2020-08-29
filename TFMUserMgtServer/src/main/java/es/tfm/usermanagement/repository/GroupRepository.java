package es.tfm.usermanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import es.tfm.usermanagement.model.UserGroup;

public interface GroupRepository extends JpaRepository<UserGroup, Long> {

	UserGroup findByGroupName(String groupName);

}
