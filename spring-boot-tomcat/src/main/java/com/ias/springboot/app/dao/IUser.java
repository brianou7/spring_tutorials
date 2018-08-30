package com.ias.springboot.app.dao;

import org.springframework.data.repository.CrudRepository;

import com.ias.springboot.app.models.User;

public interface IUser extends CrudRepository<User, Long> {

	public User findByUsername(String username);
}
