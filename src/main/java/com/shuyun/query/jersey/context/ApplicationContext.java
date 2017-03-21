package com.shuyun.query.jersey.context;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.core.Application;

import akka.actor.ActorSystem;
import akka.routing.DefaultResizer;
import akka.routing.Resizer;
import akka.routing.RoundRobinRouter;
import com.shuyun.query.akka.QueryActor;
import com.shuyun.query.meta.*;
import com.shuyun.query.queue.QueryExecuteThreadPool;
import com.shuyun.query.util.LoadBalanceUtil;
import org.apache.log4j.Logger;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.jackson.JacksonFeature;
import scala.concurrent.duration.Duration;


public class ApplicationContext extends Application {
    private static Logger logger = Logger.getLogger(ApplicationContext.class);
    private ActorSystem system;
    static QueryConfig cfg;

    @Inject
    public ApplicationContext(ServiceLocator serviceLocator) {

        cfg = QueryConfig.getInstance();

        system = ActorSystem.create("QuerySystem");
        Resizer resizer = new DefaultResizer(cfg.getLowerBound(),
                cfg.getUpperBound());

        // assign QueryRouter's actor as QueryActor class
        system.actorOf(
                QueryActor.mkProps().withRouter(new RoundRobinRouter(resizer)),
                "QueryRouter");

        DynamicConfiguration dc = Injections.getConfiguration(serviceLocator);
        Injections.addBinding(Injections.newBinder(system)
                .to(ActorSystem.class), dc);
        Injections.addBinding(Injections.newBinder(cfg).to(QueryConfig.class),
                dc);
        dc.commit();

        LoadBalanceUtil.produceMoreNodes(EsQueryConf.getInstance().getElasticSearchUrl());

        ShuyunQueryConf.getInstance().getSize();
        PermissionConf.getInstance().getUserId();
        MemberRfmConf.getInstance().getElasticSearchUrl();
        //EsColumnConf.getEsColumn();
    }


    @PostConstruct
    private void init() {

        // init broker context
        new Timer().schedule(new TimerTask() {
            public void run() {
                QueryExecuteThreadPool.initCustomerPool(); // 定时唤醒 查询执行线程池
            }
        }, 11, 10);

    }

    @PreDestroy
    private void shutdown() {
        system.shutdown();
        system.awaitTermination(Duration.create(15, TimeUnit.SECONDS));
    }

    /**
     * return the web services classes
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        classSet.add(OkService.class);
        // classSet.add(OpsService.class);
//        classSet.add(QueryService.class);
        classSet.add(SearchService.class);
//        classSet.add(SqlService.class);
//        classSet.add(UpdateService.class);
        classSet.add(TagService.class);
        classSet.add(ShuyunQueryService.class);
        classSet.add(AuthenticationService.class);
        classSet.add(AddressService.class);

        return classSet;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> s = new HashSet<Object>();
        s.add(JacksonFeature.class);
        return s;
    }
}

