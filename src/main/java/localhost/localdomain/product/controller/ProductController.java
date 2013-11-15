package localhost.localdomain.product.controller;


import localhost.localdomain.product.form.Product;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@Transactional
public class ProductController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "*", method = RequestMethod.GET)
    public String listProducts(Map<String, Object> map) {

        List<Product> productlist = sessionFactory.getCurrentSession().createQuery("from Product").list();

        map.put("product", new Product());
        map.put("productlist", productlist);

        return "index";
    }
}
