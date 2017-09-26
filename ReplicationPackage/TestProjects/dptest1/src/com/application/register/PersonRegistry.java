/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.application.register;

import java.util.ArrayList;
import java.util.List;

import com.application.entity.Person;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class PersonRegistry {
    
    private final List<Person> persons = new ArrayList<>();
    
    public int getPersonSize() {
        return persons.size();
    }
    
    public void addPerson(Person person) {
        persons.add(person);
    }
    
    public void printDetails() {
    	System.out.println(persons.toString());
    }
    
}
