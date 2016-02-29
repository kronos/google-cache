(ns google-cache.cacher
  (:require [immutant.caching :as c]))

(defonce cache (c/cache "google-cache"))

(defn has?   [key'] (contains? cache key'))
(defn store  [key' value] (.putIfAbsent cache key' value))
(defn lookup [key'] (get cache key'))
