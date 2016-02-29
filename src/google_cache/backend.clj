(ns google-cache.backend
  (:require [google-cache.address-parser :as parser]
            [google-cache.cacher         :as cache]
            [google-cache.util           :as util :refer [now elapsed-milliseconds]]
            [org.httpkit.client          :as http]
            [clojure.core.async          :as async :refer [<! >! go chan close! alt! go-loop timeout put!]]))

(def empty-response "{\"results\":[],\"status\":\"ZERO_RESULTS\"}")
(def request-timeout 2000)
(defonce ^:private url (str (System/getenv "CACHER_HOST") "/maps/api/geocode/json"))
(defonce ^:private parser   parser/base-parser)

(defn request [address timeout']
  (let [ch (timeout timeout')]
    (http/get url {:timeout timeout' :query-params {:address address}}
              (fn [{:keys [body error status]}]
                (put! ch (if (or error (not= status 200)) :retry body))))
    ch))

(defn search [address]
  (let [ch (chan) start now]
    (go-loop [timeout' request-timeout]
      (let [result (<! (request address timeout'))]
        (condp = result
          nil (>! ch "")
          :retry (recur (- request-timeout (elapsed-milliseconds start)))
          (>! ch result))))
    ch))

(defn lookup [address]
  (let [key (parser/parse address parser)]
    (if (cache/has? key)
      (cache/lookup key)
      (let [ch (chan)]
        (go
          (let [result (<! (search address))]
            (if (empty? result)
              (>! ch empty-response)
              (do
                (cache/store key result)
                (>! ch result))))
         (close! ch))
        ch))))
