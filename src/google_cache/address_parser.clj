(ns google-cache.address-parser
  (:require [clojure.string :as str :refer [split trim join lower-case]]))

(defn parse [address parser]
  (parser address))

(defn base-parser [address] address)

(defn simple-parse [address]
  (->> (-> address
           (lower-case)
           (str/replace "," " ")
           (trim)
           (split #"\s+")
           (sort))
       (join " ")))

; Next idea is to use normalization:
; ""ул. Седова" => "улица Седова", "8к1" => "8 корпус 1" or vise versa.
