/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.PersonDAL;
import entity.Person;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author Filat
 */

public class PersonLogic extends GenericLogic<Person, PersonDAL> {

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String BIRTH = "birth";
    public static final String ID = "id";
    
    
    PersonLogic() {
        super( new PersonDAL() );
    }
   
     @Override
    public List<Person> getAll() {
        return get(() -> dal().findAll());

    }

    @Override
    public Person getWithId(int id) {
        return get(() -> dal().findById(id));
    }
    
    public List<Person> getPersonWithPhone(String phone) {
        return get(() -> dal().findByPhone(phone));
        
    }
    public List<Person> getPersonWithFirstName(String firstName) {
        return get(() -> dal().findByFirstName(firstName));
        
    }
    
    public List<Person> getPersonWithLastName(String lastName) {
        return get(() -> dal().findByLastName(lastName));
        
    }
    
    public List<Person> getPersonsWithAddress(String address) {
        return get(() -> dal().findByAddress(address));
        
    }
    
     public List<Person> getPersonsWithBirth(Date birth) {
        return get(() -> dal().findByBirth(birth));
        
    }
    
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID","First Name", "Last Name", "Phone", "Address", "Birth");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, BIRTH);
    }

    @Override
    public List<?> extractDataAsList(Person e) {
        return Arrays.asList(e.getId(),e.getFirstName(), e.getLastName(), e.getPhone(), e.getAddress(), e.getBirth());

    }
    
    @Override
    public Person createEntity( Map<String, String[]> parameterMap ) {
        //do not create any logic classes in this method.

//        return new AccountBuilder().SetData( parameterMap ).build();
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
       Person entity = new Person();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if( parameterMap.containsKey( ID ) ){
            try {
                entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
            } catch( java.lang.NumberFormatException ex ) {
                throw new ValidationException( ex );
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
       ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String personInfo = null;
        if( parameterMap.containsKey( FIRST_NAME ) ){
            personInfo = parameterMap.get( FIRST_NAME )[ 0 ];
            validator.accept( personInfo, 50 );
        }
        String lastName = parameterMap.get( LAST_NAME )[ 0 ];
        String phone = parameterMap.get( PHONE  )[ 0 ];
        String address = parameterMap.get( ADDRESS )[ 0 ];
        String birth = parameterMap.get( BIRTH )[ 0 ];
            
      
        
        //validate the data
        validator.accept(lastName, 50 );
        validator.accept(phone , 15 );
        validator.accept(address, 100 );
//        validator.accept(birth, 50);
        //before converting date, replace the T in date with Space
       String str1 = birth.replace("T", " ");
        Date birthToDate  = convertStringToDate(str1);
        
        //set values on entity
        entity.setFirstName(personInfo);
        entity.setLastName(lastName );
        entity.setPhone(phone);
        entity.setAddress(address);
        entity.setBirth(birthToDate);

        return entity;
    }



}
