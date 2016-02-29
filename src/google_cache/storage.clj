(ns google-cache.cache
  (:require [taoensso.carmine :as car :refer (wcar)]
            [clojure.core.cache       :as cache]))

(def connection {:pool {} :spec {}})
(defmacro wcar* [& body] `(car/wcar connection ~@body))

(defprotocol Storage
  (store [key results])
  (lookup [key]))

(deftype STMStorage [storage]
  Storage
  (store [key results]  )
  (lookup [key] (key storage)))

(deftype RedisStorage [redis]
  Storage
  (store [key results] (car/set key results))
  (lookup [key] (car/get "foo")))
