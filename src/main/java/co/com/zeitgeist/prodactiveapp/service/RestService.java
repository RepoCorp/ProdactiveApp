package co.com.zeitgeist.prodactiveapp.service;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by D on 23/08/2014.
 */
public class RestService<T> {

    public T Send(String url,T elem){

           RestTemplate restTemplate = new RestTemplate();
           restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
           return (T) restTemplate.getForObject(url, elem.getClass());

    }

}
