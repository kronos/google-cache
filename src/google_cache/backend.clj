(ns google-cache.backend
  (:require [google-cache.address-parser :as parser :refer [parse]]
            [org.httpkit.client          :as http]
            [clojure.core.async          :as async :refer [<! >! go chan close! alt! go-loop timeout put!]])
  (:refer-clojure :exclude [resolve]))

(def empty-response "{\"results\":[],\"status\":\"ZERO_RESULTS\"}")
(def timeout' 2000)
(defonce ^:private url (str (System/getenv "CACHER_HOST") "/maps/api/geocode/json"))
(defonce ^:private cache (atom {}))

(defn request [address]
  "Perform an async request to backend and returns channel
   in which :error or string result will be sent"
  (let [ch (chan)]
    (http/get url {:timeout timeout' :query-params {:address address}}
              (fn [{:keys [body error status]}]
                (put! ch (if (or error (not= status 200)) :error body))))
    ch))

(defn search [address]
  "Try to search for an address fixed amount of time.
   Returns channel which will contain nil in case of timeout
   or string result"
  (let [ch (timeout timeout')]
    (go-loop []
      (let [result (<! (request address))]
        (if (= result :error) (recur) (>! ch result))))
    ch))

(defn resolve [address cb]
  (let [ch (chan)]
    (go
      (if-let [result (<! (search address))]
        (do (>! ch result) (cb result))
        (>! ch empty-response))
      (close! ch))
    ch))

(defn lookup [address]
  (let [cache-key (parse address)]
    (if-let [computed-result (@cache cache-key)]
      computed-result
      (resolve address
        (fn [r] (swap! cache assoc cache-key r))))))
