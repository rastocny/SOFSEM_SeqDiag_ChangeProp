/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.application.manager;

import com.application.entity.Address;
import com.application.entity.Person;
import com.application.register.PersonRegistry;

import eu.external.something.ExternalObject;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class RegisterManager {
    
    public void createRegistry() {
        Person person1 = new Person("Andrej","Mlyncar", new Address("PB", "Tatra"));
        PersonRegistry registry = new PersonRegistry();
        registry.printDetails();
        for(int i=0; i<10;i++) {
            registry.addPerson(person1);
        }
        int i;
        i = registry.getPersonSize();
        StatsManager statsManager = new StatsManager(registry);
    }
}
