package com.shops.chiggys.build.configure;

/**
 * Created by Tamil on 9/22/2017.
 */

public class BuildConfigure {

    /*  Live Mode*/
//    public static String BASE_URL = "http://shops.appoets.co/";
//    public static String CLIENT_SECRET = "D05WfB9aCBPCel6St5lOl2Cc1hqBwYoudmqxX7Ti";
//    public static final String PUBNUB_PUBLISH_KEY = "pub-c-7bce9b6e-c9ec-44ad-98c2-35eafbbcacb1";
//    public static final String PUBNUB_SUBSCRIBE_KEY = "sub-c-046e0baa-bb01-11e7-b0ca-ee8767eb9c7d";

    /*   Dev Mode*/
    public static String BASE_URL = "http://139.59.69.151/";
    public static String CLIENT_SECRET = "MQtGnp3FWXA730OXr18vXFwzUbDOTdlO1P8NFUXD";
    public static String CLIENT_ID = "2";

    //Pubnub for Chat
    public static  String PUBNUB_PUBLISH_KEY = "pub-c-c04f6576-5244-4549-a81f-fecd6026c922";
    public static  String PUBNUB_SUBSCRIBE_KEY = "sub-c-a0dda69c-a4b3-11e9-afe0-966e26ff0ecc";
    public static   String PUBNUB_CHANNEL_NAME = "21";

    //Stripe for card payment
    public static String STRIPE_PK = "pk_test_39kly6aEfUEfvMpRnN6BnxLb";

}
